package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class TripRepository @Inject constructor(
    private val api: ApiSuperApp,
    private val tokenRepository: TokenRepository
) {


    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    suspend fun requestGetAllTrips() =
        apiCall { api.requestGetAllTrips(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    suspend fun requestRegisterStation(request : RequestRegisterStationModel) =
        apiCall { api.requestRegisterStation(request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    suspend fun requestDeleteRegisteredStation(id: Int) =
        apiCall { api.requestDeleteRegisteredStation(id, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation


    //---------------------------------------------------------------------------------------------- requestGetTripForRegister
    suspend fun requestGetTripForRegister() =
        apiCall { api.requestGetTripForRegister(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetTripForRegister


    //---------------------------------------------------------------------------------------------- requestChangeStatusTripRegister
    suspend fun requestChangeStatusTripRegister(
        request: List<TripRequestRegisterStatusModel>
    ) = apiCall { api.requestChangeStatusTripRegister(request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestChangeStatusTripRegister


    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken

}