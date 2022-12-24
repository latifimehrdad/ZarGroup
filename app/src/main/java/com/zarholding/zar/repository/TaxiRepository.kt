package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiChangeStatusRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import javax.inject.Inject

class TaxiRepository @Inject constructor(private val api : ApiSuperApp) {


    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    suspend fun requestGetTaxiFavPlace(token : String) =
        apiCall{ api.requestGetTaxiFavPlace(token) }
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace



    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    suspend fun requestAddFavPlace(request : TaxiAddFavPlaceRequest, token : String) =
        apiCall{ api.requestAddFavPlace(request, token) }
    //---------------------------------------------------------------------------------------------- requestAddFavPlace



    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    suspend fun requestDeleteFavPlace(id : Int, token : String) =
        apiCall{ api.requestDeleteFavPlace(id, token) }
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    suspend fun requestTaxi(request : TaxiRequestModel, token : String) =
        apiCall{ api.requestTaxi(request, token) }
    //---------------------------------------------------------------------------------------------- requestTaxi


    //---------------------------------------------------------------------------------------------- requestTaxiList
    suspend fun requestTaxiList(token : String) = apiCall{api.requestTaxiList(token)}
    //---------------------------------------------------------------------------------------------- requestTaxiList


    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList
    suspend fun requestMyTaxiRequestList(token : String) =
        apiCall{api.requestMyTaxiRequestList(token)}
    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList


    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests
    suspend fun requestChangeStatusOfTaxiRequests(request : TaxiChangeStatusRequest, token : String) =
        apiCall{api.requestChangeStatusOfTaxiRequests(request, token)}
    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests

}