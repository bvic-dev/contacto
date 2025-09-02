package com.bvic.contacto.ui.contactdetail

import com.bvic.contacto.core.Error
import com.bvic.contacto.domain.model.RandomUser

data class ContactDetailState(
    val contact: RandomUser? = null,
    val loading: Boolean = false,
)

sealed interface ContactDetailAction {
    data object CallClicked : ContactDetailAction

    data object MailClicked : ContactDetailAction

    data object MapClicked : ContactDetailAction

    data object MessageClicked : ContactDetailAction

    data object BackClicked : ContactDetailAction
}

sealed interface ContactDetailEffect {
    data class ShowError(
        val error: Error,
    ) : ContactDetailEffect
}
