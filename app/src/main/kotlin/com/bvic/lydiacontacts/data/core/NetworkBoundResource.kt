package com.bvic.lydiacontacts.data.core

import com.bvic.lydiacontacts.core.NetworkError
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.core.onError
import com.bvic.lydiacontacts.core.onSuccess
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class NetworkBoundResource<Local, Remote, Out> {
    protected abstract suspend fun loadFromDb(): Local

    protected abstract fun shouldFetch(data: Local): Boolean

    protected abstract suspend fun fetchFromNetwork(): Response<Remote>

    protected abstract suspend fun saveNetworkResult(data: Remote)

    protected abstract fun mapToOutput(data: Local): Out

    fun asFlow(): Flow<Result<Out, NetworkError>> =
        flow {
            emit(Result.Loading)
            val localData = loadFromDb()
            if (shouldFetch(localData)) {
                val result = executeNetworkCall(::fetchFromNetwork)
                result
                    .onSuccess {
                        saveNetworkResult(it)
                        val newData = loadFromDb()
                        emit(Result.Success(mapToOutput(newData)))
                        return@flow
                    }.onError {
                        emit(Result.Error(it))
                        return@flow
                    }
            } else {
                emit(Result.Success(mapToOutput(localData)))
            }
        }.flowOn(Dispatchers.IO)

    private suspend fun executeNetworkCall(call: suspend () -> Response<Remote>): Result<Remote, NetworkError> =
        try {
            val response = call()
            handleResponse(response)
        } catch (t: Throwable) {
            handleException(t)
        }

    private fun handleResponse(response: Response<Remote>): Result<Remote, NetworkError> =
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(NetworkError.NotFound)
            }
        } else {
            val error =
                when (response.code()) {
                    401 -> NetworkError.Unauthorized
                    404 -> NetworkError.NotFound
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown
                }
            Result.Error(error)
        }

    private fun handleException(t: Throwable): Result.Error<NetworkError> {
        val error =
            when (t) {
                is SocketTimeoutException -> NetworkError.RequestTimeout
                is IOException -> NetworkError.NoInternet
                is JsonSyntaxException -> NetworkError.Serialization
                else -> NetworkError.Unknown
            }
        return Result.Error(error)
    }

    companion object {
        inline fun <Local, Remote, Out> makeCache(
            crossinline loadFromDb: suspend () -> Local,
            crossinline shouldFetch: (Local) -> Boolean,
            crossinline fetchFromNetwork: suspend () -> Response<Remote>,
            crossinline saveNetworkResult: suspend (Remote) -> Unit,
            crossinline mapToOutput: (Local) -> Out,
        ) = object : NetworkBoundResource<Local, Remote, Out>() {
            override suspend fun loadFromDb(): Local = loadFromDb()

            override fun shouldFetch(data: Local): Boolean = shouldFetch(data)

            override suspend fun fetchFromNetwork(): Response<Remote> = fetchFromNetwork()

            override suspend fun saveNetworkResult(data: Remote) = saveNetworkResult(data)

            override fun mapToOutput(data: Local): Out = mapToOutput(data)
        }
    }
}
