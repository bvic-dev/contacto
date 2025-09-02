package com.bvic.lydiacontacts.domain.usecase

import com.bvic.lydiacontacts.core.network.ConnectivityStatus
import com.bvic.lydiacontacts.core.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityUseCase(
    private val monitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<ConnectivityStatus> = monitor.status
}
