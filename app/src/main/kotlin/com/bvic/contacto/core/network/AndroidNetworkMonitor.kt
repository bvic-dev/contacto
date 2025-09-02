package com.bvic.contacto.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidNetworkMonitor(
    context: Context,
) : NetworkMonitor {
    private val cm: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val status =
        callbackFlow {
            trySend(currentStatus())
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        trySend(resolvedStatus(network))
                    }

                    override fun onLost(network: Network) {
                        trySend(ConnectivityStatus.Lost)
                        trySend(currentStatus())
                    }

                    override fun onUnavailable() {
                        trySend(ConnectivityStatus.Unavailable)
                    }

                    override fun onLosing(
                        network: Network,
                        maxMsToLive: Int,
                    ) {
                        trySend(ConnectivityStatus.Losing)
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        caps: NetworkCapabilities,
                    ) {
                        val validated = isValidated(caps)
                        trySend(if (validated) ConnectivityStatus.Available else ConnectivityStatus.Unavailable)
                    }
                }

            cm.registerDefaultNetworkCallback(callback)

            awaitClose { runCatching { cm.unregisterNetworkCallback(callback) } }
        }.distinctUntilChanged()

    private fun currentStatus(): ConnectivityStatus {
        val network = cm.activeNetwork ?: return ConnectivityStatus.Unavailable
        val caps = cm.getNetworkCapabilities(network) ?: return ConnectivityStatus.Unavailable
        return if (isValidated(caps)) ConnectivityStatus.Available else ConnectivityStatus.Unavailable
    }

    private fun resolvedStatus(network: Network): ConnectivityStatus {
        val caps = cm.getNetworkCapabilities(network) ?: return ConnectivityStatus.Unavailable
        return if (isValidated(caps)) ConnectivityStatus.Available else ConnectivityStatus.Unavailable
    }

    private fun isValidated(caps: NetworkCapabilities): Boolean {
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val validated =
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        return hasInternet && validated
    }
}
