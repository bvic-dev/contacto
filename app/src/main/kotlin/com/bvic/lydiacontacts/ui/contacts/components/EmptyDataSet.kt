package com.bvic.lydiacontacts.ui.contacts.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bvic.lydiacontacts.R
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme

@Composable
fun EmptyDataSet(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.empty_ghost),
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        modifier = modifier.size(250.dp),
        composition = composition,
        progress = { progress },
    )
}

@Composable
@Preview(
    name = "Empty Data Set – Light",
    showBackground = true,
)
@Preview(
    name = "Empty Data Set – Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
fun PreviewEmptyDataSet() {
    LydiaContactsTheme {
        EmptyDataSet()
    }
}
