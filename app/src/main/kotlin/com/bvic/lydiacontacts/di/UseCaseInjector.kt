package com.bvic.lydiacontacts.di

import com.bvic.lydiacontacts.domain.repository.RandomUserRepository
import com.bvic.lydiacontacts.domain.usecase.GetContactsUseCase
import com.bvic.lydiacontacts.domain.usecase.SearchContactsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseInjector {
    @Provides
    @Singleton
    fun provideGetContactUseCase(randomUserRepository: RandomUserRepository): GetContactsUseCase =
        GetContactsUseCase(
            randomUserRepository = randomUserRepository,
        )

    @Provides
    @Singleton
    fun provideSearchContactsUseCase(randomUserRepository: RandomUserRepository): SearchContactsUseCase =
        SearchContactsUseCase(
            randomUserRepository = randomUserRepository,
        )
}
