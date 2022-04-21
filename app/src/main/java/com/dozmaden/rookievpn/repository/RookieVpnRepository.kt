package com.dozmaden.rookievpn.repository

import com.dozmaden.rookievpn.api.RookieVpnInstance
import com.dozmaden.rookievpn.dto.LoginInfo
import com.dozmaden.rookievpn.dto.LoginResponse
import com.dozmaden.rookievpn.dto.SignUpInfo
import com.dozmaden.rookievpn.dto.SignUpResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object RookieVpnRepository {
    internal fun login(loginInfo: LoginInfo): Single<LoginResponse> =
        RookieVpnInstance.SERVER_API.logIn(loginInfo)
            .subscribeOn(Schedulers.io())

    internal fun signup(signUpInfo: SignUpInfo): Single<SignUpResponse> =
        RookieVpnInstance.SERVER_API.signUp(signUpInfo)
            .subscribeOn(Schedulers.io())
}