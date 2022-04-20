package com.dozmaden.rookievpn.preferences

import android.content.Context
import android.content.SharedPreferences
import com.dozmaden.rookievpn.model.VpnServer
import com.dozmaden.rookievpn.utils.VpnUtilities.getServerList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VpnPreferences(context: Context) {

    companion object {
        private const val VPN_PREFS_NAME = "vpn_servers_preference"
        const val KEY_SERVERS_SELECTED = "key_servers_selected"
    }

    private var preferences: SharedPreferences =
        context.getSharedPreferences(VPN_PREFS_NAME, Context.MODE_PRIVATE)

    private val preferencesEditor: SharedPreferences.Editor = preferences.edit()

    private val _selectedServersFlow: MutableStateFlow<Set<String>> =
        MutableStateFlow(getSelectedServersSet())
    val selectedServersFlow: StateFlow<Set<String>> = _selectedServersFlow.asStateFlow()

    private fun getSelectedServersSet(): MutableSet<String> {
        val set = preferences.getStringSet(KEY_SERVERS_SELECTED, emptySet())
        return set?.toMutableSet() ?: mutableSetOf()
    }

    val vpnServer: VpnServer?
        get() =
            getServer()

    private fun getServer(): VpnServer? {
        if (getSelectedServersSet().size < 1) {
            return getServerList()[0]
        }
        val selectedVpnName = getSelectedServersSet().toList()[0]
        val servers = getServerList()
        for (server in servers) {
            if (server.filename == selectedVpnName) {
                return server
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
            .putStringSet(KEY_SERVERS_SELECTED, selected)
            .apply()
        _selectedServersFlow.value = selected
    }

    fun removeSelectedServer(vpnName: String) {
        val selected = getSelectedServersSet()
        if (selected.size == 1) {
            return
        }
        selected.remove(vpnName)
        preferencesEditor
            .putStringSet(AppPreferences.KEY_SELECTED, selected)
            .apply()
        _selectedServersFlow.value = selected
    }

    fun isServerSelected(): Boolean {
        return getSelectedServersSet().size > 0
    }

}