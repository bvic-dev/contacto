package com.bvic.lydiacontacts.ui.contacts

import com.bvic.lydiacontacts.core.Error
import com.bvic.lydiacontacts.domain.model.RandomUser

internal sealed interface ContactsPartial {
    data object Loading : ContactsPartial

    data class PageLoaded(
        val page: Int,
        val data: List<RandomUser>,
    ) : ContactsPartial

    data class SearchLoaded(
        val data: List<RandomUser>,
    ) : ContactsPartial

    data class Failed(
        val error: Error,
    ) : ContactsPartial

    data class QuerySet(
        val value: String,
    ) : ContactsPartial
}

internal fun reduce(
    prev: ContactsState,
    change: ContactsPartial,
): ContactsState =
    when (change) {
        is ContactsPartial.Loading -> prev.copy(loading = true, error = null)
        is ContactsPartial.PageLoaded -> {
            val fresh = change.page == 1
            val merged = if (fresh) change.data else prev.contacts + change.data
            prev.copy(
                contacts = merged,
                loading = false,
                error = null,
                endReached = change.data.isEmpty() || change.data.size < DEFAULT_PAGE_SIZE,
            )
        }

        is ContactsPartial.Failed ->
            prev.copy(
                loading = false,
                error = change.error,
                endReached = true,
            )

        is ContactsPartial.QuerySet -> prev.copy(query = change.value)

        is ContactsPartial.SearchLoaded ->
            prev.copy(
                contacts = change.data,
                loading = false,
            )
    }
