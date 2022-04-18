package com.dozmaden.rookievpn.ui.applications

import com.dozmaden.rookievpn.model.ApplicationInfo
import com.dozmaden.rookievpn.utils.AppInfosHolder

class ApplicationInteractor(
    private val appInfosHolder: AppInfosHolder
) {
    fun mapSelectedApp(packageName: String): ApplicationInfo {
        return ApplicationInfo(
            packageName,
            appInfosHolder.getAppLabel(packageName),
            appInfosHolder.getAppIcon(packageName)
        )
    }

    fun getAppsList(): List<ApplicationInfo> {
        return appInfosHolder.getAppsList()
            .map(this::mapSelectedApp)
    }
}