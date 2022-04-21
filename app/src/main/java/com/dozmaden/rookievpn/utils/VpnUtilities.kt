package com.dozmaden.rookievpn.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.model.VpnServer
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNThread
import java.io.BufferedReader
import java.io.InputStreamReader

object VpnUtilities {
    fun isVpnConnectionActive(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val caps: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(activeNetwork)
        return caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
    }

    fun connectToVpn(context: Context, vpnServer: VpnServer) {
        val conf = vpnServer.filename.let { context.assets.open(it) }
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
            vpnServer.username,
            vpnServer.password
        )
    }

    fun disconnectFromVpn() {
        OpenVPNThread.stop()
    }

    fun getServerList(): ArrayList<VpnServer> {
        val servers: ArrayList<VpnServer> = ArrayList()
        servers.add(
            VpnServer(
                "United States",
                "rookie.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.unitedstates)
            ),
        )
        servers.add(
            VpnServer(
                "United Kingdom",
                "uk.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.unitedkingdom)
            )
        )
        servers.add(
            VpnServer(
                "Japan",
//                Utils.getImgURL(R.drawable.japan),
                "japan.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.japan)
            )
        )
        servers.add(
            VpnServer(
                "South Korea",
                "southkorea.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.southkorea)
            )
        )
        servers.add(
            VpnServer(
                "Taiwan",
//                Utils.getImgURL(R.drawable.japan),
                "taiwan.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.taiwan)
            )
        )
        servers.add(
            VpnServer(
                "India",
                "india.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.india)
            )
        )
        servers.add(
            VpnServer(
                "Germany",
                "germany.ovpn",
                "vpn",
                "vpn",
                getImgURL(R.drawable.germany)
            )
        )
        return servers
    }

    fun getImgURL(resourceId: Int): String? {
        // Use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if not same
        return Uri.parse(
            "android.resource://" + (R::class.java.getPackage()?.name)
                    + "/" + resourceId
        ).toString()
    }
}