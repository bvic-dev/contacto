package com.bvic.lydiacontacts.domain.repository

import com.bvic.lydiacontacts.core.Error
import com.bvic.lydiacontacts.core.NetworkError
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.domain.model.RandomUser
import kotlinx.coroutines.flow.Flow

interface RandomUserRepository {
    fun fetchRandomUserPage(
        page: Int,
        pageSize: Int,
        forceRefresh: Boolean,
    ): Flow<Result<List<RandomUser>, NetworkError>>

    fun searchLocalUsers(rawQuery: String): Flow<Result<List<RandomUser>, Error>>

    fun getLocalUser(id: String): Flow<Result<RandomUser, Error>>

    suspend fun cleanContact()
}
