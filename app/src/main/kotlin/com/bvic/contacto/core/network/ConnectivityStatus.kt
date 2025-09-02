package com.bvic.contacto.core.network

sealed interface ConnectivityStatus {
    data object Available : ConnectivityStatus

    data object Unavailable : ConnectivityStatus

    data object Losing : ConnectivityStatus

    data object Lost : ConnectivityStatus
}
