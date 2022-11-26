package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class TripRepository @Inject constructor(private val api : ApiSuperApp) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestGetTrips
    fun requestGetTrips(token : String) = apiCall(emitter){api.requestGetTrips(token)}
    //---------------------------------------------------------------------------------------------- requestGetTrips


}