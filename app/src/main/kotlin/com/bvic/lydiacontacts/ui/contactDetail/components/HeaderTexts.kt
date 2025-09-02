package com.bvic.lydiacontacts.ui.contactDetail.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bvic.lydiacontacts.R
import com.bvic.lydiacontacts.ui.shared.preview.SharedTransitionPreviewHarness
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HeaderTexts(
    id: String,
    name: String?,
    age: Int?,
    nationality: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        name?.takeIf { it.isNotBlank() }?.let {
            Text(
                modifier =
                    Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "name-$id"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ).semantics { heading() },
                text = it,
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
    val ageText =
        age?.takeIf { it > 0 }?.let {
            pluralStringResource(R.plurals.age_years, it, it)
        }
    val nationalityText = nationality?.ifBlank { null }
    val subtitle = listOfNotNull(ageText, nationalityText).joinToString(" · ")

    if (subtitle.isNotEmpty()) {
        Text(
            subtitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(name = "HeaderTexts - Light", showBackground = true)
@Composable
private fun PreviewHeaderTextsLight() {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeaderTexts(
                    id = "123",
                    name = "Alice Dupont",
                    age = 31,
                    nationality = "FR",
                    sharedTransitionScope = sts,
                    animatedVisibilityScope = avs,
                )
            }
        }
    }
}
