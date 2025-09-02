package com.bvic.contacto.ui.shared.connectivity

import com.bvic.contacto.core.network.ConnectivityStatus
import com.bvic.contacto.domain.usecase.ObserveConnectivityUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectivityViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var flow: MutableSharedFlow<ConnectivityStatus>
    private lateinit var useCase: ObserveConnectivityUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        flow = MutableSharedFlow(replay = 1, extraBufferCapacity = 8)
        useCase = mockk()
        every { useCase.invoke() } returns flow.asSharedFlow()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when status is Available from start, state stays online without backOnline banner`() =
        runTest {
            val vm = ConnectivityViewModel(observeConnectivity = useCase)

            flow.tryEmit(ConnectivityStatus.Available)
            advanceUntilIdle()

            val state = vm.state.value
            assertFalse(state.isOffline)
            assertFalse(state.showBackOnline)
        }

    @Test
    fun `when status becomes Unavailable, state is offline and no backOnline banner`() =
        runTest {
            val vm = ConnectivityViewModel(observeConnectivity = useCase)

            flow.tryEmit(ConnectivityStatus.Unavailable)
            advanceUntilIdle()

            val state = vm.state.value
            assertTrue(state.isOffline)
            assertFalse(state.showBackOnline)
        }

    @Test
    fun `when status becomes Losing, state is offline and no backOnline banner`() =
        runTest {
            val vm = ConnectivityViewModel(observeConnectivity = useCase)

            flow.tryEmit(ConnectivityStatus.Losing)
            advanceUntilIdle()

            val state = vm.state.value
            assertTrue(state.isOffline)
            assertFalse(state.showBackOnline)
        }

    @Test
    fun `when status becomes Lost, state is offline and no backOnline banner`() =
        runTest {
            val vm = ConnectivityViewModel(observeConnectivity = useCase)

            flow.tryEmit(ConnectivityStatus.Lost)
            advanceUntilIdle()

            val state = vm.state.value
            assertTrue(state.isOffline)
            assertFalse(state.showBackOnline)
        }

    @Test
    fun `when already online and receives Available again, backOnline banner stays hidden`() =
        runTest {
            val vm = ConnectivityViewModel(observeConnectivity = useCase)

            flow.tryEmit(ConnectivityStatus.Available)
            advanceUntilIdle()
            assertFalse(vm.state.value.isOffline)
            assertFalse(vm.state.value.showBackOnline)

            // another Available while already online
            flow.tryEmit(ConnectivityStatus.Available)
            advanceUntilIdle()
            assertFalse(vm.state.value.isOffline)
            assertFalse(vm.state.value.showBackOnline)
        }
}
