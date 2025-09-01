package com.bvic.lydiacontacts.ui.contactDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun QuickActions(
    onCall: () -> Unit,
    onMail: () -> Unit,
    onMap: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        TonalActionButton(Icons.Filled.Call, onCall, Modifier.weight(1f))
        TonalActionButton(Icons.Filled.Email, onMail, Modifier.weight(1f))
        TonalActionButton(Icons.Filled.Place, onMap, Modifier.weight(1f))
    }
}

@Composable
fun TonalActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(onClick, modifier.height(50.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp))
        Spacer(Modifier.width(8.dp))
    }
}

@Preview(name = "QuickActions - Light", showBackground = true)
@Composable
private fun PreviewQuickActionsLight() {
    QuickActions(onCall = {}, onMail = {}, onMap = {})
}
