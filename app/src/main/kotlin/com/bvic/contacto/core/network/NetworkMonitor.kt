package com.bvic.contacto.core.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val status: Flow<ConnectivityStatus>
}
