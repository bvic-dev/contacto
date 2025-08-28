package com.bvic.lydiacontacts.feature.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.domain.usecase.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val DEFAULT_PAGE_SIZE = 20

@HiltViewModel
class ContactsViewModel
    @Inject
    constructor(
        getContactsUseCase: GetContactsUseCase,
    ) : ViewModel() {
        private val pageRequests = MutableSharedFlow<Int>(extraBufferCapacity = 1)
        private val currentPage = MutableStateFlow(0)
        private val initial = ContactsUiState()

        @OptIn(ExperimentalCoroutinesApi::class)
        val contactsUiState: StateFlow<ContactsUiState> =
            pageRequests
                .onStart { emit(1) }
                .flatMapConcat { page ->
                    getContactsUseCase(
                        page = page,
                        pageSize = DEFAULT_PAGE_SIZE,
                    ).map { result -> page to result }
                }.scan(initial) { state, (page, result) ->
                    when (result) {
                        is Result.Loading -> state.copy(loading = true, error = null)
                        is Result.Success -> {
                            currentPage.value = page
                            state.copy(
                                loading = false,
                                error = null,
                                contacts = if (page == 1) result.data else state.contacts + result.data,
                                endReached = result.data.isEmpty() || result.data.size < DEFAULT_PAGE_SIZE,
                            )
                        }

                        is Result.Error -> state.copy(loading = false, error = result.error)
                    }
                }.distinctUntilChanged()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = initial,
                )

        fun onListEndReached() {
            with(contactsUiState.value) {
                val canFetch = !loading && !endReached
                if (canFetch) {
                    val nextPage = currentPage.value + 1
                    pageRequests.tryEmit(nextPage)
                }
            }
        }
    }
