package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.repository.LoginRepository
import com.zarholding.zar.utility.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(var repository: LoginRepository) : ViewModel() {

    var userName: String? = null
    var password: String? = null
    var loadingLiveDate = MutableLiveData(false)

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin() = repository.requestLogin(LoginRequestModel(userName!!, password!!))
    //---------------------------------------------------------------------------------------------- requestLogin


    //---------------------------------------------------------------------------------------------- getBiometricEnable
    fun getBiometricEnable() = sharedPreferences.getBoolean(CompanionValues.biometric, false)
    //---------------------------------------------------------------------------------------------- getBiometricEnable


    //---------------------------------------------------------------------------------------------- setUserNamePasswordFromSharePreferences
    fun setUserNamePasswordFromSharePreferences() {
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