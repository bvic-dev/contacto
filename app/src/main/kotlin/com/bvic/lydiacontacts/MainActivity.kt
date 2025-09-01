package com.bvic.lydiacontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bvic.lydiacontacts.ui.shared.navigation.Navigation
import com.bvic.lydiacontacts.ui.shared.navigation.NavigationAction
import com.bvic.lydiacontacts.ui.shared.navigation.NavigationGraph
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LydiaContactsTheme {
                val navController: NavHostController = rememberNavController()
                val mainViewModel = hiltViewModel<MainViewModel>()
                val startRoute by mainViewModel.navigator.startRoute.collectAsStateWithLifecycle()
                val navigationAction by mainViewModel.navigator.navigationActions.collectAsStateWithLifecycle(
                    NavigationAction.Idle,
                )
                Navigation(
                    navController = navController,
                    navigationAction = navigationAction,
                )
                NavigationGraph(
                    navController = navController,
                    startRoute = startRoute,
                )
            }
        }
    }
}
