package com.bvic.contacto.ui.contactDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bvic.contacto.R

@Composable
fun QuickActions(
    onCall: () -> Unit,
    onMail: () -> Unit,
    onMap: () -> Unit,
    name: String?,
) {
    val callDesc =
        name?.let { stringResource(R.string.a11y_call_name, it) }
            ?: stringResource(R.string.contact_detail_call)
    val mailDesc =
        name?.let { stringResource(R.string.a11y_email_name, it) }
            ?: stringResource(R.string.contact_detail_email_action)
    val mapDesc = stringResource(R.string.a11y_open_address_in_maps)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        TonalActionButton(Icons.Filled.Call, onCall, callDesc, Modifier.weight(1f))
        TonalActionButton(Icons.Filled.Email, onMail, mailDesc, Modifier.weight(1f))
        TonalActionButton(Icons.Filled.Place, onMap, mapDesc, Modifier.weight(1f))
    }
}

@Composable
fun TonalActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick,
        modifier =
            modifier
                .height(40.dp)
                .semantics { this.contentDescription = contentDesc },
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp))
    }
}

@Preview(name = "QuickActions - Light", showBackground = true)
@Composable
private fun PreviewQuickActionsLight() {
    QuickActions(onCall = {}, onMail = {}, onMap = {}, name = "Thibault Martin")
}
