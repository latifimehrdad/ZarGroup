package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zar.core.view.picker.time.ZarTimePicker
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.enum.FavPlaceType
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zar.model.response.taxi.TaxiFavPlaceModel
import com.zarholding.zar.repository.AddressRepository
import com.zarholding.zar.repository.TaxiRepository
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TaxiReservationViewModel @Inject constructor(
    private val taxiRepository: TaxiRepository,
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private var job: Job? = null
    val errorLiveDate = MutableLiveData<ErrorApiModel>()
    val setFavPlaceLiveData = MutableLiveData<Boolean>()
    val addFavPlaceLiveData = MutableLiveData<Boolean>()
    val removeFavPlaceLiveData = MutableLiveData<Boolean>()
    val sendRequestLiveData = MutableLiveData<Boolean>()
    val addressLiveData = MutableLiveData<AddressModel>()
    private var favPlace: MutableList<TaxiFavPlaceModel>? = null
    private var originFavPlaceModel: TaxiFavPlaceModel? = null
    private var destinationFavPlaceModel: TaxiFavPlaceModel? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var favPlaceType = FavPlaceType.NONE
    private var timePickMode = ZarTimePicker.PickerMode.RETURNING
    private var enumTaxiRequestType = EnumTaxiRequestType.OneWay


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(response: Response<*>?) {
        job?.cancel()
        withContext(Dispatchers.Main) {
            checkResponseError(response, errorLiveDate)
        }
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(message: String) {
        job?.cancel()
        withContext(Dispatchers.Main) {
            errorLiveDate.value = ErrorApiModel(EnumApiError.Error, message)
        }
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    fun requestGetTaxiFavPlace() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestGetTaxiFavPlace(tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        it.data?.let { items ->
                            setFavPlaceList(items)
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace


    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    fun requestAddFavPlace(request: TaxiAddFavPlaceRequest) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestAddFavPlace(
                request,
                tokenRepository.getBearerToken()
            )
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        it.data?.let { item ->
                            when (getFavPlaceType()) {
                                FavPlaceType.ORIGIN -> {
                                    setOriginFavPlaceModel(item)
                                }
                                FavPlaceType.DESTINATION -> {
                                    setDestinationFavPlaceModel(item)
                                }
                                else -> {}
                            }
                            addToFavPlaceList(item)
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestAddFavPlace


    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    fun requestDeleteFavPlace(id: Int) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestDeleteFavPlace(
                id,
                tokenRepository.getBearerToken()
            )
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        removeItemInFavPlace(id)
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    fun requestTaxi(request: TaxiRequestModel) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestTaxi(request, tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    setError(it.message)
                    if (!it.hasError)
                        sendRequestLiveData.value = true
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestTaxi



    //---------------------------------------------------------------------------------------------- getAddress
    fun getAddress(geoPoint: GeoPoint) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = addressRepository.requestGetAddress(geoPoint)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    it.address?.let { address ->
                        addressLiveData.value = address
                    } ?: run {
                        setError("اطلاعات خالی است")
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- getAddress




    //---------------------------------------------------------------------------------------------- setFavPlaceList
    private fun setFavPlaceList(favPlace: List<TaxiFavPlaceModel>) {
        this.favPlace = favPlace.toMutableList()
        setFavPlaceLiveData.value = true
    }
    //---------------------------------------------------------------------------------------------- setFavPlaceList


    //---------------------------------------------------------------------------------------------- addToFavPlaceList
    fun addToFavPlaceList(element: TaxiFavPlaceModel) {
        if (favPlace == null)
            favPlace = mutableListOf()
        favPlace!!.add(element)
        addFavPlaceLiveData.value = true
    }
    //---------------------------------------------------------------------------------------------- addToFavPlaceList


    //---------------------------------------------------------------------------------------------- removeItemInFavPlace
    fun removeItemInFavPlace(id: Int) {
        favPlace?.removeIf { it.id == id }
        removeFavPlaceLiveData.value = true
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
    fun setTimePickMode(timePickMode: ZarTimePicker.PickerMode) {
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


    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Dispatchers.Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler



    //---------------------------------------------------------------------------------------------- getUser
    fun getUser(): UserInfoEntity? {
        return userRepository.getUser()
    }
    //---------------------------------------------------------------------------------------------- getUser



    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}