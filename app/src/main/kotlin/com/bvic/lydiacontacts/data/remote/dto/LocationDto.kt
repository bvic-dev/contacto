package com.bvic.lydiacontacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("city")
    val city: String?,
    @SerializedName("coordinates")
    val coordinates: CoordinatesDto?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("postcode")
    val postcode: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("street")
    val street: StreetDto?,
    @SerializedName("timezone")
    val timezone: TimezoneDto?,
)
