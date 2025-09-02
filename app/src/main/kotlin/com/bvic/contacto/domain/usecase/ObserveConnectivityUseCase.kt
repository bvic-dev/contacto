package com.bvic.contacto.domain.usecase

import com.bvic.contacto.core.network.ConnectivityStatus
import com.bvic.contacto.core.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityUseCase(
    private val monitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<ConnectivityStatus> = monitor.status
}
