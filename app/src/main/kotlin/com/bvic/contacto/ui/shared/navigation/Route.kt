package com.bvic.contacto.ui.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Contacts : Route()

    @Serializable
    data class ContactDetail(
        val id: String,
    ) : Route()
}
