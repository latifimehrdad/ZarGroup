package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class TripRepository @Inject constructor(private val api: ApiSuperApp) {

    @Inject
    lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    fun requestGetAllTrips(token: String) = apiCall(emitter) { api.requestGetAllTrips(token) }
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(registerStationModel: RequestRegisterStationModel, token: String) =
        apiCall(emitter) { api.requestRegisterStation(registerStationModel, token) }
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    fun requestDeleteRegisteredStation(id: Int, token: String) =
        apiCall(emitter) { api.requestDeleteRegisteredStation(id, token) }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation


    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister
    fun requestGetTripRequestRegister(token: String) =
        apiCall(emitter) { api.requestGetTripRequestRegister(token) }
    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister


    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister
    fun requestConfirmAndRejectTripRequestRegister(
        request: List<TripRequestRegisterStatusModel>,
        token: String
    ) = apiCall(emitter) { api.requestConfirmAndRejectTripRequestRegister(request, token) }
    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister

}