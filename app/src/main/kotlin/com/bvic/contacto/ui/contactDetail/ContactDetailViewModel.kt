package com.bvic.contacto.ui.contactDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bvic.contacto.core.Result
import com.bvic.contacto.domain.usecase.GetContactUseCase
import com.bvic.contacto.ui.shared.navigation.Navigator
import com.bvic.contacto.ui.shared.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getContactUseCase: GetContactUseCase,
        private val navigator: Navigator,
    ) : ViewModel() {
        val contactId: String = savedStateHandle.toRoute<Route.ContactDetail>().id

        private val actions =
            MutableSharedFlow<ContactDetailAction>(
                extraBufferCapacity = 32,
            )

        private val _effects =
            MutableSharedFlow<ContactDetailEffect>(
                extraBufferCapacity = 1,
            )
        val effects = _effects.asSharedFlow()

        val contactsUiState: StateFlow<ContactDetailState> =
            getContactUseCase(contactId)
                .map {
                    when (it) {
                        is Result.Error -> {
                            _effects.emit(ContactDetailEffect.ShowError(it.error))
                            ContactDetailPartial.Failed(it.error)
                        }
                        is Result.Loading -> ContactDetailPartial.Loading
                        is Result.Success -> ContactDetailPartial.ContactLoaded(it.data)
                    }
                }.scan(ContactDetailState()) { state, partial -> reduce(prev = state, change = partial) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = ContactDetailState(),
                )

        init {
            viewModelScope.launch {
                actions.collect {
                    when (it) {
                        ContactDetailAction.CallClicked ->
                            navigator.openCallApp(
                                phoneNumber = contactsUiState.value.contact?.phone ?: return@collect,
                            )

                        ContactDetailAction.MailClicked ->
                            navigator.openMailApp(
                                email = contactsUiState.value.contact?.email ?: return@collect,
                            )

                        ContactDetailAction.MapClicked ->
                            navigator.openGoogleMapApp(
                                latitude = contactsUiState.value.contact?.latitude ?: 0.0,
                                longitude = contactsUiState.value.contact?.longitude ?: 0.0,
                                address = contactsUiState.value.contact?.address ?: return@collect,
                            )

                        ContactDetailAction.MessageClicked ->
                            navigator.openSmsApp(
                                phoneNumber = contactsUiState.value.contact?.phone ?: return@collect,
                            )

                        ContactDetailAction.BackClicked -> navigator.navigateBack()
                    }
                }
            }
        }

        fun submitAction(action: ContactDetailAction) {
            viewModelScope.launch { actions.emit(action) }
        }
    }
