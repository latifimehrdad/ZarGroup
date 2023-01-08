package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.repository.LoginRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private var repository: LoginRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    var loginLiveDate = SingleLiveEvent<String?>()
    var userName: String? = null
    var password: String? = null


    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            if (userName.isNullOrEmpty() || password.isNullOrEmpty())
                setMessage(resourcesProvider.getString(R.string.dataSendingIsEmpty))
            else {
                val response = repository.requestLogin(LoginRequestModel(userName!!, password!!))
                if (response?.isSuccessful == true) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        if (!it.hasError)
                            saveUserNameAndPassword(it.data)
                        setMessage(it.message)
                    } ?: run {
                        setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                    }
                } else
                    setMessage(response)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestLogin


    //---------------------------------------------------------------------------------------------- isBiometricEnable
    fun isBiometricEnable() = sharedPreferences.getBoolean(CompanionValues.biometric, false)
    //---------------------------------------------------------------------------------------------- isBiometricEnable


    //---------------------------------------------------------------------------------------------- setUserNamePasswordFromSharePreferences
    fun setUserNamePasswordFromSharePreferences() {
        userName = sharedPreferences.getString(CompanionValues.userName, null)
        password = sharedPreferences.getString(CompanionValues.passcode, null)
    }
    //---------------------------------------------------------------------------------------------- setUserNamePasswordFromSharePreferences


    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword
    private suspend fun saveUserNameAndPassword(token: String?) {
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, token)
            .putString(CompanionValues.userName, userName)
            .putString(CompanionValues.passcode, password)
            .apply()
        withContext(Main){ loginLiveDate.value = token }
    }
    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword


}