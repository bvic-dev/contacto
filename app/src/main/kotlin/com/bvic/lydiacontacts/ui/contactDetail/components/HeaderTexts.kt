package com.bvic.lydiacontacts.ui.contactDetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HeaderTexts(
    name: String?,
    age: Int?,
    nationality: String?,
) {
    name?.takeIf { it.isNotBlank() }?.let {
        Text(it, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
    }

    val subtitle =
        remember(age, nationality) {
            val ageText = age?.takeIf { it > 0 }?.let { "$it ans" }
            val nationalityText = nationality?.ifBlank { null }
            listOfNotNull(ageText, nationalityText).joinToString(" · ")
        }

    if (subtitle.isNotEmpty()) {
        Text(
            subtitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "HeaderTexts - Light", showBackground = true)
@Composable
private fun PreviewHeaderTextsLight() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderTexts(name = "Alice Dupont", age = 31, nationality = "FR")
    }
}
