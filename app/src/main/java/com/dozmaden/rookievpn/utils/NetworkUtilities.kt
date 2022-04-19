package com.dozmaden.rookievpn.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.dozmaden.rookievpn.model.VpnServer
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNThread
import java.io.BufferedReader
import java.io.InputStreamReader

object NetworkUtilities {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isVpnConnectionActive(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val caps: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(activeNetwork)

        return caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
    }

    fun connectToVpn(context: Context, vpnServer: VpnServer) {
        val conf = vpnServer.vpn.let { context.assets.open(it) }
        val isr = InputStreamReader(conf)
        val br = BufferedReader(isr)

        var config = ""
        var line: String?

        while (true) {
            line = br.readLine()
            if (line == null) break
            config += """
                            $line
                            
                            """.trimIndent()
        }
        br.readLine()

        OpenVpnApi.startVpn(
            context,
            config,
            vpnServer.country,
            vpnServer.vpnUsername,
            vpnServer.vpnPassword
        )
    }

    fun disconnectFromVpn() {
        OpenVPNThread.stop()
    }
}