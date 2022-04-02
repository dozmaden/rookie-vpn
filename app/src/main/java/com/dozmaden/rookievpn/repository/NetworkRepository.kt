package com.dozmaden.rookievpn.repository

import com.dozmaden.rookievpn.api.NetworkInstance
import com.dozmaden.rookievpn.dto.NetworkInfo
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object NetworkRepository {
    internal fun getNetworkInfo(): Single<NetworkInfo> =
        NetworkInstance.API.getConnectionInfo().subscribeOn(Schedulers.io())
}