package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.model.response.LoginResponseModel
import com.zarholding.zar.repository.LoginRepository
import com.zarholding.zar.utility.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    var repository: LoginRepository
    ) : ViewModel() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    val loadingLiveDate = MutableLiveData(false)
    val useNameEmptyLiveData = MutableLiveData(false)
    val passwordEmptyLiveData = MutableLiveData(false)
    var responseOfLoginRequestLiveDate : LiveData<LoginResponseModel?>? = null
    var userName: String? = null
    var password: String? = null


    //---------------------------------------------------------------------------------------------- login
    fun login(fromFingerPrint : Boolean) {
        if (fromFingerPrint)
            setUserNamePasswordFromSharePreferences()
        if (userName.isNullOrEmpty()) {
            useNameEmptyLiveData.value = true
            return
        }
        useNameEmptyLiveData.value = false
        if (password.isNullOrEmpty()) {
            passwordEmptyLiveData.value = true
            return
        }
        passwordEmptyLiveData.value = false
        loadingLiveDate.value = true
        requestLogin()
    }
    //---------------------------------------------------------------------------------------------- login


    //---------------------------------------------------------------------------------------------- requestLogin
    private fun requestLogin() {
        responseOfLoginRequestLiveDate = repository.requestLogin(LoginRequestModel(userName!!, password!!))
    }
    //---------------------------------------------------------------------------------------------- requestLogin


    //---------------------------------------------------------------------------------------------- getBiometricEnable
    fun getBiometricEnable() = sharedPreferences.getBoolean(CompanionValues.biometric, false)
    //---------------------------------------------------------------------------------------------- getBiometricEnable


    //---------------------------------------------------------------------------------------------- setUserNamePasswordFromSharePreferences
    private fun setUserNamePasswordFromSharePreferences() {
        userName = sharedPreferences.getString(CompanionValues.userName, null)
        password = sharedPreferences.getString(CompanionValues.passcode, null)
    }
    //---------------------------------------------------------------------------------------------- setUserNamePasswordFromSharePreferences


    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword
    fun saveUserNameAndPassword(token : String?) {
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, token)
            .putString(CompanionValues.userName, userName)
            .putString(CompanionValues.passcode, password)
            .apply()
    }
    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword

}