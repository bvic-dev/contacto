package com.bvic.lydiacontacts.ui.contacts

import com.bvic.lydiacontacts.core.NetworkError
import com.bvic.lydiacontacts.domain.model.RandomUser

data class ContactsUiState(
    val contacts: List<RandomUser> = emptyList(),
    val loading: Boolean = true,
    val error: NetworkError? = null,
    val endReached: Boolean = false,
) {
    val isLoadingNextPage
        get() = loading && contacts.isNotEmpty()

    val isLoadingFirstPage
        get() = loading && contacts.isEmpty()
}
