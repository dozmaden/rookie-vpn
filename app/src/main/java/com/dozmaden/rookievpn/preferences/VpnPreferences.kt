package com.dozmaden.rookievpn.preferences

import android.content.Context
import android.content.SharedPreferences
import com.dozmaden.rookievpn.model.VpnServer
import com.dozmaden.rookievpn.utils.VpnUtilities.getServerList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VpnPreferences(private val context: Context) {

    companion object {
        private const val VPN_PREFS_NAME = "VpnPreference"
        const val KEY_SELECTED = "key_servers_selected"
    }

    private var preferences: SharedPreferences =
        context.getSharedPreferences(VPN_PREFS_NAME, Context.MODE_PRIVATE)

    private val preferencesEditor: SharedPreferences.Editor = preferences.edit()

    private val _selectedServersFlow: MutableStateFlow<Set<String>> =
        MutableStateFlow(getSelectedServersSet())
    val selectedServersFlow: StateFlow<Set<String>> = _selectedServersFlow.asStateFlow()

    private fun getSelectedServersSet(): MutableSet<String> {
        val set = preferences.getStringSet(KEY_SELECTED, emptySet())
        return set?.toMutableSet() ?: mutableSetOf()
    }

    val vpnServer: VpnServer?
        get() =
//            VpnServer(
//            country = preferences.getString(VPN_COUNTRY, "Ozmaden")!!,
//            vpn = preferences.getString(VPN_SERVER, "ozmaden.ovpn")!!,
//            vpnUsername = preferences.getString(VPN_USERNAME, "vpn")!!,
//            vpnPassword = preferences.getString(VPN_PASSWORD, "vpn")!!
//        )
            getFirstServer()

    private fun getFirstServer(): VpnServer? {
        var name = ""
        val selected = getSelectedServersSet()
        for (i in selected) {
            name = i
        }

        val servers = getServerList()
        for (i in servers) {
            if (i.vpn == name) {
                return i
            }
        }

        return null
    }

    /**
     * Save server details
     * @param vpnServer details of ovpn server
     */
//    fun saveServer(vpnServer: VpnServer) {
//        preferencesEditor.putString(VPN_COUNTRY, vpnServer.country)
//        preferencesEditor.putString(VPN_SERVER, vpnServer.vpn)
//        preferencesEditor.putString(VPN_USERNAME, vpnServer.vpnUsername)
//        preferencesEditor.putString(VPN_PASSWORD, vpnServer.vpnPassword)
//        preferencesEditor.commit()
//    }

    fun isSelectedServer(vpnName: String): Boolean {
        return getSelectedServersSet().find {
            it == vpnName
        } != null
    }

    fun addSelectedServer(vpnName: String) {
        val selected = getSelectedServersSet()

        for (i in selected) {
            selected.remove(i)
        }
        selected.clear()

        selected.add(vpnName)
        preferencesEditor
            .putStringSet(KEY_SELECTED, selected)
            .apply()
        _selectedServersFlow.value = selected
    }

    fun removeSelectedServer(vpnName: String) {
        val selected = getSelectedServersSet()
        selected.remove(vpnName)
        preferencesEditor
            .putStringSet(AppsPreferences.KEY_SELECTED, selected)
            .apply()
        _selectedServersFlow.value = selected
    }
}