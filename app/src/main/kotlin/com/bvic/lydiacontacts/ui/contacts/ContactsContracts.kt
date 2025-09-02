package com.bvic.lydiacontacts.ui.contacts

import com.bvic.lydiacontacts.core.Error
import com.bvic.lydiacontacts.domain.model.RandomUser

data class ContactsState(
    val contacts: List<RandomUser> = emptyList(),
    val isRefreshing: Boolean = false,
    val loading: Boolean = false,
    val error: Error? = null,
    val endReached: Boolean = false,
    val query: String = "",
) {
    val isLoadingNextPage
        get() = loading && contacts.isNotEmpty()

    val isLoadingFirstPage
        get() = loading && contacts.isEmpty()

    val showEmptyDataSet
        get() = !loading && contacts.isEmpty()
}

sealed interface ContactsAction {
    data class QueryChanged(
        val value: String,
    ) : ContactsAction

    data class ContactClicked(
        val id: String,
    ) : ContactsAction

    data object ListEndReached : ContactsAction

    data object PullToRefresh : ContactsAction
}

sealed interface ContactsEffect {
    data class ShowMessage(
        val message: String,
    ) : ContactsEffect
}
