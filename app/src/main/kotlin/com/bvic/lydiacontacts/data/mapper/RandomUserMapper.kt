package com.bvic.lydiacontacts.data.mapper

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
