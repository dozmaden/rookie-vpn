package com.dozmaden.rookievpn.api

import com.dozmaden.rookievpn.dto.LoginInfo
import com.dozmaden.rookievpn.dto.LoginResponse
import com.dozmaden.rookievpn.dto.SignUpInfo
import com.dozmaden.rookievpn.dto.SignUpResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

internal const val SERVER_URL = "http://164.92.183.41:5000/"

interface RookieVpnApi {
    @POST("/client/users")
    fun signUp(@Body signup: SignUpInfo): Single<SignUpResponse>

    @POST("/token/")
    fun logIn(@Body login: LoginInfo): Single<LoginResponse>
}