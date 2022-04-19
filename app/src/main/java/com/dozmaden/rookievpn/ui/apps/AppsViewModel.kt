package com.dozmaden.rookievpn.ui.apps

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dozmaden.rookievpn.model.App
import com.dozmaden.rookievpn.preferences.AppsPreferences
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

    private val appsPreferences: AppsPreferences = AppsPreferences(context)
    val autoModeStatusFlow = appsPreferences.autoModeStatusFlow

    val selectedAppsFlow: Flow<List<App>> =
        appsPreferences.selectedAppsFlow
            .map {
                it.map(appInteractor::mapSelectedApp)
                    .sortedBy { app -> app.appLabel }
            }
            .flowOn(Dispatchers.Default)

    val unselectedAppsFlow: Flow<List<App>> =
        appsPreferences.selectedAppsFlow
            .map { selected ->
                appsList
                    .filter { app ->
                        !selected.contains(app.packageName)
                    }
                    .sortedBy { it.appLabel }
            }
            .flowOn(Dispatchers.Default)

    fun setAutoModeStatus(focusModeOn: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            appsPreferences.setAutoVpnStatus(focusModeOn)
        }
    }

    fun getAutoModeStatus(): Boolean {
        return appsPreferences.getAutoStatus()
    }

    fun addToSelected(app: App) {
        viewModelScope.launch(Dispatchers.Default) {
            appsPreferences.addSelectedApp(app.packageName)
        }
    }

    fun removeFromSelected(app: App) {
        viewModelScope.launch(Dispatchers.Default) {
            appsPreferences.removeSelectedApp(app.packageName)
        }
    }
}