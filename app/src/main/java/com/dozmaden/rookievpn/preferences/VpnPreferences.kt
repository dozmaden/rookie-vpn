package com.dozmaden.rookievpn.preferences

import android.content.Context
import android.content.SharedPreferences
import com.dozmaden.rookievpn.model.VpnServer

class VpnPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)

    private val preferencesEditor: SharedPreferences.Editor = preferences.edit()

    companion object {
        private const val APP_PREFS_NAME = "VpnPreference"
        private const val SERVER_VPN = "server_ovpn"
        private const val SERVER_VPN_USERNAME = "server_ovpn_user"
        private const val SERVER_VPN_PASSWORD = "server_ovpn_password"
        private const val SERVER_COUNTRY = "server_country"
    }

    /**
     * Save server details
     * @param vpnServer details of ovpn server
     */
    fun saveServer(vpnServer: VpnServer) {
        preferencesEditor.putString(SERVER_COUNTRY, vpnServer.country)
        preferencesEditor.putString(SERVER_VPN, vpnServer.vpn)
        preferencesEditor.putString(SERVER_VPN_USERNAME, vpnServer.vpnUsername)
        preferencesEditor.putString(SERVER_VPN_PASSWORD, vpnServer.vpnPassword)
        preferencesEditor.commit()
    }

    val vpnServer: VpnServer?
        get() = VpnServer(
            country = preferences.getString(SERVER_COUNTRY, "Japan")!!,
            vpn = preferences.getString(SERVER_VPN, "japan2.ovpn")!!,
            vpnUsername = preferences.getString(SERVER_VPN_USERNAME, "vpn")!!,
            vpnPassword = preferences.getString(SERVER_VPN_PASSWORD, "vpn")!!
        )
}