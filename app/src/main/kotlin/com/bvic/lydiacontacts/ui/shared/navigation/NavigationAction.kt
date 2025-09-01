package com.bvic.lydiacontacts.ui.shared.navigation

import androidx.navigation.NavOptionsBuilder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed interface NavigationAction {
    data object Idle : NavigationAction

    data class Redirect(
        val uri: String,
        val id: String = Uuid.random().toString(),
    ) : NavigationAction

    data class Navigate(
        val destination: Route,
        val navOptions: NavOptionsBuilder.() -> Unit = {},
        val id: String = Uuid.random().toString(),
    ) : NavigationAction

    data class NavigateBack(
        val id: String = Uuid.random().toString(),
    ) : NavigationAction
}
