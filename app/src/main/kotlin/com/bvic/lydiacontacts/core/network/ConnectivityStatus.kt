package com.bvic.lydiacontacts.core.network

sealed interface ConnectivityStatus {
    data object Available : ConnectivityStatus

    data object Unavailable : ConnectivityStatus

    data object Losing : ConnectivityStatus

    data object Lost : ConnectivityStatus
}
