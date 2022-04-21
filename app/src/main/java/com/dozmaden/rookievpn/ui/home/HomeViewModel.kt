package com.dozmaden.rookievpn.ui.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dozmaden.rookievpn.dto.NetworkInfo
import com.dozmaden.rookievpn.preferences.VpnPreferences
import com.dozmaden.rookievpn.repository.NetworkInfoRepository
import com.dozmaden.rookievpn.state.VpnConnectionStatus
import com.dozmaden.rookievpn.utils.VpnUtilities.connectToVpn
import com.dozmaden.rookievpn.utils.VpnUtilities.disconnectFromVpn
import com.dozmaden.rookievpn.utils.VpnUtilities.isVpnConnectionActive
import de.blinkt.openvpn.core.VpnStatus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val preference: VpnPreferences = VpnPreferences(context)

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
        NetworkInfoRepository.getNetworkInfo().observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { info ->
                    _networkInfo.postValue(info)
//                    if (_networkInfo.value?.proxy == "true" && VpnStatus.isVPNActive()) {
//                        _connectionStatus.postValue(VpnConnectionStatus.CONNECTED)
//                    }
//                    else {
//                        _connectionStatus.postValue(VpnConnectionStatus.NOT_CONNECTED)
//                    }
                },
                onError = {
//                    _networkInfo.postValue(
//                        NetworkInfo()
//                    )
                }
            )
            .onBind()
    }

    internal fun checkVpnActivity() {
        if (VpnStatus.isVPNActive()) {
            if (isVpnConnectionActive(context)) {
                _connectionStatus.postValue(VpnConnectionStatus.CONNECTED)
            }
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
                preference.vpnServer?.let {
                    _networkInfo.postValue(NetworkInfo(country = it.country, isp = it.filename))
                }
            }
            "DISCONNECTED" -> {
                _connectionStatus.postValue(VpnConnectionStatus.DISCONNECTED)
            }
        }
    }

    internal fun startVpn() {
        preference.vpnServer?.let {
            connectToVpn(context, it)
        }
    }

    internal fun stopVPN() {
        disconnectFromVpn()
        _connectionStatus.postValue(VpnConnectionStatus.NOT_CONNECTED)
    }

    internal fun getVpnCountry(): String? {
        return preference.vpnServer?.country
    }
}