package com.bvic.lydiacontacts.data.repository

import com.bvic.lydiacontacts.core.NetworkError
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.data.mapper.toDomain
import com.bvic.lydiacontacts.data.remote.api.RandomUserApi
import com.bvic.lydiacontacts.domain.model.RandomUser
import com.bvic.lydiacontacts.domain.repository.RandomUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RandomUserRepositoryImpl(
    private val randomUserApi: RandomUserApi,
) : RandomUserRepository {
    override fun fetchRandomUserPage(
        page: Int,
        pageSize: Int,
    ): Flow<Result<List<RandomUser>, NetworkError>> =
        flow {
            emit(Result.Loading)
            try {
                val response = randomUserApi.getRandomUsers(page = page, results = pageSize)
                if (!response.isSuccessful) {
                    val mapped =
                        when (response.code()) {
                            401 -> NetworkError.Unauthorized
                            404 -> NetworkError.NotFound
                            in 500..599 -> NetworkError.ServerError
                            else -> NetworkError.Unknown
                        }
                    emit(Result.Error(mapped))
                    return@flow
                }
                val users =
                    response
                        .body()
                        ?.results
                        .orEmpty()
                        .map { it.toDomain() }
                emit(Result.Success(users))
            } catch (t: Throwable) {
                val mapped =
                    when (t) {
                        is java.net.SocketTimeoutException -> NetworkError.RequestTimeout
                        is java.io.IOException -> NetworkError.NoInternet
                        is com.google.gson.JsonSyntaxException -> NetworkError.Serialization
                        else -> NetworkError.Unknown
                    }
                emit(Result.Error(mapped))
            }
        }.flowOn(Dispatchers.IO)
}
