package com.bvic.contacto.ui.contacts

import com.bvic.contacto.core.Result
import com.bvic.contacto.core.network.NetworkError
import com.bvic.contacto.domain.model.RandomUser
import com.bvic.contacto.domain.usecase.GetContactsUseCase
import com.bvic.contacto.domain.usecase.SearchContactsUseCase
import com.bvic.contacto.ui.shared.navigation.Navigator
import com.bvic.contacto.ui.shared.navigation.Route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class ContactsViewModelTest {
    private val mainDispatcher = StandardTestDispatcher()

    private lateinit var getContacts: GetContactsUseCase
    private lateinit var searchContacts: SearchContactsUseCase
    private lateinit var navigator: Navigator

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
        getContacts = mockk()
        searchContacts = mockk()
        navigator = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.collectState(vm: ContactsViewModel) =
        launch {
            // Not strictly required with Eagerly, but keeps things safe if strategy changes
            vm.contactsUiState.collect { /* no-op */ }
        }

    private fun vm(): ContactsViewModel =
        ContactsViewModel(
            getContactsUseCase = getContacts,
            searchContactsUseCase = searchContacts,
            navigator = navigator,
        )

    private fun users(range: IntRange): List<RandomUser> =
        range.map { idx ->
            RandomUser(
                id = "id-$idx",
                fullName = "User $idx",
                email = "user$idx@mail.com",
                phone = "01020304$idx",
                pictureThumbnailUrl = null,
                pictureLarge = null,
                age = 30,
                nationality = "FR",
                address = "10 Rue Paris",
                latitude = 48.85,
                longitude = 2.35,
                birthDate = Instant.fromEpochMilliseconds(0),
            )
        }

    private fun flowLoadingSuccess(list: List<RandomUser>): Flow<Result<List<RandomUser>, NetworkError>> =
        flowOf(Result.Loading, Result.Success(list))

    private fun flowError(error: NetworkError): Flow<Result<List<RandomUser>, NetworkError>> = flowOf(Result.Error(error))

    @Test
    fun `QueryChanged blank - loads first page and populates state`() =
        runTest {
            // Given
            val page1 = users(1..DEFAULT_PAGE_SIZE) // 20 users
            coEvery { getContacts(1, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(page1)

            val vm = vm()
            val job = collectState(vm)

            // When
            vm.submitAction(ContactsAction.QueryChanged(""))
            advanceUntilIdle()

            // Then
            val state = vm.contactsUiState.value
            assertEquals(DEFAULT_PAGE_SIZE, state.contacts.size)
            assertFalse(state.isLoadingFirstPage)
            assertFalse(state.isRefreshing)
            assertFalse(state.endReached) // 20 == page size -> can load more
            assertEquals("", state.query)

            job.cancel()
            // verify correct call
            coVerify(exactly = 1) { getContacts(1, DEFAULT_PAGE_SIZE, false) }
        }

    @Test
    fun `ListEndReached - when allowed, loads next page and appends`() =
        runTest {
            // Given initial page then next page
            val page1 = users(1..DEFAULT_PAGE_SIZE)
            val page2 = users((DEFAULT_PAGE_SIZE + 1)..(DEFAULT_PAGE_SIZE * 2))
            coEvery { getContacts(1, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(page1)
            coEvery { getContacts(2, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(page2)

            val vm = vm()
            val job = collectState(vm)

            // Initial load via blank query
            vm.submitAction(ContactsAction.QueryChanged(""))
            advanceUntilIdle()
            assertEquals(DEFAULT_PAGE_SIZE, vm.contactsUiState.value.contacts.size)

            // When list end reached and can fetch, we load page 2
            vm.submitAction(ContactsAction.ListEndReached)
            advanceUntilIdle()

            // Then
            val state = vm.contactsUiState.value
            assertEquals(DEFAULT_PAGE_SIZE * 2, state.contacts.size)
            assertFalse(state.endReached) // still exactly a full page, can load more

            job.cancel()
            coVerify(exactly = 1) { getContacts(1, DEFAULT_PAGE_SIZE, false) }
            coVerify(exactly = 1) { getContacts(2, DEFAULT_PAGE_SIZE, false) }
        }

    @Test
    fun `ListEndReached - does nothing when query is non-empty`() =
        runTest {
            // Given
            val results = users(1..5)
            coEvery { searchContacts("hello") } returns
                flowOf(
                    Result.Loading,
                    Result.Success(results),
                )

            val vm = vm()
            val job = collectState(vm)

            // Enter search mode
            vm.submitAction(ContactsAction.QueryChanged("hello"))
            advanceUntilIdle()
            assertEquals(5, vm.contactsUiState.value.contacts.size)

            // When list end reached under query mode -> should not hit getContacts
            vm.submitAction(ContactsAction.ListEndReached)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { getContacts(any(), any(), any()) }
            job.cancel()
        }

    @Test
    fun `PullToRefresh - sets refreshing then reloads first page with forceRefresh`() =
        runTest {
            // Given existing page first, then refresh
            val page1 =
                users(1..10) // smaller page to test endReached=false because < 20? -> true actually
            val refresh = users(1..DEFAULT_PAGE_SIZE)

            // Initial load
            coEvery { getContacts(1, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(page1)
            // Refresh load with forceRefresh = true
            coEvery { getContacts(1, DEFAULT_PAGE_SIZE, true) } returns flowLoadingSuccess(refresh)

            val vm = vm()
            val job = collectState(vm)

            vm.submitAction(ContactsAction.QueryChanged(""))
            advanceUntilIdle()
            assertEquals(10, vm.contactsUiState.value.contacts.size)

            // When
            vm.submitAction(ContactsAction.PullToRefresh)
            advanceUntilIdle()

            // Then
            val state = vm.contactsUiState.value
            assertEquals(DEFAULT_PAGE_SIZE, state.contacts.size)
            assertFalse(state.isRefreshing)
            assertFalse(state.isLoadingFirstPage)

            job.cancel()
            coVerify(exactly = 1) { getContacts(1, DEFAULT_PAGE_SIZE, false) }
            coVerify(exactly = 1) { getContacts(1, DEFAULT_PAGE_SIZE, true) }
        }

    @Test
    fun `PageLoaded with empty second page marks endReached and prevents further pagination`() =
        runTest {
            // First page is full -> endReached = false, can paginate
            val page1 = users(1..DEFAULT_PAGE_SIZE) // 20 users
            val page2 = emptyList<RandomUser>() // empty second page

            coEvery { getContacts(1, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(page1)
            coEvery { getContacts(2, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(page2)

            val vm = vm()
            val job = collectState(vm)

            // Load page 1 via blank query
            vm.submitAction(ContactsAction.QueryChanged(""))
            advanceUntilIdle()
            assertEquals(DEFAULT_PAGE_SIZE, vm.contactsUiState.value.contacts.size)
            assertFalse(vm.contactsUiState.value.endReached)

            // Load page 2 -> empty => endReached should become true
            vm.submitAction(ContactsAction.ListEndReached)
            advanceUntilIdle()
            assertTrue(vm.contactsUiState.value.endReached)

            // Try again -> should NOT call page 3
            vm.submitAction(ContactsAction.ListEndReached)
            advanceUntilIdle()

            coVerify(exactly = 1) { getContacts(1, DEFAULT_PAGE_SIZE, false) }
            coVerify(exactly = 1) { getContacts(2, DEFAULT_PAGE_SIZE, false) }
            coVerify(exactly = 0) { getContacts(3, DEFAULT_PAGE_SIZE, false) }

            job.cancel()
        }

    @Test
    fun `Short first page immediately sets endReached and prevents pagination`() =
        runTest {
            // First page smaller than page size -> endReached = true right away
            val shortPage = users(1..10) // 10 < 20

            coEvery { getContacts(1, DEFAULT_PAGE_SIZE, false) } returns flowLoadingSuccess(shortPage)

            val vm = vm()
            val job = collectState(vm)

            vm.submitAction(ContactsAction.QueryChanged(""))
            advanceUntilIdle()

            val state = vm.contactsUiState.value
            assertEquals(10, state.contacts.size)
            assertTrue(state.endReached)

            // ListEndReached must not trigger page 2
            vm.submitAction(ContactsAction.ListEndReached)
            advanceUntilIdle()

            coVerify(exactly = 1) { getContacts(1, DEFAULT_PAGE_SIZE, false) }
            coVerify(exactly = 0) { getContacts(2, DEFAULT_PAGE_SIZE, false) }

            job.cancel()
        }

    @Test
    fun `QueryChanged non-blank uses search use case and not the paged endpoint`() =
        runTest {
            val result = users(1..7)
            coEvery { searchContacts("abc") } returns flowOf(Result.Loading, Result.Success(result))

            val vm = vm()
            val job = collectState(vm)

            vm.submitAction(ContactsAction.QueryChanged("abc"))
            advanceUntilIdle()

            val state = vm.contactsUiState.value
            assertEquals(7, state.contacts.size)
            assertEquals("abc", state.query)
            assertFalse(state.isLoadingFirstPage)
            assertFalse(state.loading)

            // No paged call should have been made
            coVerify(exactly = 0) { getContacts(any(), any(), any()) }
            job.cancel()
        }

    @Test
    fun `Error from paged endpoint emits effect and sets flags`() =
        runTest {
            coEvery {
                getContacts(
                    1,
                    DEFAULT_PAGE_SIZE,
                    false,
                )
            } returns flowError(NetworkError.ServerError)

            val vm = vm()
            val job = collectState(vm)

            val effects = mutableListOf<ContactsEffect>()
            val effectJob = launch { vm.effects.collect { effects += it } }

            vm.submitAction(ContactsAction.QueryChanged(""))
            advanceUntilIdle()

            // Effect
            val eff = effects.firstOrNull() as? ContactsEffect.ShowError
            assertNotNull(eff)
            assertEquals(NetworkError.ServerError, eff.error)

            // State flags
            val state = vm.contactsUiState.value
            assertTrue(state.endReached) // set to true in Failed to stop loading more
            assertFalse(state.isLoadingFirstPage)
            assertFalse(state.loading)

            effectJob.cancel()
            job.cancel()
        }

    @Test
    fun `ContactClicked navigates to ContactDetail`() =
        runTest {
            val vm = vm()
            val job = collectState(vm)

            vm.submitAction(ContactsAction.ContactClicked("id-42"))
            advanceUntilIdle()

            coVerify(exactly = 1) { navigator.navigate(Route.ContactDetail("id-42"), any()) }
            job.cancel()
        }
}
