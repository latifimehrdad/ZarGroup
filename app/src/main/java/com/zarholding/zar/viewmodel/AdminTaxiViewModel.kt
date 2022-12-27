package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zarholding.zar.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminTaxiViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){

    companion object {
        val requestTaxiLiveDate = MutableLiveData<Int>()
        val myTaxiLiveDate = MutableLiveData<Int>()
    }

    //---------------------------------------------------------------------------------------------- getUserType
    fun getUserType() = userRepository.getUserType()
    //---------------------------------------------------------------------------------------------- getUserType

}