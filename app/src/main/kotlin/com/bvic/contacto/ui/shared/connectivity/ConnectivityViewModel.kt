package com.bvic.contacto.ui.shared.connectivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.contacto.core.network.ConnectivityStatus
import com.bvic.contacto.domain.usecase.ObserveConnectivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel
    @Inject
    constructor(
        observeConnectivity: ObserveConnectivityUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ConnectivityUiState())
        val state: StateFlow<ConnectivityUiState> = _state

        private var backOnlineJob: Job? = null

        init {
            viewModelScope.launch {
                observeConnectivity().collect { status ->
                    when (status) {
                        ConnectivityStatus.Available -> handleBackOnline()
                        ConnectivityStatus.Unavailable,
                        ConnectivityStatus.Losing,
                        ConnectivityStatus.Lost,
                        -> {
                            backOnlineJob?.cancel()
                            _state.update { it.copy(isOffline = true, showBackOnline = false) }
                        }
                    }
                }
            }
        }

        private fun handleBackOnline() {
            val wasOffline = _state.value.isOffline
            _state.update { it.copy(isOffline = false) }

            if (wasOffline) {
                backOnlineJob?.cancel()
                backOnlineJob =
                    viewModelScope.launch {
                        _state.update { it.copy(showBackOnline = true) }
                        delay(2000L)
                        _state.update { it.copy(showBackOnline = false) }
                    }
            } else {
                _state.update { it.copy(showBackOnline = false) }
            }
        }
    }
