package com.bvic.lydiacontacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InfoDto(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("results")
    val results: Int?,
    @SerializedName("seed")
    val seed: String?,
    @SerializedName("version")
    val version: String?,
)
