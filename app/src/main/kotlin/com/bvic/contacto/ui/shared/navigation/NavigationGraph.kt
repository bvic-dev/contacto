package com.bvic.contacto.ui.shared.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bvic.contacto.ui.contactDetail.ContactDetailScreen
import com.bvic.contacto.ui.contacts.ContactsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    startRoute: Route,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startRoute,
        ) {
            composable<Route.Contacts> {
                ContactsScreen(
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            }

            composable<Route.ContactDetail> {
                ContactDetailScreen(
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            }
        }
    }
}
