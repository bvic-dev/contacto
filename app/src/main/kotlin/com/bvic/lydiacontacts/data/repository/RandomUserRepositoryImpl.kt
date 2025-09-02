package com.bvic.lydiacontacts.data.repository

import com.bvic.lydiacontacts.core.Error
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.core.network.NetworkError
import com.bvic.lydiacontacts.data.core.NetworkBoundResource
import com.bvic.lydiacontacts.data.local.dao.RandomUserDao
import com.bvic.lydiacontacts.data.mapper.toDomain
import com.bvic.lydiacontacts.data.mapper.toEntity
import com.bvic.lydiacontacts.data.remote.api.RandomUserApi
import com.bvic.lydiacontacts.domain.model.RandomUser
import com.bvic.lydiacontacts.domain.repository.RandomUserRepository
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
