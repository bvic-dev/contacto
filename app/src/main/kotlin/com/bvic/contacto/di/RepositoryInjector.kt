package com.bvic.contacto.di

import com.bvic.contacto.data.local.dao.RandomUserDao
import com.bvic.contacto.data.remote.api.RandomUserApi
import com.bvic.contacto.data.repository.RandomUserRepositoryImpl
import com.bvic.contacto.domain.repository.RandomUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryInjector {
    @Provides
    @Singleton
    fun provideRandomUserRepository(
        randomUserApi: RandomUserApi,
        randomUserDao: RandomUserDao,
    ): RandomUserRepository =
        RandomUserRepositoryImpl(
            randomUserApi = randomUserApi,
            randomUserDao = randomUserDao,
        )
}
