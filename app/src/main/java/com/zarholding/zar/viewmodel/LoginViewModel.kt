package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.repository.LoginRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
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

    var loginLiveDate = SingleLiveEvent<String?>()
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    var userName: String? = null
    var password: String? = null
    private var job: Job? = null


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(response: Response<*>?) {
        withContext(Main) {
            checkResponseError(response, errorLiveDate)
        }
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError



    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(message : String) {
        withContext(Main) {
            errorLiveDate.value = ErrorApiModel(EnumApiError.Error, message)
        }
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError




    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin() {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            CoroutineScope(Main).launch {
                throwable.localizedMessage?.let { setError(it) }
            }
        }

        job = CoroutineScope(IO + exceptionHandler).launch {
            val response = repository.requestLogin(LoginRequestModel(userName!!, password!!))
            withContext(Main) {
                if (response?.isSuccessful == true) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        if (!it.hasError)
                            saveUserNameAndPassword(it.data)
                        setError(it.message)
                    }
                } else
                    setError(response)
            }
        }
    }
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
    private fun saveUserNameAndPassword(token : String?) {
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, token)
            .putString(CompanionValues.userName, userName)
            .putString(CompanionValues.passcode, password)
            .apply()
        loginLiveDate.value = token
    }
    //---------------------------------------------------------------------------------------------- saveUserNameAndPassword


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared

}