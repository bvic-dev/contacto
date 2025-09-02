package com.bvic.contacto.domain.usecase

import com.bvic.contacto.domain.repository.RandomUserRepository

class GetContactUseCase(
    private val randomUserRepository: RandomUserRepository,
) {
    operator fun invoke(id: String) = randomUserRepository.getLocalUser(id)
}
