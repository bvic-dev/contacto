package com.bvic.lydiacontacts.feature.contacts

import com.bvic.lydiacontacts.core.NetworkError
import com.bvic.lydiacontacts.domain.model.RandomUser

data class ContactsUiState(
    val contacts: List<RandomUser> = emptyList(),
    val loading: Boolean = false,
    val error: NetworkError? = null,
    val endReached: Boolean = false,
)
