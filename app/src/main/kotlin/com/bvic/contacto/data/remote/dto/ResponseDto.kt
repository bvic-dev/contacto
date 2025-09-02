package com.bvic.contacto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ResponseDto(
    @SerializedName("info")
    val info: InfoDto?,
    @SerializedName("results")
    val results: List<RandomUserDto>?,
)
