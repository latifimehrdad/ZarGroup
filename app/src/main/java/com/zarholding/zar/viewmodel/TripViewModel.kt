package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.tools.api.apiCall
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
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


    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister
    fun requestGetTripRequestRegister(token: String) =
        repository.requestGetTripRequestRegister(token)
    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister


    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister
    fun requestConfirmAndRejectTripRequestRegister(
        request: List<TripRequestRegisterStatusModel>,
        token: String
    ) = repository.requestConfirmAndRejectTripRequestRegister(request, token)
    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister


}