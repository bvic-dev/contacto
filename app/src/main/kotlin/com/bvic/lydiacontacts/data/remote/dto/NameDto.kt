package com.bvic.lydiacontacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NameDto(
    @SerializedName("first")
    val first: String?,
    @SerializedName("last")
    val last: String?,
    @SerializedName("title")
    val title: String?,
)
