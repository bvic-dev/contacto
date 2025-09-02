package com.bvic.contacto.ui.contactdetail

import androidx.lifecycle.SavedStateHandle
import com.bvic.contacto.core.Result
import com.bvic.contacto.core.network.NetworkError
import com.bvic.contacto.domain.model.RandomUser
import com.bvic.contacto.domain.repository.RandomUserRepository
import com.bvic.contacto.domain.usecase.GetContactUseCase
import com.bvic.contacto.ui.shared.navigation.Navigator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class ContactDetailViewModelTest {
    private val mainDispatcher = StandardTestDispatcher()

    private lateinit var navigator: Navigator
    private lateinit var getContactUseCase: GetContactUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
        navigator = mockk(relaxed = true)
        val repo = mockk<RandomUserRepository>(relaxed = true)
        getContactUseCase = GetContactUseCase(repo)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun savedState(id: String) = SavedStateHandle(mapOf("id" to id))

    private fun vmWithFlow(
        id: String,
        flow: Flow<Result<RandomUser, com.bvic.contacto.core.Error>>,
    ): ContactDetailViewModel {
        getContactUseCase = mockk()
        coEvery { getContactUseCase(id) } returns flow

        return ContactDetailViewModel(
            savedStateHandle = savedState(id),
            getContactUseCase = getContactUseCase,
            navigator = navigator,
            contactIdDecoder = { _ -> id },
        )
    }

    private fun aUser(
        id: String = "u-1",
        fullName: String = "John Doe",
        email: String? = "john@doe.com",
        phone: String? = "0102030405",
        pictureThumb: String? = "thumb",
        pictureLarge: String? = "large",
        age: Int? = 30,
        nationality: String? = "FR",
        address: String? = "10 Rue Paris",
        lat: Double? = 48.85,
        lon: Double? = 2.35,
        birth: Instant? = Instant.parse("1990-01-01T00:00:00Z"),
    ) = RandomUser(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        pictureThumbnailUrl = pictureThumb,
        pictureLarge = pictureLarge,
        age = age,
        nationality = nationality,
        address = address,
        latitude = lat,
        longitude = lon,
        birthDate = birth,
    )

    private fun TestScope.collectState(vm: ContactDetailViewModel) =
        launch {
            vm.contactsUiState.collect { }
        }

    @Test
    fun `when use case emits Loading then Success, state shows loading then contact`() =
        runTest {
            val id = "user-42"
            val user = aUser(id = id)

            val vm =
                vmWithFlow(
                    id = id,
                    flow = listOf(Result.Loading, Result.Success(user)).asFlow(),
                )

            val stateJob = collectState(vm) // activate pipeline
            advanceUntilIdle()

            val state = vm.contactsUiState.value
            assertEquals(user, state.contact)
            assertFalse(state.loading)

            stateJob.cancel()
        }

    @Test
    fun `when use case emits Error, effect ShowError is emitted and state is not loading`() =
        runTest {
            val id = "user-404"
            val error = NetworkError.NotFound

            val vm = vmWithFlow(id = id, flow = flowOf(Result.Error(error)))

            val stateJob = collectState(vm) // activate pipeline

            val collected = mutableListOf<ContactDetailEffect>()
            val effectsJob = launch { vm.effects.collect { collected += it } }

            advanceUntilIdle()

            // One ShowError effect with the correct error
            val eff = collected.firstOrNull() as? ContactDetailEffect.ShowError
            requireNotNull(eff) { "Expected a ShowError effect, but none was collected." }
            assertEquals(error, eff.error)

            // State remains not loading and contact is null
            val state = vm.contactsUiState.value
            assertNull(state.contact)
            assertFalse(state.loading)

            effectsJob.cancel()
            stateJob.cancel()
        }

    @Test
    fun `CallClicked calls navigator when phone present and ignores when absent`() =
        runTest {
            val id = "user-call"
            val user = aUser(id = id, phone = "0600000000")

            // With phone
            val vm = vmWithFlow(id = id, flow = flowOf(Result.Success(user)))
            val stateJob1 = collectState(vm)
            advanceUntilIdle()

            vm.submitAction(ContactDetailAction.CallClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openCallApp("0600000000") }

            // Without phone
            val vmNoPhone = vmWithFlow(id = id, flow = flowOf(Result.Success(user.copy(phone = null))))
            val stateJob2 = collectState(vmNoPhone)
            advanceUntilIdle()

            vmNoPhone.submitAction(ContactDetailAction.CallClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openCallApp(any()) } // still 1 total

            stateJob1.cancel()
            stateJob2.cancel()
        }

    @Test
    fun `MailClicked calls navigator when email present and ignores when absent`() =
        runTest {
            val id = "user-mail"
            val user = aUser(id = id, email = "jane@doe.com")

            val vm = vmWithFlow(id = id, flow = flowOf(Result.Success(user)))
            val stateJob1 = collectState(vm)
            advanceUntilIdle()

            vm.submitAction(ContactDetailAction.MailClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openMailApp("jane@doe.com") }

            val vmNoEmail = vmWithFlow(id = id, flow = flowOf(Result.Success(user.copy(email = null))))
            val stateJob2 = collectState(vmNoEmail)
            advanceUntilIdle()

            vmNoEmail.submitAction(ContactDetailAction.MailClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openMailApp(any()) } // still 1 total

            stateJob1.cancel()
            stateJob2.cancel()
        }

    @Test
    fun `MapClicked calls navigator with coordinates and address when address present, ignores when address null`() =
        runTest {
            val id = "user-map"
            val user = aUser(id = id, address = "10 Rue Paris", lat = 1.23, lon = 4.56)

            val vm = vmWithFlow(id = id, flow = flowOf(Result.Success(user)))
            val stateJob1 = collectState(vm)
            advanceUntilIdle()

            vm.submitAction(ContactDetailAction.MapClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openGoogleMapApp(1.23, 4.56, "10 Rue Paris") }

            val vmNoAddr =
                vmWithFlow(id = id, flow = flowOf(Result.Success(user.copy(address = null))))
            val stateJob2 = collectState(vmNoAddr)
            advanceUntilIdle()

            vmNoAddr.submitAction(ContactDetailAction.MapClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) {
                navigator.openGoogleMapApp(
                    any(),
                    any(),
                    any(),
                )
            } // still 1 total

            stateJob1.cancel()
            stateJob2.cancel()
        }

    @Test
    fun `MessageClicked calls navigator when phone present and ignores when absent`() =
        runTest {
            val id = "user-sms"
            val user = aUser(id = id, phone = "0700000000")

            val vm = vmWithFlow(id = id, flow = flowOf(Result.Success(user)))
            val stateJob1 = collectState(vm)
            advanceUntilIdle()

            vm.submitAction(ContactDetailAction.MessageClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openSmsApp("0700000000") }

            val vmNoPhone = vmWithFlow(id = id, flow = flowOf(Result.Success(user.copy(phone = null))))
            val stateJob2 = collectState(vmNoPhone)
            advanceUntilIdle()

            vmNoPhone.submitAction(ContactDetailAction.MessageClicked)
            advanceUntilIdle()
            coVerify(exactly = 1) { navigator.openSmsApp(any()) } // still 1 total

            stateJob1.cancel()
            stateJob2.cancel()
        }

    @Test
    fun `BackClicked navigates back`() =
        runTest {
            val id = "user-back"
            val vm = vmWithFlow(id = id, flow = flowOf(Result.Success(aUser(id = id))))
            val stateJob = collectState(vm)
            advanceUntilIdle()

            vm.submitAction(ContactDetailAction.BackClicked)
            advanceUntilIdle()

            coVerify(exactly = 1) { navigator.navigateBack() }
            stateJob.cancel()
        }
}
