package com.bvic.contacto.ui.shared.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavHostController

@Composable
fun Navigation(
    navController: NavHostController,
    navigationAction: NavigationAction,
) {
    val context = LocalContext.current
    LaunchedEffect(navigationAction) {
        when (navigationAction) {
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

            is NavigationAction.OpenGoogleMap -> {
                try {
                    val gmmIntentUri: Uri? =
                        (
                            "geo:${navigationAction.latitude},${navigationAction.longitude}?q=" +
                                Uri.encode(
                                    navigationAction.address,
                                )
                        ).toUri()
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("uriHandler openUri")
                    Firebase.crashlytics.recordException(e)
                     */
                }
            }

            is NavigationAction.OpenSmsApp -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = "smsto: ${navigationAction.phoneNumber}".toUri()
                    context.startActivity(intent)
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("uriHandler openUri")
                    Firebase.crashlytics.recordException(e)
                     */
                }
            }

            is NavigationAction.OpenCallApp -> {
                try {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = "tel:${navigationAction.phoneNumber}".toUri()
                    context.startActivity(intent)
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("uriHandler openUri")
                    Firebase.crashlytics.recordException(e)
                     */
                }
            }

            is NavigationAction.OpenEmailApp -> {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = "mailto:${navigationAction.email}".toUri()
                    context.startActivity(intent)
                } catch (e: Exception) {
                    /*
                    Todo : Log
                    Firebase.crashlytics.log("uriHandler openUri")
                    Firebase.crashlytics.recordException(e)
                     */
                }
            }

            NavigationAction.Idle -> {}
        }
    }
}
