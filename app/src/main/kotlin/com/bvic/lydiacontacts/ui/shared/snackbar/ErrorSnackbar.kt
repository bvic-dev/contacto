package com.bvic.lydiacontacts.ui.shared.snackbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bvic.lydiacontacts.R
import com.bvic.lydiacontacts.core.Error
import com.bvic.lydiacontacts.core.network.NetworkError
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun ErrorSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(0.9f),
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (data.visuals.withDismissAction) {
                Spacer(Modifier.width(12.dp))
                TextButton(
                    onClick = { data.dismiss() },
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}

@Composable
fun CollectErrors(
    errors: Flow<Error?>,
    host: SnackbarHostState,
    duration: SnackbarDuration = SnackbarDuration.Long,
    withDismissAction: Boolean = true,
) {
    val context = LocalContext.current
    LaunchedEffect(errors) {
        errors.filterNotNull().collect { error ->
            host.showSnackbar(
                message = context.getString(errorToMessageRes(error)),
                withDismissAction = withDismissAction,
                duration = duration,
            )
        }
    }
}

@StringRes
private fun errorToMessageRes(error: Error): Int =
    when (error) {
        is NetworkError ->
            when (error) {
                NetworkError.RequestTimeout -> R.string.error_request_timeout
                NetworkError.Unauthorized -> R.string.error_unauthorized
                NetworkError.NoInternet -> R.string.error_no_internet
                NetworkError.NotFound -> R.string.error_not_found
                NetworkError.ServerError -> R.string.error_server
                NetworkError.Serialization -> R.string.error_serialization
                NetworkError.Unknown -> R.string.error_unknown
            }

        else -> R.string.error_generic
    }

// Implémentations fake pour la Preview
private data class PreviewSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

private class PreviewSnackbarData(
    override val visuals: SnackbarVisuals,
) : SnackbarData {
    override fun dismiss() { // no-op for preview
    }

    override fun performAction() { // no-op for preview
    }
}

@Preview(name = "Erreur - simple (avec dismiss)", showBackground = true, widthDp = 360)
@Composable
private fun PreviewErrorSnackbar_Simple() {
    LydiaContactsTheme {
        ErrorSnackbar(
            data =
                PreviewSnackbarData(
                    PreviewSnackbarVisuals(
                        message = "Une erreur est survenue. Réessaie.",
                        withDismissAction = true,
                    ),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        )
    }
}

@Preview(name = "Erreur - longue (wrap)", showBackground = true, widthDp = 360)
@Composable
private fun PreviewErrorSnackbar_Long() {
    LydiaContactsTheme {
        ErrorSnackbar(
            data =
                PreviewSnackbarData(
                    PreviewSnackbarVisuals(
                        message =
                            "Erreur serveur. Réessaie plus tard. Si le problème persiste, " +
                                "vérifie ta connexion ou relance l’application.",
                        withDismissAction = true,
                    ),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        )
    }
}

@Preview(name = "Erreur - sans dismiss", showBackground = true, widthDp = 360)
@Composable
private fun PreviewErrorSnackbar_NoDismiss() {
    LydiaContactsTheme {
        ErrorSnackbar(
            data =
                PreviewSnackbarData(
                    PreviewSnackbarVisuals(
                        message = "Pas de connexion internet.",
                        withDismissAction = false,
                    ),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        )
    }
}
