package com.bvic.contacto.domain.repository

import com.bvic.contacto.core.Error
import com.bvic.contacto.core.Result
import com.bvic.contacto.core.network.NetworkError
import com.bvic.contacto.domain.model.RandomUser
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
