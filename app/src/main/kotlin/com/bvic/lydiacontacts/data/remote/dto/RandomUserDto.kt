package com.bvic.lydiacontacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RandomUserDto(
    @SerializedName("cell")
    val cell: String?,
    @SerializedName("dob")
    val dob: DobDto?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("id")
    val id: IdDto?,
    @SerializedName("location")
    val location: LocationDto?,
    @SerializedName("login")
    val login: LoginDto,
    @SerializedName("name")
    val name: NameDto?,
    @SerializedName("nat")
    val nat: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("picture")
    val picture: PictureDto?,
    @SerializedName("registered")
    val registered: RegisteredDto?,
)
