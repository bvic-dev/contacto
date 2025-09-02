package com.bvic.contacto.ui.shared.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator
    @Inject
    constructor() {
        private val _navigationActions = Channel<NavigationAction>()
        val navigationActions = _navigationActions.receiveAsFlow()

        private val _startRoute: MutableStateFlow<Route> = MutableStateFlow(Route.Contacts)
        val startRoute = _startRoute.asStateFlow()

        suspend fun openSmsApp(phoneNumber: String) {
            _navigationActions.send(
                NavigationAction.OpenSmsApp(
                    phoneNumber = phoneNumber,
                ),
            )
        }

        suspend fun openCallApp(phoneNumber: String) {
            _navigationActions.send(
                NavigationAction.OpenCallApp(
                    phoneNumber = phoneNumber,
                ),
            )
        }

        suspend fun openMailApp(email: String) {
            _navigationActions.send(
                NavigationAction.OpenEmailApp(
                    email = email,
                ),
            )
        }

        suspend fun openGoogleMapApp(
            latitude: Double,
            longitude: Double,
            address: String,
        ) {
            _navigationActions.send(
                NavigationAction.OpenGoogleMap(
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                ),
            )
        }

        suspend fun navigate(
            destination: Route,
            navOptions: NavOptionsBuilder.() -> Unit = {},
        ) {
            _navigationActions.send(
                NavigationAction.Navigate(
                    destination = destination,
                    navOptions = navOptions,
                ),
            )
        }

        suspend fun navigateBack() {
            _navigationActions.send(NavigationAction.NavigateBack())
        }
    }
