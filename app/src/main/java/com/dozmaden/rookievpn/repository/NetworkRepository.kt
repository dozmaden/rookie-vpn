package com.dozmaden.rookievpn.repository

import com.dozmaden.rookievpn.api.NetworkInstance
import com.dozmaden.rookievpn.dto.NetworkInfo
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object NetworkRepository {
    internal fun getNetworkInfo(): Single<NetworkInfo> =
        NetworkInstance.API.getConnectionInfo()
            .retry(4L)
            .delay(300L, TimeUnit.MILLISECONDS, true)
            .subscribeOn(Schedulers.io())
}