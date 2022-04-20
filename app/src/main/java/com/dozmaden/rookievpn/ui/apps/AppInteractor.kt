package com.dozmaden.rookievpn.ui.apps

import com.dozmaden.rookievpn.model.InstalledApp

class AppInteractor(
    private val appInfoHolder: AppInfoHolder
) {
    private fun mapSelectedApp(packageName: String): InstalledApp {
        return InstalledApp(
            packageName,
            appInfoHolder.getAppLabel(packageName),
            appInfoHolder.getAppIcon(packageName)
        )
    }

    fun getAppsList(): List<InstalledApp> {
        return appInfoHolder.getAppsList()
            .map(this::mapSelectedApp)
    }
}