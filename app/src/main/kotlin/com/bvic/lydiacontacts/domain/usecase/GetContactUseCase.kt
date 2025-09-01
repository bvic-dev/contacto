package com.bvic.lydiacontacts.domain.usecase

import com.bvic.lydiacontacts.domain.repository.RandomUserRepository

class GetContactUseCase(
    private val randomUserRepository: RandomUserRepository,
) {
    operator fun invoke(id: String) = randomUserRepository.getLocalUser(id)
}
