package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zar.core.view.picker.time.ZarTimePicker
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.enum.FavPlaceType
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import com.zarholding.zar.model.response.taxi.TaxiFavPlaceModel
import com.zarholding.zar.repository.TaxiRepository
import com.zarholding.zar.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.osmdroid.views.overlay.Marker
import javax.inject.Inject

@HiltViewModel
class TaxiViewModel @Inject constructor(
    private val repo: TaxiRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    var loadingLiveDate = MutableLiveData(false)
    private var favPlace: MutableList<TaxiFavPlaceModel>? = null
    private var originFavPlaceModel: TaxiFavPlaceModel? = null
    private var destinationFavPlaceModel: TaxiFavPlaceModel? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var favPlaceType = FavPlaceType.NONE
    private var timePickMode = ZarTimePicker.PickerMode.RETURNING
    private var enumTaxiRequestType = EnumTaxiRequestType.OneWay

    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    fun requestGetTaxiFavPlace() = repo.requestGetTaxiFavPlace(tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace


    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    fun requestAddFavPlace(request: TaxiAddFavPlaceRequest) =
        repo.requestAddFavPlace(request, tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestAddFavPlace


    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    fun requestDeleteFavPlace(id: Int) =
        repo.requestDeleteFavPlace(id, tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    fun requestTaxi(request: TaxiRequestModel) =
        repo.requestTaxi(request, tokenRepository.getBearerToken())
    //---------------------------------------------------------------------------------------------- requestTaxi


    //---------------------------------------------------------------------------------------------- setFavPlaceList
    fun setFavPlaceList(favPlace: List<TaxiFavPlaceModel>) {
        this.favPlace = favPlace.toMutableList()
    }
    //---------------------------------------------------------------------------------------------- setFavPlaceList


    //---------------------------------------------------------------------------------------------- addToFavPlaceList
    fun addToFavPlaceList(element: TaxiFavPlaceModel) {
        if (favPlace == null)
            favPlace = mutableListOf()
        favPlace!!.add(element)
    }
    //---------------------------------------------------------------------------------------------- addToFavPlaceList


    //---------------------------------------------------------------------------------------------- removeItemInFavPlace
    fun removeItemInFavPlace(id: Int) {
        favPlace?.removeIf { it.id == id }
    }
    //---------------------------------------------------------------------------------------------- removeItemInFavPlace


    //---------------------------------------------------------------------------------------------- getFavPlace
    fun getFavPlaceList() = favPlace
    //---------------------------------------------------------------------------------------------- getFavPlace


    //---------------------------------------------------------------------------------------------- setOriginFavPlaceModel
    fun setOriginFavPlaceModel(item: TaxiFavPlaceModel?) {
        originFavPlaceModel = item
    }
    //---------------------------------------------------------------------------------------------- setOriginFavPlaceModel


    //---------------------------------------------------------------------------------------------- getOriginFacPlaceModel
    fun getOriginFacPlaceModel() = originFavPlaceModel
    //---------------------------------------------------------------------------------------------- getOriginFacPlaceModel


    //---------------------------------------------------------------------------------------------- setDestinationFavPlaceModel
    fun setDestinationFavPlaceModel(item: TaxiFavPlaceModel?) {
        destinationFavPlaceModel = item
    }
    //---------------------------------------------------------------------------------------------- setDestinationFavPlaceModel


    //---------------------------------------------------------------------------------------------- getDestinationFavPlaceModel
    fun getDestinationFavPlaceModel() = destinationFavPlaceModel
    //---------------------------------------------------------------------------------------------- getDestinationFavPlaceModel


    //---------------------------------------------------------------------------------------------- getOriginMarker
    fun getOriginMarker() = originMarker
    //---------------------------------------------------------------------------------------------- getOriginMarker


    //---------------------------------------------------------------------------------------------- setOriginMarker
    fun setOriginMarker(originMarker: Marker?) {
        this.originMarker = originMarker
    }
    //---------------------------------------------------------------------------------------------- setOriginMarker


    //---------------------------------------------------------------------------------------------- getDestinationMarker
    fun getDestinationMarker() = destinationMarker
    //---------------------------------------------------------------------------------------------- getDestinationMarker


    //---------------------------------------------------------------------------------------------- setDestinationMarker
    fun setDestinationMarker(destinationMarker: Marker?) {
        this.destinationMarker = destinationMarker
    }
    //---------------------------------------------------------------------------------------------- setDestinationMarker


    //---------------------------------------------------------------------------------------------- setFavPlaceType
    fun setFavPlaceType(favPlaceType: FavPlaceType) {
        this.favPlaceType = favPlaceType
    }
    //---------------------------------------------------------------------------------------------- setFavPlaceType


    //---------------------------------------------------------------------------------------------- getFavPlaceType
    fun getFavPlaceType() = favPlaceType
    //---------------------------------------------------------------------------------------------- getFavPlaceType



    //---------------------------------------------------------------------------------------------- setTimePickMode
    fun setTimePickMode(timePickMode : ZarTimePicker.PickerMode) {
        this.timePickMode = timePickMode
        enumTaxiRequestType = if (timePickMode == ZarTimePicker.PickerMode.DEPARTURE)
            EnumTaxiRequestType.OneWay
        else
            EnumTaxiRequestType.Return
    }
    //---------------------------------------------------------------------------------------------- setTimePickMode


    //---------------------------------------------------------------------------------------------- getTimePickMode
    fun getTimePickMode() = timePickMode
    //---------------------------------------------------------------------------------------------- getTimePickMode


    //---------------------------------------------------------------------------------------------- getEnumTaxiRequest
    fun getEnumTaxiRequest() = enumTaxiRequestType
    //---------------------------------------------------------------------------------------------- getEnumTaxiRequest
}