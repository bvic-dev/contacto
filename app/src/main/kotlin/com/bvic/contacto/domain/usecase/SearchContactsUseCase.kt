package com.bvic.contacto.domain.usecase

import com.bvic.contacto.domain.repository.RandomUserRepository

class SearchContactsUseCase(
    private val randomUserRepository: RandomUserRepository,
) {
    operator fun invoke(query: String) = randomUserRepository.searchLocalUsers(query)
}
