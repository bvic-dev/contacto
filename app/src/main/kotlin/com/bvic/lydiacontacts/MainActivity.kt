package com.bvic.lydiacontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bvic.lydiacontacts.ui.contacts.ContactsScreen
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LydiaContactsTheme {
                ContactsScreen()
            }
        }
    }
}
