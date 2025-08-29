package com.bvic.lydiacontacts.data.repository

import com.bvic.lydiacontacts.core.NetworkError
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.data.core.NetworkBoundResource
import com.bvic.lydiacontacts.data.local.dao.RandomUserDao
import com.bvic.lydiacontacts.data.mapper.toDomain
import com.bvic.lydiacontacts.data.mapper.toEntity
import com.bvic.lydiacontacts.data.remote.api.RandomUserApi
import com.bvic.lydiacontacts.domain.model.RandomUser
import com.bvic.lydiacontacts.domain.repository.RandomUserRepository
import kotlinx.coroutines.flow.Flow
import kotlin.collections.map

class RandomUserRepositoryImpl(
    private val randomUserApi: RandomUserApi,
    private val randomUserDao: RandomUserDao,
) : RandomUserRepository {
    override fun fetchRandomUserPage(
        page: Int,
        pageSize: Int,
    ): Flow<Result<List<RandomUser>, NetworkError>> =
        NetworkBoundResource
            .makeCache(
                loadFromDb = {
                    randomUserDao.getPage(page)
                },
                shouldFetch = {
                    it.isEmpty()
                },
                fetchFromNetwork = {
                    randomUserApi.getRandomUsers(page = page, results = pageSize)
                },
                saveNetworkResult = {
                    val entities = it.results?.map { dto -> dto.toEntity(page) }
                    if (entities.isNullOrEmpty()) return@makeCache
                    randomUserDao.insertAll(entities)
                },
                mapToOutput = { entities -> entities.map { it.toDomain() } },
            ).asFlow()
}
