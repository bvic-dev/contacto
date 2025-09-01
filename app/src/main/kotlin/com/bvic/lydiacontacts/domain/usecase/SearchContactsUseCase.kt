package com.bvic.lydiacontacts.domain.usecase

import com.bvic.lydiacontacts.domain.repository.RandomUserRepository

class SearchContactsUseCase(
    private val randomUserRepository: RandomUserRepository,
) {
    operator fun invoke(query: String) = randomUserRepository.searchLocalUsers(query)
}
