package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.tools.api.apiCall
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: TripRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    fun requestGetAllTrips() = repository.requestGetAllTrips(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(registerStationModel: RequestRegisterStationModel) =
        repository.requestRegisterStation(registerStationModel, tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    fun requestDeleteRegisteredStation(id: Int) =
        repository.requestDeleteRegisteredStation(id, tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation


    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister
    fun requestGetTripRequestRegister() =
        repository.requestGetTripRequestRegister(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister


    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister
    fun requestConfirmAndRejectTripRequestRegister(
        request: List<TripRequestRegisterStatusModel>
    ) = repository
        .requestConfirmAndRejectTripRequestRegister(request, tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister


    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken


}