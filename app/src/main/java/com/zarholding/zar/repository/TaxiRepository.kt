package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.*
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestResponse
import retrofit2.Response
import javax.inject.Inject

class TaxiRepository @Inject constructor(
    private val api: ApiSuperApp,
    private val tokenRepository: TokenRepository
) {

    private var pageNumber = 0
    private val pageSize = 3
    val request = OnlyPagingListRequest(pageNumber, pageSize)

    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    suspend fun requestGetTaxiFavPlace() =
        apiCall { api.requestGetTaxiFavPlace(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace


    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    suspend fun requestAddFavPlace(request: TaxiAddFavPlaceRequest) =
        apiCall { api.requestAddFavPlace(request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestAddFavPlace


    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    suspend fun requestDeleteFavPlace(id: Int) =
        apiCall { api.requestDeleteFavPlace(id, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    suspend fun requestTaxi(request: TaxiRequestModel) =
        apiCall { api.requestTaxi(request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestTaxi


    //---------------------------------------------------------------------------------------------- requestTaxiList
    suspend fun requestTaxiList() =
        apiCall { api.requestTaxiList(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestTaxiList


    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList
    suspend fun requestMyTaxiRequestList() : Response<AdminTaxiRequestResponse>? {
        request.PageNumber++
        return apiCall { api.requestMyTaxiRequestList(request, tokenRepository.getBearerToken()) }
    }
    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList


    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests
    suspend fun requestChangeStatusOfTaxiRequests(request: TaxiChangeStatusRequest) =
        apiCall { api.requestChangeStatusOfTaxiRequests(request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests


    //---------------------------------------------------------------------------------------------- requestAssignDriverToRequest
    suspend fun requestAssignDriverToRequest(request: AssignDriverRequest) =
        apiCall { api.requestAssignDriverToRequest(request, tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestAssignDriverToRequest

}