package com.bvic.lydiacontacts.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.lydiacontacts.core.Result
import com.bvic.lydiacontacts.domain.usecase.GetContactsUseCase
import com.bvic.lydiacontacts.domain.usecase.SearchContactsUseCase
import com.bvic.lydiacontacts.ui.shared.navigation.Navigator
import com.bvic.lydiacontacts.ui.shared.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val DEFAULT_PAGE_SIZE = 20

@HiltViewModel
class ContactsViewModel
    @Inject
    constructor(
        private val getContactsUseCase: GetContactsUseCase,
        private val searchContactsUseCase: SearchContactsUseCase,
        private val navigator: Navigator,
    ) : ViewModel() {
        private val actions =
            MutableSharedFlow<ContactsAction>(
                extraBufferCapacity = 32,
            )

        private val pageRequests = MutableStateFlow(0)

        private val _effects =
            MutableSharedFlow<ContactsEffect>(
                extraBufferCapacity = 1,
            )
        val effects = _effects.asSharedFlow()

        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        private val partials: Flow<ContactsPartial> =
            actions
                .onStart { emit(ContactsAction.ListEndReached) }
                .flatMapConcat { action ->
                    when (action) {
                        is ContactsAction.QueryChanged -> handleQueryChanged(action)
                        is ContactsAction.ListEndReached -> handleListEndReached()
                        is ContactsAction.ContactClicked -> handleContactClicked(action)
                        is ContactsAction.PullToRefresh -> handleListPullToRefresh()
                    }
                }.catch { e ->
                    // TODO error
                    // emit(ContactsPartial.Failed(e))
                }

        val contactsUiState: StateFlow<ContactsState> =
            partials
                .scan(ContactsState()) { state, partial -> reduce(prev = state, change = partial) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = ContactsState(),
                )

        private fun handleQueryChanged(action: ContactsAction.QueryChanged): Flow<ContactsPartial> =
            flow {
                emit(ContactsPartial.QuerySet(action.value))

                if (action.value.isBlank()) {
                    // Reset pagination et charge la première page
                    pageRequests.value = 1
                    emitAll(
                        getContactsUseCase(pageRequests.value, DEFAULT_PAGE_SIZE).map { res ->
                            when (res) {
                                is Result.Loading -> ContactsPartial.Loading
                                is Result.Success ->
                                    ContactsPartial.PageLoaded(
                                        pageRequests.value,
                                        res.data,
                                    )

                                is Result.Error -> ContactsPartial.Failed(res.error)
                            }
                        },
                    )
                } else {
                    emitAll(
                        searchContactsUseCase(action.value).map { res ->
                            when (res) {
                                is Result.Loading -> ContactsPartial.Loading
                                is Result.Success -> ContactsPartial.SearchLoaded(res.data)
                                is Result.Error -> ContactsPartial.Failed(res.error)
                            }
                        },
                    )
                }
            }

        private fun handleListEndReached(): Flow<ContactsPartial> =
            flow {
                val current = contactsUiState.value
                val canFetch = !current.loading && !current.endReached && current.query.isEmpty()
                if (canFetch) {
                    pageRequests.value = pageRequests.value + 1
                    emitAll(
                        getContactsUseCase(pageRequests.value, DEFAULT_PAGE_SIZE).map { res ->
                            when (res) {
                                is Result.Loading -> ContactsPartial.Loading
                                is Result.Success ->
                                    ContactsPartial.PageLoaded(pageRequests.value, res.data)

                                is Result.Error -> ContactsPartial.Failed(res.error)
                            }
                        },
                    )
                } else {
                    emitAll(emptyFlow())
                }
            }

        private fun handleContactClicked(action: ContactsAction.ContactClicked): Flow<ContactsPartial> =
            flow {
                navigator.navigate(destination = Route.ContactDetail(action.id))
                emitAll(emptyFlow())
            }

        private fun handleListPullToRefresh(): Flow<ContactsPartial> =
            flow {
                emit(ContactsPartial.Refreshing)
                pageRequests.value = 1
                emitAll(
                    getContactsUseCase(
                        pageRequests.value,
                        DEFAULT_PAGE_SIZE,
                        forceRefresh = true,
                    ).map { res ->
                        when (res) {
                            is Result.Loading -> ContactsPartial.Loading
                            is Result.Success ->
                                ContactsPartial.PageLoaded(pageRequests.value, res.data)

                            is Result.Error -> ContactsPartial.Failed(res.error)
                        }
                    },
                )
            }

        fun submitAction(action: ContactsAction) {
            viewModelScope.launch { actions.emit(action) }
        }
    }
