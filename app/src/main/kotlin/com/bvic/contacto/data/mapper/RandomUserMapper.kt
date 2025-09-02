package com.bvic.contacto.data.mapper

import com.bvic.contacto.data.local.entity.RandomUserEntity
import com.bvic.contacto.data.remote.dto.RandomUserDto
import com.bvic.contacto.domain.model.RandomUser
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun RandomUserDto.toEntity(page: Int): RandomUserEntity =
    RandomUserEntity(
        id = login.uuid,
        firstName = name?.first,
        lastName = name?.last,
        email = email,
        phone = phone,
        pictureThumbnailUrl = picture?.thumbnail ?: picture?.medium ?: picture?.large,
        pictureLarge = picture?.large ?: picture?.medium ?: picture?.thumbnail,
        age = dob?.age,
        nationality = nat,
        address =
            listOfNotNull(
                location?.street?.number,
                location?.street?.name,
                location?.city,
                location?.state,
                location?.country,
            ).joinToString(
                " ",
            ),
        latitude = location?.coordinates?.latitude,
        longitude = location?.coordinates?.longitude,
        birthDate = dob?.date,
        page = page,
    )

@OptIn(ExperimentalTime::class)
fun RandomUserEntity.toDomain(): RandomUser =
    RandomUser(
        id = id,
        fullName = listOfNotNull(firstName, lastName).joinToString(" ").trim(),
        email = email,
        phone = phone,
        pictureThumbnailUrl = pictureThumbnailUrl,
        pictureLarge = pictureLarge,
        age = age,
        nationality = nationality,
        address = address,
        latitude = latitude,
        longitude = longitude,
        birthDate = birthDate?.let { Instant.parse(birthDate) },
    )
