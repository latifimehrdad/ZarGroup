package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: UserRepository) : ViewModel(){

    //---------------------------------------------------------------------------------------------- requestUserInfo
    fun requestUserInfo(token: String) = repository.requestUserInfo(token)
    //---------------------------------------------------------------------------------------------- requestUserInfo

}