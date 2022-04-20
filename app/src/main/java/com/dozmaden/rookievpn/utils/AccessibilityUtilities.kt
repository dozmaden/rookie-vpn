package com.dozmaden.rookievpn.utils

import android.content.Context
import android.provider.Settings
import android.util.Log

object AccessibilityUtilities {
    fun Context.isAccessibilitySettingsOn(): Boolean {
        try {
            val accessibilityEnabled = Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            if (accessibilityEnabled == 1) {
                val services = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (services != null) {
                    return services.contains(packageName, ignoreCase = true)
                }
            }
        } catch (e: Throwable) {
            Log.d("AccessibilityUtilities", "Error!")
        }
        return false
    }
}