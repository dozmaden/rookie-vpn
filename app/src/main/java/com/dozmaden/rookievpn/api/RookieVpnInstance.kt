package com.dozmaden.rookievpn.api

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RookieVpnInstance {
    private val logInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val httpClient = OkHttpClient.Builder().addNetworkInterceptor(logInterceptor).build()

    private val retrofit =
        Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(SERVER_URL)
            .build()

    val SERVER_API: RookieVpnApi = retrofit.create(RookieVpnApi::class.java)
}