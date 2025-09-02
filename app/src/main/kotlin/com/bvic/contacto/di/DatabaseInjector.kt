package com.bvic.contacto.di

import android.content.Context
import androidx.room.Room
import com.bvic.contacto.data.local.dao.RandomUserDao
import com.bvic.contacto.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseInjector {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideRandomUserDao(db: AppDatabase): RandomUserDao = db.randomUserDao()
}
