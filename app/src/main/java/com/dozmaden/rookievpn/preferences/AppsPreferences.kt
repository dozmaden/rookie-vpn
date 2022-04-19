package com.dozmaden.rookievpn.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppsPreferences(context: Context) {

    companion object {
        const val SELECTED_PREFS_NAME = "selected_prefs"
        const val KEY_SELECTED = "key_apps_selected"
        const val KEY_AUTO_MODE = "key_auto_mode"
    }

    private val preferences =
        context.getSharedPreferences(SELECTED_PREFS_NAME, Context.MODE_PRIVATE)

    private val preferencesEditor: SharedPreferences.Editor = preferences.edit()

    private val _autoModeStatusFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(getAutoStatus())
    val autoModeStatusFlow: StateFlow<Boolean> = _autoModeStatusFlow.asStateFlow()

    private val _selectedAppsFlow: MutableStateFlow<Set<String>> =
        MutableStateFlow(getSelectedSet())
    val selectedAppsFlow: StateFlow<Set<String>> = _selectedAppsFlow.asStateFlow()

    fun isSelectedApp(packageName: String): Boolean {
        return getSelectedSet().find {
            it == packageName
        } != null
    }

    fun getAutoStatus(): Boolean {
        return preferences.getBoolean(KEY_AUTO_MODE, false)
    }

    fun setAutoVpnStatus(on: Boolean) {
        preferencesEditor
            .putBoolean(KEY_AUTO_MODE, on)
            .apply()
        _autoModeStatusFlow.value = on
    }

    private fun getSelectedSet(): MutableSet<String> {
        val set = preferences.getStringSet(KEY_SELECTED, emptySet())
        return set?.toMutableSet() ?: mutableSetOf()
    }

    fun addSelectedApp(packageName: String) {
        val selected = getSelectedSet()
        selected.add(packageName)
        preferencesEditor
            .putStringSet(KEY_SELECTED, selected)
            .apply()
        _selectedAppsFlow.value = selected
    }

    fun removeSelectedApp(packageName: String) {
        val selected = getSelectedSet()
        selected.remove(packageName)
        preferencesEditor
            .putStringSet(KEY_SELECTED, selected)
            .apply()
        _selectedAppsFlow.value = selected
    }
}