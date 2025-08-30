package com.bvic.lydiacontacts.ui.shared.extension

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val last = layoutInfo.visibleItemsInfo.lastOrNull() ?: return false
    val total = layoutInfo.totalItemsCount
    if (total == 0) return false
    return last.index >= total - 1 - buffer
}
