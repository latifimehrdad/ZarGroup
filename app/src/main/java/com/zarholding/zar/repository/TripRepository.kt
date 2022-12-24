package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class TripRepository @Inject constructor(private val api: ApiSuperApp) {


    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    suspend fun requestGetAllTrips(token: String) = apiCall { api.requestGetAllTrips(token) }
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    suspend fun requestRegisterStation(registerStationModel: RequestRegisterStationModel, token: String) =
        apiCall { api.requestRegisterStation(registerStationModel, token) }
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    suspend fun requestDeleteRegisteredStation(id: Int, token: String) =
        apiCall { api.requestDeleteRegisteredStation(id, token) }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation


    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister
    suspend fun requestGetTripRequestRegister(token: String) =
        apiCall { api.requestGetTripRequestRegister(token) }
    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister


    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister
    suspend fun requestConfirmAndRejectTripRequestRegister(
        request: List<TripRequestRegisterStatusModel>,
        token: String
    ) = apiCall { api.requestConfirmAndRejectTripRequestRegister(request, token) }
    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister

}