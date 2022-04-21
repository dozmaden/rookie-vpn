package com.dozmaden.rookievpn.preferences

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    companion object {
        private const val AUTH_PREFS_NAME = "auth_preference"
    }

    private var preferences: SharedPreferences =
        context.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)

    private val preferencesEditor: SharedPreferences.Editor = preferences.edit()

    fun saveLogin(login: String) {
        preferencesEditor
            .putBoolean(AUTH_PREFS_NAME, true)
            .apply()
    }

    fun isAuthorized(): Boolean {
        return preferences.getBoolean(AUTH_PREFS_NAME, false)
    }
}