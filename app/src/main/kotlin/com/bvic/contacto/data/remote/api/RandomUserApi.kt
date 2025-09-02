package com.bvic.contacto.data.remote.api

import com.bvic.contacto.data.remote.dto.ResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    @GET("api/1.3/")
    suspend fun getRandomUsers(
        @Query("seed") seed: String? = "lydia",
        @Query("results") results: Int,
        @Query("page") page: Int,
    ): Response<ResponseDto>
}
