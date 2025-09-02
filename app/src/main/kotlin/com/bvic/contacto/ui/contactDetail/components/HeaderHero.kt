package com.bvic.contacto.ui.contactDetail.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.bvic.contacto.R
import com.bvic.contacto.ui.shared.components.ContactoLoader
import com.bvic.contacto.ui.shared.preview.SharedTransitionPreviewHarness
import com.bvic.contacto.ui.shared.theme.ContactoTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HeaderHero(
    modifier: Modifier = Modifier,
    id: String,
    scrollValue: Int,
    pictureLarge: String?,
    name: String?,
    headerHeight: Dp,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(headerHeight),
    ) {
        with(sharedTransitionScope) {
            val imageContentDesc =
                name?.let { stringResource(R.string.contact_detail_photo_of, it) }
                    ?: stringResource(R.string.contact_detail_contact_photo)

            SubcomposeAsyncImage(
                model = pictureLarge,
                contentDescription = imageContentDesc,
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
                    ContactoLoader()
                },
                error = {
                    EmptyPicture(
                        name = name ?: "",
                        headerHeight = headerHeight,
                    )
                },
            )
        }

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
    ContactoTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeaderHero(
                    id = "123",
                    pictureLarge = null,
                    scrollValue = 0,
                    name = "Alice Dupont",
                    headerHeight = 300.dp,
                    sharedTransitionScope = sts,
                    animatedVisibilityScope = avs,
                )
            }
        }
    }
}
