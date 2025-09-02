package com.bvic.contacto.data.core

import com.bvic.contacto.core.Result
import com.bvic.contacto.core.network.NetworkError
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class NetworkBoundResourceTest {
    @Test
    fun `when shouldFetch is false then returns mapped local data and does not call network`() =
        runTest {
            var networkCalled = false

            val flow =
                NetworkBoundResource
                    .makeCache<List<Int>, String, Int>(
                        loadFromDb = { listOf(1, 2, 3) },
                        shouldFetch = { false },
                        fetchFromNetwork = {
                            networkCalled = true
                            Response.success("ignored")
                        },
                        saveNetworkResult = { /* not called */ },
                        mapToOutput = { local -> local.sum() },
                    ).asFlow()

            val emissions = mutableListOf<Result<Int, NetworkError>>()
            flow.toList(emissions)

            assertTrue(emissions.first() is Result.Loading)
            val success = emissions.last() as Result.Success
            assertEquals(6, success.data)
            assertTrue(!networkCalled)
        }

    @Test
    fun `when http 5xx then returns ServerError`() =
        runTest {
            val flow =
                NetworkBoundResource
                    .makeCache<List<String>, String, List<String>>(
                        loadFromDb = { emptyList() },
                        shouldFetch = { true },
                        fetchFromNetwork = {
                            val body =
                                "".toResponseBody("application/json".toMediaTypeOrNull())
                            Response.error(500, body)
                        },
                        saveNetworkResult = { },
                        mapToOutput = { it },
                    ).asFlow()

            val emissions = mutableListOf<Result<List<String>, NetworkError>>()
            flow.toList(emissions)

            assertTrue(emissions.first() is Result.Loading)
            val error = emissions.last() as Result.Error
            assertEquals(NetworkError.ServerError, error.error)
        }

    @Test
    fun `when SocketTimeoutException then returns RequestTimeout`() =
        runTest {
            val flow =
                NetworkBoundResource
                    .makeCache<Unit, String, Unit>(
                        loadFromDb = { },
                        shouldFetch = { true },
                        fetchFromNetwork = {
                            throw SocketTimeoutException("timeout")
                        },
                        saveNetworkResult = { /* not called */ },
                        mapToOutput = { it },
                    ).asFlow()

            val emissions = mutableListOf<Result<Unit, NetworkError>>()
            flow.toList(emissions)

            assertTrue(emissions.first() is Result.Loading)
            val error = emissions.last() as Result.Error
            assertEquals(NetworkError.RequestTimeout, error.error)
        }

    @Test
    fun `when JsonSyntaxException then returns Serialization`() =
        runTest {
            val flow =
                NetworkBoundResource
                    .makeCache<Unit, String, Unit>(
                        loadFromDb = { },
                        shouldFetch = { true },
                        fetchFromNetwork = {
                            throw JsonSyntaxException("bad json")
                        },
                        saveNetworkResult = { /* not called */ },
                        mapToOutput = { it },
                    ).asFlow()

            val emissions = mutableListOf<Result<Unit, NetworkError>>()
            flow.toList(emissions)

            assertTrue(emissions.first() is Result.Loading)
            val error = emissions.last() as Result.Error
            assertEquals(NetworkError.Serialization, error.error)
        }

    @Test
    fun `when IOException then returns NoInternet`() =
        runTest {
            val flow =
                NetworkBoundResource
                    .makeCache<Unit, String, Unit>(
                        loadFromDb = { },
                        shouldFetch = { true },
                        fetchFromNetwork = {
                            throw IOException("no network")
                        },
                        saveNetworkResult = { /* not called */ },
                        mapToOutput = { it },
                    ).asFlow()

            val emissions = mutableListOf<Result<Unit, NetworkError>>()
            flow.toList(emissions)

            assertTrue(emissions.first() is Result.Loading)
            val error = emissions.last() as Result.Error
            assertEquals(NetworkError.NoInternet, error.error)
        }
}
