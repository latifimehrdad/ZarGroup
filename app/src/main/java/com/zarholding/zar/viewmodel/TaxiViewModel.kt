package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.tools.api.apiCall
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import com.zarholding.zar.repository.TaxiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaxiViewModel @Inject constructor(private val repo: TaxiRepository) : ViewModel() {

    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    fun requestGetTaxiFavPlace(token : String) = repo.requestGetTaxiFavPlace(token)
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace



    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    fun requestAddFavPlace(request : TaxiAddFavPlaceRequest, token : String) =
        repo.requestAddFavPlace(request, token)
    //---------------------------------------------------------------------------------------------- requestAddFavPlace


    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    fun requestDeleteFavPlace(id : Int, token : String) = repo.requestDeleteFavPlace(id, token)
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    fun requestTaxi(request : TaxiRequestModel, token : String) =
        repo.requestTaxi(request, token)
    //---------------------------------------------------------------------------------------------- requestTaxi
}