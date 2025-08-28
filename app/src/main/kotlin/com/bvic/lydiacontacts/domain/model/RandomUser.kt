package com.bvic.lydiacontacts.domain.model

data class RandomUser(
    val id: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val pictureUrl: String?,
)
