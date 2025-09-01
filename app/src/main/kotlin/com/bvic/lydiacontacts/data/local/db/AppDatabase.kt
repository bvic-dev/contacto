package com.bvic.lydiacontacts.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bvic.lydiacontacts.data.local.dao.RandomUserDao
import com.bvic.lydiacontacts.data.local.entity.RandomUserEntity

@Database(
    entities = [RandomUserEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomUserDao(): RandomUserDao

    companion object {
        const val DATABASE_NAME = "lydia_contact_db"
    }
}
