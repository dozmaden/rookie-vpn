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

    private val applicationInteractor = AppInteractor(AppInfoHolder(context))

    private val preferenceStorage: AppsPreferences = AppsPreferences(context)

    private val appsList = applicationInteractor.getAppsList()

    val focusModeStatusFlow = preferenceStorage.autoModeStatusFlow

    val selectedAppsFlow: Flow<List<App>> =
        preferenceStorage.selectedAppsFlow
            .map {
                it.map(applicationInteractor::mapSelectedApp)
                    .sortedBy { app -> app.appLabel }
            }
            .flowOn(Dispatchers.Default)

    val unselectedAppsFlow: Flow<List<App>> =
        preferenceStorage.selectedAppsFlow
            .map { selected ->
                appsList
                    .filter { app ->
                        !selected.contains(app.packageName)
                    }
                    .sortedBy { it.appLabel }
            }
            .flowOn(Dispatchers.Default)

    fun setFocusModeStatus(focusModeOn: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceStorage.setAutoVpnStatus(focusModeOn)
        }
    }

    fun getFocusModeStatus(): Boolean {
        return preferenceStorage.getAutoStatus()
    }

    fun addToSelected(app: App) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceStorage.addSelectedApp(app.packageName)
        }
    }

    fun removeFromSelected(app: App) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceStorage.removeSelectedApp(app.packageName)
        }
    }
}