package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.response.trip.RegisterStationModel
import com.zarholding.zar.repository.RegisterStationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/27/2022.
 */

@HiltViewModel
class RegisterStationViewModel @Inject constructor(
    private val repository: RegisterStationRepository
) : ViewModel() {

    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(registerStationModel: RegisterStationModel, token : String) =
        repository.requestRegisterStation(registerStationModel, token)
    //---------------------------------------------------------------------------------------------- requestRegisterStation

}