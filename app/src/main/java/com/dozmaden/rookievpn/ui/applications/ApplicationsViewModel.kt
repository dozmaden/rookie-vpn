package com.dozmaden.rookievpn.ui.applications

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dozmaden.rookievpn.model.ApplicationInfo
import com.dozmaden.rookievpn.preferences.SharedPreference
import com.dozmaden.rookievpn.utils.AppInfosHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ApplicationsViewModel(
    application: Application,
//    private val applicationInteractor: ApplicationInteractor
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val applicationInfoHolder = AppInfosHolder(context)
    private val applicationInteractor = ApplicationInteractor(applicationInfoHolder)

    private val preferenceStorage: SharedPreference = SharedPreference(context)

    private val appsList = applicationInteractor.getAppsList()

    val focusModeStatusFlow = preferenceStorage.focusModeStatusFlow

    val selectedAppsFlow: Flow<List<ApplicationInfo>> =
        preferenceStorage.selectedAppsFlow
            .map {
                it.map(applicationInteractor::mapSelectedApp)
                    .sortedBy { app -> app.appLabel }
            }
            .flowOn(Dispatchers.Default)

    val unselectedAppsFlow: Flow<List<ApplicationInfo>> =
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
            preferenceStorage.setFocusModeStatus(focusModeOn)
        }
    }

    fun getFocusModeStatus(): Boolean {
        return preferenceStorage.getFocusModeStatus()
    }

    fun addToSelected(app: ApplicationInfo) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceStorage.addSelectedApp(app.packageName)
        }
    }

    fun removeFromSelected(app: ApplicationInfo) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceStorage.removeSelectedApp(app.packageName)
        }
    }
}