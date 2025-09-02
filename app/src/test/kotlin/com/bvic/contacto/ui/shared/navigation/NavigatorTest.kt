package com.bvic.contacto.ui.shared.navigation

import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigatorTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var navigator: Navigator

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        navigator = Navigator()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `openSmsApp emits OpenSmsApp with phone`() =
        runTest {
            val phone = "0700000000"
            val action =
                collectOne {
                    navigator.openSmsApp(phone)
                }
            val sms = action as NavigationAction.OpenSmsApp
            assertEquals(phone, sms.phoneNumber)
            assertTrue(sms.id.isNotBlank())
        }

    @Test
    fun `openCallApp emits OpenCallApp with phone`() =
        runTest {
            val phone = "0600000000"
            val action =
                collectOne {
                    navigator.openCallApp(phone)
                }
            val call = action as NavigationAction.OpenCallApp
            assertEquals(phone, call.phoneNumber)
            assertTrue(call.id.isNotBlank())
        }

    @Test
    fun `openMailApp emits OpenEmailApp with email`() =
        runTest {
            val email = "john@doe.com"
            val action =
                collectOne {
                    navigator.openMailApp(email)
                }
            val mail = action as NavigationAction.OpenEmailApp
            assertEquals(email, mail.email)
            assertTrue(mail.id.isNotBlank())
        }

    @Test
    fun `openGoogleMapApp emits OpenGoogleMap with coords and address`() =
        runTest {
            val action =
                collectOne {
                    navigator.openGoogleMapApp(48.85, 2.35, "10 Rue Paris")
                }
            val maps = action as NavigationAction.OpenGoogleMap
            assertEquals(48.85, maps.latitude)
            assertEquals(2.35, maps.longitude)
            assertEquals("10 Rue Paris", maps.address)
            assertTrue(maps.id.isNotBlank())
        }

    @Test
    fun `navigate emits Navigate with route`() =
        runTest {
            val route = Route.ContactDetail("id-42")
            val action =
                collectOne {
                    navigator.navigate(route)
                }
            val nav = action as NavigationAction.Navigate
            assertEquals(route, nav.destination)
            assertTrue(nav.id.isNotBlank())
        }

    @Test
    fun `navigateBack emits NavigateBack`() =
        runTest {
            val action =
                collectOne {
                    navigator.navigateBack()
                }
            assertTrue(action is NavigationAction.NavigateBack)
            val back = action as NavigationAction.NavigateBack
            assertTrue(back.id.isNotBlank())
        }

    // --- helpers ---

    private suspend fun collectOne(block: suspend () -> Unit): NavigationAction {
        val list = Channel<NavigationAction>(1)
        val job = backgroundCollectTo(list)
        block()
        dispatcher.scheduler.advanceUntilIdle()
        val action = list.receive()
        job.cancel()
        return action
    }

    private fun backgroundCollectTo(channel: Channel<NavigationAction>) =
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).let { scope ->
            scope.launch {
                navigator.navigationActions.collect { channel.send(it) }
            }
        }
}
