package com.bvic.contacto.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bvic.contacto.data.local.dao.RandomUserDao
import com.bvic.contacto.data.local.entity.RandomUserEntity

@Database(
    entities = [RandomUserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomUserDao(): RandomUserDao

    companion object {
        const val DATABASE_NAME = "contacto_db"
    }
}
