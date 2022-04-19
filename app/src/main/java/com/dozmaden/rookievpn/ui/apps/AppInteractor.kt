package com.dozmaden.rookievpn.ui.apps

import com.dozmaden.rookievpn.model.App

class AppInteractor(
    private val appInfoHolder: AppInfoHolder
) {
    fun mapSelectedApp(packageName: String): App {
        return App(
            packageName,
            appInfoHolder.getAppLabel(packageName),
            appInfoHolder.getAppIcon(packageName)
        )
    }

    fun getAppsList(): List<App> {
        return appInfoHolder.getAppsList()
            .map(this::mapSelectedApp)
    }
}