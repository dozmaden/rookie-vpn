package com.dozmaden.rookievpn.api

import com.dozmaden.rookievpn.dto.NetworkInfo
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.POST

internal const val BASE_URL = "https://api.techniknews.net/ipgeo/"

interface NetworkApi {
    @GET(BASE_URL)
    fun getConnectionInfo(): Single<NetworkInfo>
//
//    @POST()
//    fun sendLoginInfo() : Single<LoginInfo>
}