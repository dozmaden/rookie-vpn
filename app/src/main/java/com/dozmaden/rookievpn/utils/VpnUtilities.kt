package com.dozmaden.rookievpn.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
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
                "Japan",
//                Utils.getImgURL(R.drawable.japan),
                "japan2.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            VpnServer(
                "United States",
//                Utils.getImgURL(R.drawable.usa_flag),
                "us.ovpn",
                "freeopenvpn",
                "416248023"
            )
        )
        servers.add(
            VpnServer(
                "Sweden",
//                Utils.getImgURL(R.drawable.sweden),
                "sweden.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            VpnServer(
                "Korea",
//                Utils.getImgURL(R.drawable.korea),
                "korea.ovpn",
                "vpn",
                "vpn"
            )
        )
        //            VpnServer(
//            country = preferences.getString(VPN_COUNTRY, "Ozmaden")!!,
//            vpn = preferences.getString(VPN_SERVER, "ozmaden.ovpn")!!,
//            vpnUsername = preferences.getString(VPN_USERNAME, "vpn")!!,
//            vpnPassword = preferences.getString(VPN_PASSWORD, "vpn")!!
//        )
        return servers
    }
}