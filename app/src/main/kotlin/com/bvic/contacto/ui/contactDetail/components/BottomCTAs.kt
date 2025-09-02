package com.bvic.contacto.ui.contactDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bvic.contacto.R

@Composable
fun BottomCTAs(
    onMessage: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    ) {
        Button(
            onClick = onMessage,
            modifier =
                Modifier
                    .weight(1f)
                    .height(50.dp),
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.contact_detail_message),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        FilledTonalButton(
            onClick = onFavorite,
            modifier =
                Modifier
                    .weight(1f)
                    .height(50.dp),
        ) {
            Row {
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.contact_detail_favorites),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview(name = "BottomCTAs - Light", showBackground = true)
@Composable
private fun PreviewBottomCTAsLight() {
    BottomCTAs(onMessage = {}, onFavorite = {})
}
