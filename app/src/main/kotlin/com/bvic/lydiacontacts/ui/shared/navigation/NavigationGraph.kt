package com.bvic.lydiacontacts.ui.shared.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bvic.lydiacontacts.ui.contact.ContactDetailScreen
import com.bvic.lydiacontacts.ui.contacts.ContactsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startRoute: Route,
) {
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        remember {
            {
                slideIntoContainer(SlideDirection.Left)
            }
        }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        remember {
            {
                slideOutOfContainer(SlideDirection.Left)
            }
        }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        remember {
            {
                slideIntoContainer(SlideDirection.Right)
            }
        }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        remember {
            {
                slideOutOfContainer(SlideDirection.Right)
            }
        }

    NavHost(
        navController = navController,
        startDestination = startRoute,
    ) {
        composable<Route.Contacts>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
        ) {
            ContactsScreen()
        }

        composable<Route.ContactDetail>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
        ) {
            ContactDetailScreen()
        }
    }
}
