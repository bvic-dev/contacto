package com.bvic.lydiacontacts.ui.contact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.bvic.lydiacontacts.ui.shared.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val contactId: String = savedStateHandle.toRoute<Route.ContactDetail>().id

        private val _state = MutableStateFlow(contactId)
        val state = _state.asStateFlow()
    }
