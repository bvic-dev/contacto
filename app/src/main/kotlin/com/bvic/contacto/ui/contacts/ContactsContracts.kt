package com.bvic.contacto.ui.contacts

import com.bvic.contacto.core.Error
import com.bvic.contacto.domain.model.RandomUser

data class ContactsState(
    val contacts: List<RandomUser> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingFirstPage: Boolean = false,
    val loading: Boolean = false,
    val endReached: Boolean = false,
    val query: String = "",
) {
    val isLoadingNextPage
        get() = loading && contacts.isNotEmpty()

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
    data class ShowError(
        val error: Error,
    ) : ContactsEffect
}
