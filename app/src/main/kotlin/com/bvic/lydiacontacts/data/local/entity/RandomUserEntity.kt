package com.bvic.lydiacontacts.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "randomUsers",
    indices = [Index("page"), Index("first_name"), Index("last_name")],
)
data class RandomUserEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "picture_thumbnail_url") val pictureThumbnailUrl: String?,
    @ColumnInfo(name = "page") val page: Int,
)
