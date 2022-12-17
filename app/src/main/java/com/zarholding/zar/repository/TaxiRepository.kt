package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import javax.inject.Inject

class TaxiRepository @Inject constructor(private val api : ApiSuperApp) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    fun requestGetTaxiFavPlace(token : String) = apiCall(emitter) {api.requestGetTaxiFavPlace(token)}
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace



    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    fun requestAddFavPlace(request : TaxiAddFavPlaceRequest, token : String) =
        apiCall(emitter){api.requestAddFavPlace(request, token)}
    //---------------------------------------------------------------------------------------------- requestAddFavPlace



    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    fun requestDeleteFavPlace(id : Int, token : String) =
        apiCall(emitter){api.requestDeleteFavPlace(id, token)}
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    fun requestTaxi(request : TaxiRequestModel, token : String) =
        apiCall(emitter){api.requestTaxi(request, token)}
    //---------------------------------------------------------------------------------------------- requestTaxi


}