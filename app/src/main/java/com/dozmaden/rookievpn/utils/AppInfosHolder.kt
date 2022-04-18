package com.dozmaden.rookievpn.utils

import android.content.Context
import android.graphics.drawable.Drawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppInfosHolder(
    private val context: Context
) {
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val appsList: MutableList<String> = mutableListOf()
    private val appIconByPackageName: MutableMap<String, Drawable?> = mutableMapOf()
    private val appLabelByPackageName: MutableMap<String, String> = mutableMapOf()

    fun init() {
        scope.launch {
            getAppsList()
                .forEach {
                    appIconByPackageName[it] = appIcon(it)
                    appLabelByPackageName[it] = appLabel(context, it)
                }
        }
    }

    fun getAppLabel(packageName: String): String {
        return appLabelByPackageName[packageName] ?: appLabel(context, packageName).also {
            appLabelByPackageName[packageName] = it
        }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return appIconByPackageName[packageName] ?: appIcon(packageName).also {
            appIconByPackageName[packageName] = it
        }
    }

    fun getAppsList(): List<String> {
        if (appsList.isNotEmpty()) {
            return appsList
        }
        return context.getAppsList()
            .map { it.activityInfo.packageName }
            .also {
                appsList.clear()
                appsList.addAll(it)
            }
    }
}