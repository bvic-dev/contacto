package com.bvic.contacto.data.repository

import com.bvic.contacto.core.Error
import com.bvic.contacto.core.Result
import com.bvic.contacto.core.network.NetworkError
import com.bvic.contacto.data.core.NetworkBoundResource
import com.bvic.contacto.data.local.dao.RandomUserDao
import com.bvic.contacto.data.mapper.toDomain
import com.bvic.contacto.data.mapper.toEntity
import com.bvic.contacto.data.remote.api.RandomUserApi
import com.bvic.contacto.domain.model.RandomUser
import com.bvic.contacto.domain.repository.RandomUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RandomUserRepositoryImpl(
    private val randomUserApi: RandomUserApi,
    private val randomUserDao: RandomUserDao,
) : RandomUserRepository {
    override fun fetchRandomUserPage(
        page: Int,
        pageSize: Int,
        forceRefresh: Boolean,
    ): Flow<Result<List<RandomUser>, NetworkError>> =
        NetworkBoundResource
            .makeCache(
                loadFromDb = {
                    randomUserDao.getPage(page)
                },
                shouldFetch = {
                    it.isEmpty() || forceRefresh
                },
                fetchFromNetwork = {
                    randomUserApi.getRandomUsers(page = page, results = pageSize)
                },
                saveNetworkResult = {
                    if (forceRefresh) {
                        randomUserDao.deleteAllRandomUsers()
                    }
                    val entities = it?.results?.map { dto -> dto.toEntity(page) }
                    if (entities.isNullOrEmpty()) return@makeCache
                    randomUserDao.insertAll(entities)
                },
                mapToOutput = { entities -> entities.map { it.toDomain() } },
            ).asFlow()

    override fun searchLocalUsers(rawQuery: String): Flow<Result<List<RandomUser>, Error>> =
        flow {
            emit(Result.Loading)
            val query = "${rawQuery.trim()}%"
            val result =
                randomUserDao
                    .search(
                        query = query,
                    ).map { it.toDomain() }
            emit(Result.Success(result))
        }.flowOn(Dispatchers.IO)

    override fun getLocalUser(id: String): Flow<Result<RandomUser, Error>> =
        flow {
            emit(Result.Loading)
            val result = randomUserDao.getRandomUserById(id)
            if (result == null) {
                emit(Result.Error(NetworkError.NotFound))
                return@flow
            }
            emit(Result.Success(result.toDomain()))
        }

    override suspend fun cleanContact() {
        randomUserDao.deleteAllRandomUsers()
    }
}
