package com.bvic.lydiacontacts.ui.contactDetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bvic.lydiacontacts.R

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String?,
) {
    if (value.isNullOrBlank()) return
    val container = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    val outline = MaterialTheme.colorScheme.outlineVariant
    val localClipboard = LocalClipboardManager.current

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(
                    androidx.compose.foundation.shape
                        .RoundedCornerShape(16.dp),
                ).clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.a11y_copy_to_clipboard),
                ) {
                    localClipboard.setText(AnnotatedString(value))
                },
        shape =
            androidx.compose.foundation.shape
                .RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, outline),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            androidx.compose.foundation.layout.Column {
                Text(
                    title.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview(name = "InfoCard - Light", showBackground = true)
@Composable
private fun PreviewInfoCardLight() {
    InfoCard(icon = Icons.Filled.Email, title = "Email", value = "test@example.com")
}
