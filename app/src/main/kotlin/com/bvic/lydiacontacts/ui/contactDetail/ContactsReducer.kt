package com.bvic.lydiacontacts.ui.contactDetail

import com.bvic.lydiacontacts.core.Error
import com.bvic.lydiacontacts.domain.model.RandomUser

internal sealed interface ContactDetailPartial {
    data object Loading : ContactDetailPartial

    data class ContactLoaded(
        val data: RandomUser,
    ) : ContactDetailPartial

    data class Failed(
        val error: Error,
    ) : ContactDetailPartial
}

internal fun reduce(
    prev: ContactDetailState,
    change: ContactDetailPartial,
): ContactDetailState =
    when (change) {
        is ContactDetailPartial.ContactLoaded ->
            prev.copy(
                contact = change.data,
                loading = false,
            )

        is ContactDetailPartial.Failed ->
            prev.copy(
                loading = false,
            )

        is ContactDetailPartial.Loading ->
            prev.copy(
                loading = true,
            )
    }
