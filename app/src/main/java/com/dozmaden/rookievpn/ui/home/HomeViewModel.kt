package com.dozmaden.rookievpn.ui.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dozmaden.rookievpn.dto.NetworkInfo
import com.dozmaden.rookievpn.model.Server
import com.dozmaden.rookievpn.repository.NetworkRepository
import com.dozmaden.rookievpn.utils.ConnectionStatus
import com.dozmaden.rookievpn.utils.SharedPreference
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNThread
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

    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
//        .apply {
//        postValue(ConnectionStatus.NOT_CONNECTED)
//    }
    internal val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus

    internal fun loadNetworkInfo() {
        NetworkRepository.getNetworkInfo().observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { info ->
                    _networkInfo.postValue(info)
                    _connectionStatus.postValue(ConnectionStatus.CONNECTED_TO_NETWORK)
                    if (_networkInfo.value?.proxy == "true") {
                        _connectionStatus.postValue(ConnectionStatus.CONNECTED_TO_VPN)
                    }
                },
                onError = {
//                    _connectionStatus.postValue(ConnectionStatus.CONNECTION_FAILED)
                }
            )
            .onBind()
    }

    internal fun updateVpnStatus(status: String?) {
        if (status != null) when (status) {
            "NONETWORK" -> {
                _connectionStatus.postValue(ConnectionStatus.NOT_CONNECTED)
            }
            "WAIT", "AUTH", "RECONNECTING" -> {
                _connectionStatus.postValue(ConnectionStatus.CONNECTING_TO_VPN)
            }
            "CONNECTED" -> {
                _connectionStatus.postValue(ConnectionStatus.CONNECTED_TO_VPN)
            }
            "DISCONNECTED" -> {
//                _connectionStatus.postValue(ConnectionStatus.DISCONNECTED_FROM_VPN)
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

//        _connectionStatus.postValue(ConnectionStatus.CONNECTING_TO_VPN)
//
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
        _connectionStatus.postValue(ConnectionStatus.DISCONNECTED_FROM_VPN)
    }
}