package com.bvic.contacto.ui.shared.connectivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bvic.contacto.R
import com.bvic.contacto.ui.shared.theme.ContactoTheme

@Composable
fun ConnectivityBanner(
    state: ConnectivityUiState,
    modifier: Modifier = Modifier,
) {
    val visible = state.isOffline || state.showBackOnline
    val isOffline = state.isOffline

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = fadeOut() + slideOutVertically { -it },
    ) {
        val bg =
            if (isOffline) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }

        val fg =
            if (isOffline) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onTertiaryContainer
            }

        Surface(
            color = bg,
            contentColor = fg,
            modifier =
                modifier
                    .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .heightIn(min = 40.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
            ) {
                val a11yConnectivityOffline =
                    stringResource(R.string.a11y_connectivity_offline_announce)
                val a11yConnectivityOnline =
                    stringResource(R.string.a11y_connectivity_back_online_announce)

                Icon(
                    imageVector = if (isOffline) Icons.Outlined.WifiOff else Icons.Outlined.Wifi,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text =
                        if (isOffline) {
                            stringResource(R.string.connectivity_offline)
                        } else {
                            stringResource(R.string.connectivity_back_online)
                        },
                    style = MaterialTheme.typography.labelLarge,
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .semantics {
                                if (isOffline) {
                                    error(a11yConnectivityOffline)
                                } else {
                                    error(a11yConnectivityOnline)
                                }
                            },
                )
            }
        }
    }
}

@Preview(
    name = "Offline",
    showBackground = true,
)
@Composable
private fun PreviewConnectivityBannerOffline() {
    MaterialTheme {
        ConnectivityBanner(
            state =
                ConnectivityUiState(
                    isOffline = true,
                    showBackOnline = false,
                ),
        )
    }
}

@Preview(
    name = "Back online",
    showBackground = true,
)
@Composable
private fun PreviewConnectivityBannerBackOnline() {
    ContactoTheme {
        ConnectivityBanner(
            state =
                ConnectivityUiState(
                    isOffline = false,
                    showBackOnline = true,
                ),
        )
    }
}

@Preview(
    name = "Hidden",
    showBackground = true,
)
@Composable
private fun PreviewConnectivityBannerHidden() {
    ContactoTheme {
        ConnectivityBanner(
            state =
                ConnectivityUiState(
                    isOffline = false,
                    showBackOnline = false,
                ),
        )
    }
}
