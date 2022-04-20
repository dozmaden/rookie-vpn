package com.dozmaden.rookievpn.ui.apps

import android.content.Context
import android.graphics.drawable.Drawable
import com.dozmaden.rookievpn.utils.AppUtilities
import com.dozmaden.rookievpn.utils.AppUtilities.getAppLabel
import com.dozmaden.rookievpn.utils.AppUtilities.getAppList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppInfoHolder(
    private val context: Context
) {
    private val appsList: MutableList<String> = mutableListOf()
    private val appIconsByPackageName: MutableMap<String, Drawable?> = mutableMapOf()
    private val appLabelByPackageName: MutableMap<String, String> = mutableMapOf()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            getAppsList()
                .forEach {
                    appIconsByPackageName[it] = AppUtilities.getAppIcon(it)
                    appLabelByPackageName[it] =
                        getAppLabel(context, it)
                }
        }
    }

    fun getAppsList(): List<String> {
        if (appsList.isNotEmpty()) {
            return appsList
        }
        return context.getAppList()
            .map { it.activityInfo.packageName }
            .also {
                appsList.clear()
                appsList.addAll(it)
            }
    }

    fun getAppLabel(packageName: String): String {
        return appLabelByPackageName[packageName] ?: getAppLabel(context, packageName)
            .also {
                appLabelByPackageName[packageName] = it
            }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return appIconsByPackageName[packageName] ?: AppUtilities.getAppIcon(packageName)
            .also {
                appIconsByPackageName[packageName] = it
            }
    }
}