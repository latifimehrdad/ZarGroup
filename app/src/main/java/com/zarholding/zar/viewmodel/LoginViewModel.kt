package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zar.core.tools.api.apiCall
import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(var repository: LoginRepository) : ViewModel() {

    var userName: String? = null
    var passcode: String? = null
    var loadingLiveDate = MutableLiveData(false)

    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin(login: LoginRequestModel) = repository.requestLogin(login)
    //---------------------------------------------------------------------------------------------- requestLogin

    fun requestTestApi(code : String) = repository.requestTestApi(code)

}