package com.dozmaden.rookievpn.ui.servers

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dozmaden.rookievpn.model.VpnServer
import com.dozmaden.rookievpn.preferences.VpnPreferences
import com.dozmaden.rookievpn.utils.VpnUtilities.getServerList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ServersViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val vpnPreferences: VpnPreferences = VpnPreferences(context)

    private val servers: ArrayList<VpnServer> = getServerList()

    init {
        if(!vpnPreferences.isServerSelected()){
            vpnPreferences.addSelectedServer("rookie.ovpn")
        }
    }

    val selectedServersFlow: Flow<List<VpnServer>> =
        vpnPreferences.selectedServersFlow
            .map { selected ->
                servers
                    .filter { server ->
                        selected.contains(server.filename)
                    }
                    .sortedBy { it.filename }
            }
            .flowOn(Dispatchers.Default)

    val unselectedServersFlow: Flow<List<VpnServer>> =
        vpnPreferences.selectedServersFlow
            .map { selected ->
                servers
                    .filter { server ->
                        !selected.contains(server.filename)
                    }
                    .sortedBy { it.filename }
            }
            .flowOn(Dispatchers.Default)

    fun addToSelected(server: VpnServer) {
        viewModelScope.launch(Dispatchers.Default) {
            vpnPreferences.addSelectedServer(server.filename)
        }
    }

    fun removeFromSelected(server: VpnServer) {
        viewModelScope.launch(Dispatchers.Default) {
            vpnPreferences.removeSelectedServer(server.filename)
        }
    }
}