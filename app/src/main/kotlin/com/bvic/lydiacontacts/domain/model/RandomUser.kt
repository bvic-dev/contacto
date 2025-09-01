package com.bvic.lydiacontacts.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class RandomUser
    @OptIn(ExperimentalTime::class)
    constructor(
        val id: String,
        val fullName: String,
        val email: String?,
        val phone: String?,
        val pictureThumbnailUrl: String?,
        val pictureLarge: String?,
        val age: Int?,
        val nationality: String?,
        val address: String?,
        val latitude: Double?,
        val longitude: Double?,
        val birthDate: Instant?,
    )
