package com.bvic.lydiacontacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StreetDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("number")
    val number: Int?,
)
