package com.bvic.lydiacontacts.di

import com.bvic.lydiacontacts.core.network.NetworkMonitor
import com.bvic.lydiacontacts.domain.repository.RandomUserRepository
import com.bvic.lydiacontacts.domain.usecase.GetContactUseCase
import com.bvic.lydiacontacts.domain.usecase.GetContactsUseCase
import com.bvic.lydiacontacts.domain.usecase.ObserveConnectivityUseCase
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
    fun provideGetContactsUseCase(randomUserRepository: RandomUserRepository): GetContactsUseCase =
        GetContactsUseCase(
            randomUserRepository = randomUserRepository,
        )

    @Provides
    @Singleton
    fun provideSearchContactsUseCase(randomUserRepository: RandomUserRepository): SearchContactsUseCase =
        SearchContactsUseCase(
            randomUserRepository = randomUserRepository,
        )

    @Provides
    @Singleton
    fun provideGetContactUseCase(randomUserRepository: RandomUserRepository): GetContactUseCase =
        GetContactUseCase(
            randomUserRepository = randomUserRepository,
        )

    @Provides
    @Singleton
    fun provideObserveConnectivityUseCase(monitor: NetworkMonitor): ObserveConnectivityUseCase =
        ObserveConnectivityUseCase(
            monitor = monitor,
        )
}
