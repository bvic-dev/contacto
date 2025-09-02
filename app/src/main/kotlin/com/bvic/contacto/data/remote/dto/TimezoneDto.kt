package com.bvic.contacto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TimezoneDto(
    @SerializedName("description")
    val description: String?,
    @SerializedName("offset")
    val offset: String?,
)
