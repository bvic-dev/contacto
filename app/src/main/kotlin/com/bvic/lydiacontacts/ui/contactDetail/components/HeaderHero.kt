package com.bvic.lydiacontacts.ui.contactDetail.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.bvic.lydiacontacts.ui.shared.components.LydiaContactsLoader
import com.bvic.lydiacontacts.ui.shared.preview.SharedTransitionPreviewHarness
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HeaderHero(
    modifier: Modifier = Modifier,
    id: String,
    scrollValue: Int,
    pictureLarge: String?,
    name: String?,
    headerHeight: Dp,
    nameForContentDesc: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(headerHeight),
    ) {
        with(sharedTransitionScope) {
            SubcomposeAsyncImage(
                model = pictureLarge,
                contentDescription =
                    nameForContentDesc?.let { "Photo de $it" }
                        ?: "Photo du contact",
                modifier =
                    Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "image-$id"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            renderInOverlayDuringTransition = false,
                        ).matchParentSize()
                        .blur(
                            if (scrollValue > 40) 8.dp else 0.dp,
                        ),
                contentScale = ContentScale.Crop,
                loading = {
                    LydiaContactsLoader()
                },
                error = {
                    EmptyPicture(
                        name = name ?: "",
                        headerHeight = headerHeight,
                    )
                },
            )
        }

        // Scrim vertical pour lisibilité
        Box(
            Modifier
                .matchParentSize()
                .drawWithCache {
                    val brush =
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.35f),
                            0.7f to Color.Transparent,
                        )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush)
                    }
                },
        )
    }
}

@Composable
private fun EmptyPicture(
    name: String,
    headerHeight: Dp,
) {
    Box(
        modifier =
            Modifier
                .height(headerHeight)
                .background(
                    MaterialTheme.colorScheme.primary,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.firstOrNull()?.toString() ?: "?",
            color = Color.White,
            fontSize = 125.sp,
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
                HeaderHero(
                    id = "123",
                    pictureLarge = null,
                    scrollValue = 0,
                    name = "Alice Dupont",
                    headerHeight = 300.dp,
                    nameForContentDesc = "Alice Dupont",
                    sharedTransitionScope = sts,
                    animatedVisibilityScope = avs,
                )
            }
        }
    }
}
