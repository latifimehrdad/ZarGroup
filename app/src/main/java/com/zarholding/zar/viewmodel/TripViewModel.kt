package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

@HiltViewModel
class TripViewModel @Inject constructor(private val repository: TripRepository) : ViewModel(){

    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    fun requestGetAllTrips(token : String) = repository.requestGetAllTrips(token)
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(registerStationModel: RequestRegisterStationModel, token : String) =
        repository.requestRegisterStation(registerStationModel, token)
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    fun requestDeleteRegisteredStation(id: Int, token: String) =
        repository.requestDeleteRegisteredStation(id, token)
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation

}