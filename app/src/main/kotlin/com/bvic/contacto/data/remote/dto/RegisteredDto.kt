package com.bvic.contacto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisteredDto(
    @SerializedName("age")
    val age: Int?,
    @SerializedName("date")
    val date: String?,
)
