package com.dozmaden.rookievpn.ui.apps

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dozmaden.rookievpn.model.InstalledApp
import com.dozmaden.rookievpn.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppsViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val appInfoHolder = AppInfoHolder(context)

    private val appInteractor = AppInteractor(appInfoHolder)
    private val appsList = appInteractor.getAppsList()

    private val appPreferences: AppPreferences = AppPreferences(context)
    val autoModeStatusFlow = appPreferences.autoModeStatusFlow

    val selectedAppsFlow: Flow<List<InstalledApp>> =
        appPreferences.selectedAppsFlow
            .map { selected ->
                appsList
                    .filter { app ->
                        selected.contains(app.packageName)
                    }
                    .sortedBy { it.appName }
            }
            .flowOn(Dispatchers.Default)

    val unselectedAppsFlow: Flow<List<InstalledApp>> =
        appPreferences.selectedAppsFlow
            .map { selected ->
                appsList
                    .filter { app ->
                        !selected.contains(app.packageName)
                    }
                    .sortedBy { it.appName }
            }
            .flowOn(Dispatchers.Default)

    fun setAutoModeStatus(focusModeOn: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            appPreferences.setAutoVpnStatus(focusModeOn)
        }
    }

    fun getAutoModeStatus(): Boolean {
        return appPreferences.getAutoStatus()
    }

    fun addToSelected(app: InstalledApp) {
        viewModelScope.launch(Dispatchers.Default) {
            appPreferences.addSelectedApp(app.packageName)
        }
    }

    fun removeFromSelected(app: InstalledApp) {
        viewModelScope.launch(Dispatchers.Default) {
            appPreferences.removeSelectedApp(app.packageName)
        }
    }
}