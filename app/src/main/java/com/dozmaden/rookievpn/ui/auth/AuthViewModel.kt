package com.dozmaden.rookievpn.ui.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.dto.LoginInfo
import com.dozmaden.rookievpn.dto.LoginResponse
import com.dozmaden.rookievpn.repository.RookieVpnRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy

class AuthViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginInfo>()
    val loginFormState: LiveData<LoginInfo> = _loginForm

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job

        val loginInfo = LoginInfo(username, password)
        RookieVpnRepository.login(loginInfo).observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { info ->
                    Log.d("SUCCESS", "logined")
                    _loginResult.postValue(info)
                },
                onError = {
                    _loginResult.postValue(LoginResponse("", ""))
                    Log.d("ERROR", "erroronlogin")
                }
            )
//        val result = loginRepository.login(username, password)
//
//        if (result is Result.Success) {
//            _loginResult.value =
//                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
//        } else {
//            _loginResult.value = LoginResult(error = R.string.login_failed)
//        }
    }

    fun loginDataChanged(username: String, password: String) {
//        if (!isUserNameValid(username)) {
//            _loginForm.value = LoginInfo(usernameError = R.string.invalid_username)
//        } else if (!isPasswordValid(password)) {
//            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
//        } else {
//            _loginForm.value = LoginFormState(isDataValid = true)
//        }
        _loginForm.value = LoginInfo(username, password)
    }

    // A placeholder username validation check
    internal fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    internal fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}