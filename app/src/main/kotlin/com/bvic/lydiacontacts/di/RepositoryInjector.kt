package com.bvic.lydiacontacts.di

import com.bvic.lydiacontacts.data.remote.api.RandomUserApi
import com.bvic.lydiacontacts.data.repository.RandomUserRepositoryImpl
import com.bvic.lydiacontacts.domain.repository.RandomUserRepository
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
    fun provideRandomUserRepository(randomUserApi: RandomUserApi): RandomUserRepository =
        RandomUserRepositoryImpl(
            randomUserApi = randomUserApi,
        )
}
