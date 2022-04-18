package com.dozmaden.rookievpn.preferences

import android.content.Context
import android.content.SharedPreferences
import com.dozmaden.rookievpn.model.Server
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreference(context: Context) {

    private val preferences: SharedPreferences
    private val preferencesEditor: SharedPreferences.Editor
    private val context: Context

    private val selectedPrefs =
        context.getSharedPreferences(SELECTED_PREFS_NAME, Context.MODE_PRIVATE)

    private val _focusModeStatusFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(getFocusModeStatus())
    val focusModeStatusFlow: StateFlow<Boolean> = _focusModeStatusFlow.asStateFlow()

    private val _selectedAppsFlow: MutableStateFlow<Set<String>> =
        MutableStateFlow(getSelectedSet())
    val selectedAppsFlow: StateFlow<Set<String>> = _selectedAppsFlow.asStateFlow()

    /**
     * Save server details
     * @param server details of ovpn server
     */
    fun saveServer(server: Server) {
        preferencesEditor.putString(SERVER_COUNTRY, server.country)
        preferencesEditor.putString(SERVER_VPN, server.vpn)
        preferencesEditor.putString(SERVER_VPN_USERNAME, server.vpnUsername)
        preferencesEditor.putString(SERVER_VPN_PASSWORD, server.vpnPassword)
        preferencesEditor.commit()
    }

    val server: Server?
        get() = Server(
            country = preferences.getString(SERVER_COUNTRY, "Japan")!!,
            vpn = preferences.getString(SERVER_VPN, "japan2.ovpn")!!,
            vpnUsername = preferences.getString(SERVER_VPN_USERNAME, "vpn")!!,
            vpnPassword = preferences.getString(SERVER_VPN_PASSWORD, "vpn")!!
        )

    companion object {
        private const val APP_PREFS_NAME = "RookieVpnPreference"
        private const val SERVER_VPN = "server_ovpn"
        private const val SERVER_VPN_USERNAME = "server_ovpn_user"
        private const val SERVER_VPN_PASSWORD = "server_ovpn_password"
        private const val SERVER_COUNTRY = "server_country"

        const val SELECTED_PREFS_NAME = "blacklist_prefs"
        const val KEY_SELECTED = "key_apps_blacklist"
        const val KEY_FOCUS_MODE = "key_focus_mode"
    }

    init {
        preferences = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)
        preferencesEditor = preferences.edit()
        this.context = context
    }

    fun getFocusModeStatus(): Boolean {
        return selectedPrefs.getBoolean(KEY_FOCUS_MODE, false)
    }

    fun setFocusModeStatus(on: Boolean) {
        selectedPrefs.edit()
            .putBoolean(KEY_FOCUS_MODE, on)
            .apply()
        _focusModeStatusFlow.value = on
    }

    private fun getSelectedSet(): MutableSet<String> {
        val set = selectedPrefs.getStringSet(KEY_SELECTED, emptySet())
        return set?.toMutableSet() ?: mutableSetOf()
    }

    fun addSelectedApp(packageName: String) {
        val selected = getSelectedSet()
        selected.add(packageName)
        selectedPrefs.edit()
            .putStringSet(KEY_SELECTED, selected)
            .apply()
        _selectedAppsFlow.value = selected
    }

    fun removeSelectedApp(packageName: String) {
        val selected = getSelectedSet()
        selected.remove(packageName)
        selectedPrefs.edit()
            .putStringSet(KEY_SELECTED, selected)
            .apply()
        _selectedAppsFlow.value = selected
    }
}
