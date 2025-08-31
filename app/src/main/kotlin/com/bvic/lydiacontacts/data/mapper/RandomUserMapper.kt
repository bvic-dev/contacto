package com.bvic.lydiacontacts.data.mapper

import com.bvic.lydiacontacts.data.local.entity.RandomUserEntity
import com.bvic.lydiacontacts.data.remote.dto.RandomUserDto
import com.bvic.lydiacontacts.domain.model.RandomUser

fun RandomUserDto.toEntity(page: Int): RandomUserEntity =
    RandomUserEntity(
        id = login.uuid,
        firstName = name?.first,
        lastName = name?.last,
        email = email,
        phone = phone,
        pictureThumbnailUrl = picture?.thumbnail ?: picture?.medium ?: picture?.large,
        page = page,
    )

fun RandomUserEntity.toDomain(): RandomUser =
    RandomUser(
        id = id,
        fullName = listOfNotNull(firstName, lastName).joinToString(" ").trim(),
        email = email,
        phone = phone,
        pictureThumbnailUrl = pictureThumbnailUrl,
    )
