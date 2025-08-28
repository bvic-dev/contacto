package com.bvic.lydiacontacts.feature.contacts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bvic.lydiacontacts.domain.model.RandomUser

@Composable
fun ContactsScreen(contactsViewModel: ContactsViewModel = hiltViewModel()) {
    val contactsUiState by contactsViewModel.contactsUiState.collectAsStateWithLifecycle()
    val contactListState = rememberLazyListState()

    val reachedBottom: Boolean by remember {
        derivedStateOf { contactListState.reachedBottom() }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) contactsViewModel.onListEndReached()
    }

    LazyColumn(
        state = contactListState,
    ) {
        itemsIndexed(
            items = contactsUiState.contacts,
            key = { index, randomUser -> randomUser.id },
        ) { index, randomUser ->
            ContactItem(randomUser = randomUser)
            if (index < contactsUiState.contacts.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ContactItem(randomUser: RandomUser) {
    Text(text = randomUser.fullName)
}

fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val last = layoutInfo.visibleItemsInfo.lastOrNull() ?: return false
    val total = layoutInfo.totalItemsCount
    if (total == 0) return false
    return last.index >= total - 1 - buffer
}
