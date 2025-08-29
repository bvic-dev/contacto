package com.bvic.lydiacontacts.data.mapper

import com.bvic.lydiacontacts.data.local.entity.RandomUserEntity
import com.bvic.lydiacontacts.data.remote.dto.RandomUserDto
import com.bvic.lydiacontacts.domain.model.RandomUser
import java.util.UUID

fun RandomUserDto.toDomain(): RandomUser =
    RandomUser(
        id = login?.uuid ?: UUID.randomUUID().toString(),
        fullName = listOfNotNull(name?.first, name?.last).joinToString(" ").trim(),
        email = email,
        phone = phone,
        pictureUrl = picture?.medium ?: picture?.thumbnail ?: picture?.large,
    )

fun RandomUserDto.toEntity(page: Int): RandomUserEntity =
    RandomUserEntity(
        id = login?.uuid ?: UUID.randomUUID().toString(),
        firstName = name?.first,
        lastName = name?.last,
        email = email,
        phone = phone,
        pictureUrl = picture?.medium ?: picture?.thumbnail ?: picture?.large,
        page = page,
    )

fun RandomUserEntity.toDomain(): RandomUser =
    RandomUser(
        id = id,
        fullName = listOfNotNull(firstName, lastName).joinToString(" ").trim(),
        email = email,
        phone = phone,
        pictureUrl = pictureUrl,
    )
