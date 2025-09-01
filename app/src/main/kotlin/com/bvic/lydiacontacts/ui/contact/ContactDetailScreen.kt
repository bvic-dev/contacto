package com.bvic.lydiacontacts.ui.contact

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ContactDetailScreen(
    modifier: Modifier = Modifier,
    contactDetailViewModel: ContactDetailViewModel = hiltViewModel(),
) {
    val state by contactDetailViewModel.state.collectAsStateWithLifecycle()
    Scaffold {
        Text("user id : $state", modifier = Modifier.padding(it))
    }
}
