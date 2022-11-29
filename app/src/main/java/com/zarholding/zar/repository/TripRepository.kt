package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.RequestRegisterStationModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class TripRepository @Inject constructor(private val api : ApiSuperApp) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    fun requestGetAllTrips(token : String) = apiCall(emitter){api.requestGetAllTrips(token)}
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(registerStationModel: RequestRegisterStationModel, token : String) =
        apiCall(emitter){api.requestRegisterStation(registerStationModel, token)}
    //---------------------------------------------------------------------------------------------- requestRegisterStation


}