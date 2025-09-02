package com.bvic.lydiacontacts.ui.contacts.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.bvic.lydiacontacts.ui.shared.components.LydiaContactsLoader
import com.bvic.lydiacontacts.ui.shared.extension.shimmerBackground
import com.bvic.lydiacontacts.ui.shared.preview.SharedTransitionPreviewHarness
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContactRow(
    id: String,
    name: String,
    email: String?,
    pictureThumbnailUrl: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        with(sharedTransitionScope) {
            SubcomposeAsyncImage(
                model = pictureThumbnailUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "image-$id"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ).size(48.dp)
                        .clip(CircleShape),
                loading = {
                    LydiaContactsLoader()
                },
                error = {
                    EmptyPicture(name)
                },
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            with(sharedTransitionScope) {
                Text(
                    modifier =
                        Modifier.sharedBounds(
                            rememberSharedContentState(key = "name-$id"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                    text = name.ifBlank { "Indéfinie" },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            email?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun EmptyPicture(name: String) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.firstOrNull()?.toString() ?: "?",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun ContactRowShimmer() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .shimmerBackground(CircleShape),
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier =
                    Modifier
                        .height(16.dp)
                        .width(128.dp)
                        .shimmerBackground(),
            )

            Spacer(
                Modifier
                    .height(10.dp)
                    .width(200.dp)
                    .shimmerBackground(),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    name = "Contact Row – Light",
    showBackground = true,
)
@Preview(
    name = "Contact Row – Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewContactRow() {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            Column {
                ContactRow(
                    id = "123",
                    name = "Alex Martin",
                    email = "alex.martin@example.com",
                    pictureThumbnailUrl = null,
                    sharedTransitionScope = sts,
                    animatedVisibilityScope = avs,
                    onClick = {},
                )
                ContactRow(
                    id = "345",
                    name = "",
                    email = "alex.martin@example.com",
                    pictureThumbnailUrl = null,
                    sharedTransitionScope = sts,
                    animatedVisibilityScope = avs,
                    onClick = {},
                )
            }
        }
    }
}

@Preview(name = "Shimmer - Light", showBackground = true)
@Preview(
    name = "Shimmer - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun PreviewContactRowShimmer() {
    LydiaContactsTheme { ContactRowShimmer() }
}
