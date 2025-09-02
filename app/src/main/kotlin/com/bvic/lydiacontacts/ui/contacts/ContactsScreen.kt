package com.bvic.lydiacontacts.ui.contacts

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bvic.lydiacontacts.R
import com.bvic.lydiacontacts.domain.model.RandomUser
import com.bvic.lydiacontacts.ui.contacts.components.ContactRow
import com.bvic.lydiacontacts.ui.contacts.components.ContactRowShimmer
import com.bvic.lydiacontacts.ui.contacts.components.EmptyDataSet
import com.bvic.lydiacontacts.ui.shared.connectivity.ConnectivityBanner
import com.bvic.lydiacontacts.ui.shared.connectivity.ConnectivityUiState
import com.bvic.lydiacontacts.ui.shared.connectivity.ConnectivityViewModel
import com.bvic.lydiacontacts.ui.shared.extension.reachedBottom
import com.bvic.lydiacontacts.ui.shared.preview.SharedTransitionPreviewHarness
import com.bvic.lydiacontacts.ui.shared.snackbar.CollectErrors
import com.bvic.lydiacontacts.ui.shared.snackbar.ErrorSnackbar
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContactsScreen(
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    connectivityViewModel: ConnectivityViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val contactsUiState by contactsViewModel.contactsUiState.collectAsStateWithLifecycle()
    val contactListState = rememberLazyListState()

    val connectivityState by connectivityViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(contactListState) {
        snapshotFlow { contactListState.reachedBottom() }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                contactsViewModel.submitAction(ContactsAction.ListEndReached)
            }
    }

    CollectErrors(
        errors =
            remember(contactsViewModel.effects) {
                contactsViewModel.effects.map { (it as? ContactsEffect.ShowError)?.error }
            },
        host = snackbarHostState,
    )

    ContactsScreen(
        contactListState = contactListState,
        contacts = contactsUiState.contacts,
        isRefreshing = contactsUiState.isRefreshing,
        isScrollEnabled = !contactsUiState.isLoadingFirstPage,
        showInitialShimmers = contactsUiState.isLoadingFirstPage,
        showPaginationShimmers = contactsUiState.isLoadingNextPage,
        showEmptyDataSet = contactsUiState.showEmptyDataSet,
        queryValue = contactsUiState.query,
        snackbarHostState = snackbarHostState,
        connectivityState = connectivityState,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onQueryChanged = { query ->
            contactsViewModel.submitAction(
                action = ContactsAction.QueryChanged(query),
            )
        },
        onContactClick = { id ->
            contactsViewModel.submitAction(
                action = ContactsAction.ContactClicked(id),
            )
        },
        onRefresh = {
            contactsViewModel.submitAction(
                action = ContactsAction.PullToRefresh,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    contacts: List<RandomUser> = listOf(),
    contactListState: LazyListState = rememberLazyListState(),
    isScrollEnabled: Boolean = true,
    showInitialShimmers: Boolean = false,
    showPaginationShimmers: Boolean = false,
    isRefreshing: Boolean = false,
    showEmptyDataSet: Boolean = false,
    queryValue: String,
    snackbarHostState: SnackbarHostState,
    connectivityState: ConnectivityUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onQueryChanged: (String) -> Unit,
    onContactClick: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.contacts_title),
                        fontSize = 35.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    ErrorSnackbar(
                        data = it,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            ConnectivityBanner(state = connectivityState)
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = contactListState,
                    userScrollEnabled = isScrollEnabled,
                ) {
                    item {
                        val contentDescriptionSearch = stringResource(R.string.a11y_search_field)

                        OutlinedTextField(
                            value = queryValue,
                            onValueChange = onQueryChanged,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .semantics {
                                        contentDescription = contentDescriptionSearch
                                    },
                            placeholder = { Text(stringResource(R.string.contacts_search_placeholder)) },
                            leadingIcon = { Icon(Icons.Outlined.Search, null) },
                            trailingIcon = {
                                if (queryValue.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChanged("") }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Clear,
                                            contentDescription = stringResource(R.string.a11y_clear_query),
                                            modifier = Modifier.padding(end = 16.dp),
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(50),
                        )
                    }

                    if (showEmptyDataSet) {
                        item {
                            EmptyDataSet(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .align(alignment = Alignment.Center),
                            )
                        }
                        return@LazyColumn
                    }

                    if (showInitialShimmers) {
                        repeat(20) {
                            item {
                                ContactRowShimmer()
                            }
                        }
                        return@LazyColumn
                    }
                    items(
                        items = contacts,
                        key = { randomUser -> randomUser.id },
                    ) { randomUser ->
                        ContactRow(
                            id = randomUser.id,
                            name = randomUser.fullName,
                            email = randomUser.email,
                            pictureThumbnailUrl = randomUser.pictureThumbnailUrl,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onClick = {
                                onContactClick(randomUser.id)
                            },
                        )
                    }

                    if (showPaginationShimmers) {
                        repeat(2) {
                            item {
                                ContactRowShimmer()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun fakeUsers(count: Int = 10): List<RandomUser> =
    List(count) { idx ->
        RandomUser(
            id = "id-$idx",
            fullName = "Utilisateur $idx",
            email = "user$idx@mail.com",
            phone = "01 23 45 67 89",
            pictureThumbnailUrl = null,
            pictureLarge = null,
            age = 25,
            nationality = "Francais",
            address = "123 rue de la paix",
            latitude = 48.8566,
            longitude = 2.3522,
            birthDate = Instant.fromEpochMilliseconds(0),
        )
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    name = "Contacts – Loading first page",
    showBackground = true,
)
@Composable
private fun PreviewContactScreenLoadingFirstPage() {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            ContactsScreen(
                contacts = emptyList(),
                contactListState = rememberLazyListState(),
                isScrollEnabled = false,
                showInitialShimmers = true,
                showPaginationShimmers = false,
                queryValue = "",
                snackbarHostState = remember { SnackbarHostState() },
                connectivityState = ConnectivityUiState(),
                sharedTransitionScope = sts,
                animatedVisibilityScope = avs,
                onQueryChanged = {},
                onContactClick = {},
                onRefresh = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    name = "Contacts – With content",
    showBackground = true,
)
@Preview(
    name = "Contacts – With content (Dark)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewContactScreenWithContent() {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            ContactsScreen(
                contacts = fakeUsers(12),
                contactListState = rememberLazyListState(),
                isScrollEnabled = true,
                showInitialShimmers = false,
                showPaginationShimmers = false,
                queryValue = "",
                snackbarHostState = remember { SnackbarHostState() },
                connectivityState = ConnectivityUiState(),
                sharedTransitionScope = sts,
                animatedVisibilityScope = avs,
                onQueryChanged = {},
                onContactClick = {},
                onRefresh = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    name = "Contacts – Loading next page",
    showBackground = true,
)
@Composable
private fun PreviewContactScreenLoadingNextPage() {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            ContactsScreen(
                contacts = fakeUsers(5),
                isScrollEnabled = true,
                showInitialShimmers = false,
                showPaginationShimmers = true,
                connectivityState = ConnectivityUiState(),
                queryValue = "",
                snackbarHostState = remember { SnackbarHostState() },
                sharedTransitionScope = sts,
                animatedVisibilityScope = avs,
                onQueryChanged = {},
                onContactClick = {},
                onRefresh = {},
            )
        }
    }
}
