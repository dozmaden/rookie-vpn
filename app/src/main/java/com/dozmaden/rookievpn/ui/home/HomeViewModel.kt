package com.dozmaden.rookievpn.ui.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dozmaden.rookievpn.dto.NetworkInfo
import com.dozmaden.rookievpn.model.Server
import com.dozmaden.rookievpn.repository.NetworkRepository
import com.dozmaden.rookievpn.state.VpnConnectionStatus
import com.dozmaden.rookievpn.utils.SharedPreference
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.VpnStatus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val preference: SharedPreference = SharedPreference(context)
    private val server: Server? = preference.server

    private val disposables = CompositeDisposable()
    private fun Disposable.onBind() = disposables.add(this)

    private val _networkInfo = MutableLiveData<NetworkInfo>()
    internal val networkInfo: LiveData<NetworkInfo> = _networkInfo

    private val _connectionStatus = MutableLiveData<VpnConnectionStatus>()
        .apply {
            postValue(VpnConnectionStatus.NOT_CONNECTED)
        }
    internal val connectionStatus: LiveData<VpnConnectionStatus> = _connectionStatus
    internal fun getConnectionStatus() = connectionStatus.value

    internal fun loadNetworkInfo() {
        NetworkRepository.getNetworkInfo().observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { info ->
                    _networkInfo.postValue(info)
//                    if (_networkInfo.value?.proxy == "true") {
//                        _connectionStatus.postValue(VpnConnectionStatus.CONNECTED)
//                    } else {
//                        _connectionStatus.postValue(VpnConnectionStatus.NOT_CONNECTED)
//                    }
                },
                onError = {
                    _networkInfo.postValue(
                        NetworkInfo()
                    )
//                    _connectionStatus.postValue(ConnectionStatus.CONNECTION_FAILED)
                }
            )
            .onBind()
    }

    internal fun checkVpnActivity() {
        if (VpnStatus.isVPNActive()) {
            _connectionStatus.postValue(VpnConnectionStatus.CONNECTED)
        } else {
            _connectionStatus.postValue(VpnConnectionStatus.NOT_CONNECTED)
        }
    }

    internal fun updateVpnConnectionStatus(status: String?) {
        if (status != null) when (status) {
            "NONETWORK" -> {
                _connectionStatus.postValue(VpnConnectionStatus.NOT_CONNECTED)
            }
            "WAIT", "AUTH", "RECONNECTING" -> {
                _connectionStatus.postValue(VpnConnectionStatus.CONNECTING)
            }
            "CONNECTED" -> {
                _connectionStatus.postValue(VpnConnectionStatus.CONNECTED)
            }
            "DISCONNECTED" -> {
                _connectionStatus.postValue(VpnConnectionStatus.DISCONNECTED)
            }
        }
    }

    internal fun startVpn() {
        val conf = server?.vpn?.let { context.assets.open(it) }

        val isr = InputStreamReader(conf)
        val br = BufferedReader(isr)

        var config = ""
        var line: String?

        while (true) {
            line = br.readLine()
            if (line == null) break
            config += """
                            $line
                            
                            """.trimIndent()
        }

        br.readLine()

        OpenVpnApi.startVpn(
            context,
            config,
            server?.country,
            server?.vpnUsername,
            server?.vpnPassword
        )
    }

    internal fun stopVPN() {
        OpenVPNThread.stop()
        _connectionStatus.postValue(VpnConnectionStatus.NOT_CONNECTED)
    }
}