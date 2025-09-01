package com.bvic.lydiacontacts.domain.usecase

import com.bvic.lydiacontacts.domain.repository.RandomUserRepository

class GetContactsUseCase(
    private val randomUserRepository: RandomUserRepository,
) {
    operator fun invoke(
        page: Int,
        pageSize: Int,
        forceRefresh: Boolean = false,
    ) = randomUserRepository.fetchRandomUserPage(
        page = page,
        pageSize = pageSize,
        forceRefresh = forceRefresh,
    )
}
