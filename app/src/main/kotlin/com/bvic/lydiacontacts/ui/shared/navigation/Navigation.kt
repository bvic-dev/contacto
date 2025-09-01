package com.bvic.lydiacontacts.ui.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController

@Composable
fun Navigation(
    navController: NavHostController,
    navigationAction: NavigationAction,
) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(navigationAction) {
        when (navigationAction) {
            is NavigationAction.Redirect -> {
                try {
                    uriHandler.openUri(navigationAction.uri)
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("uriHandler openUri")
                    Firebase.crashlytics.recordException(e)
                     */
                }
            }

            is NavigationAction.Navigate -> {
                try {
                    navController.navigate(navigationAction.destination) {
                        navigationAction.navOptions(this)
                    }
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("Navigate")
                    Firebase.crashlytics.recordException(e)
                     */
                }
            }

            is NavigationAction.NavigateBack -> {
                try {
                    navController.popBackStack()
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("Pop back stack")
                    Firebase.crashlytics.recordException(e)

                     */
                }
            }

            NavigationAction.Idle -> {}
        }
    }
}
