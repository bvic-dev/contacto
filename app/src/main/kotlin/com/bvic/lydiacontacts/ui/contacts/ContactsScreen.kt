package com.bvic.lydiacontacts.ui.contacts

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bvic.lydiacontacts.domain.model.RandomUser
import com.bvic.lydiacontacts.ui.contacts.components.ContactRow
import com.bvic.lydiacontacts.ui.contacts.components.ContactRowShimmer
import com.bvic.lydiacontacts.ui.shared.extension.reachedBottom
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun ContactsScreen(contactsViewModel: ContactsViewModel = hiltViewModel()) {
    val contactsUiState by contactsViewModel.contactsUiState.collectAsStateWithLifecycle()
    val contactListState = rememberLazyListState()

    LaunchedEffect(contactListState) {
        snapshotFlow { contactListState.reachedBottom() }
            .distinctUntilChanged()
            .filter { it }
            .collect { contactsViewModel.onListEndReached() }
    }

    ContactsScreen(
        contactListState = contactListState,
        contacts = contactsUiState.contacts,
        isScrollEnabled = !contactsUiState.isLoadingFirstPage,
        showInitialShimmers = contactsUiState.isLoadingFirstPage,
        showPaginationShimmers = contactsUiState.isLoadingNextPage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    contacts: List<RandomUser> = listOf(),
    contactListState: LazyListState = rememberLazyListState(),
    isScrollEnabled: Boolean = true,
    showInitialShimmers: Boolean = false,
    showPaginationShimmers: Boolean = false,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            state = contactListState,
            userScrollEnabled = isScrollEnabled,
        ) {
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    placeholder = { Text("Rechercher un contact") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    shape = RoundedCornerShape(50),
                )
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
                ContactRow(name = randomUser.fullName, email = randomUser.email, onClick = {})
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

private fun fakeUsers(count: Int = 10): List<RandomUser> =
    List(count) { idx ->
        RandomUser(
            id = "id-$idx",
            fullName = "Utilisateur $idx",
            email = "user$idx@mail.com",
            phone = "01 23 45 67 89",
            pictureUrl = null,
        )
    }

@Preview(
    name = "Contacts – Loading first page",
    showBackground = true,
)
@Composable
private fun PreviewContactScreenLoadingFirstPage() {
    LydiaContactsTheme {
        ContactsScreen(
            contacts = emptyList<RandomUser>(),
            contactListState = rememberLazyListState(),
            isScrollEnabled = false,
            showInitialShimmers = true,
            showPaginationShimmers = false,
        )
    }
}

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
        ContactsScreen(
            contacts = fakeUsers(12),
            contactListState = rememberLazyListState(),
            isScrollEnabled = true,
            showInitialShimmers = false,
            showPaginationShimmers = false,
        )
    }
}

@Preview(
    name = "Contacts – Loading next page",
    showBackground = true,
)
@Composable
private fun PreviewContactScreenLoadingNextPage() {
    LydiaContactsTheme {
        ContactsScreen(
            contacts = fakeUsers(5),
            isScrollEnabled = true,
            showInitialShimmers = false,
            showPaginationShimmers = true,
        )
    }
}
