package com.zarholding.zar.viewmodel

import com.zar.core.view.picker.time.ZarTimePicker
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.enum.FavPlaceType
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zar.model.response.company.CompanyModel
import com.zarholding.zar.model.response.taxi.TaxiFavPlaceModel
import com.zarholding.zar.repository.*
import com.zarholding.zar.utility.RoleManager
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import javax.inject.Inject

@HiltViewModel
class TaxiReservationViewModel @Inject constructor(
    private val taxiRepository: TaxiRepository,
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
    private val roleManager: RoleManager
) : ZarViewModel() {

    val setFavPlaceLiveData = SingleLiveEvent<Boolean>()
    val addFavPlaceLiveData = SingleLiveEvent<Boolean>()
    val removeFavPlaceLiveData = SingleLiveEvent<Boolean>()
    val sendRequestLiveData = SingleLiveEvent<String>()
    val addressLiveData = SingleLiveEvent<AddressModel>()
    var companySelected : CompanyModel? = null

    private var favPlace : MutableList<TaxiFavPlaceModel>? = null
    private var companies : MutableList<CompanyModel>? = null
    private var originFavPlaceModel: TaxiFavPlaceModel? = null
    private var destinationFavPlaceModel: TaxiFavPlaceModel? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var favPlaceType = FavPlaceType.NONE
    private var timePickMode = ZarTimePicker.PickerMode.RETURNING
    private var enumTaxiRequestType = EnumTaxiRequestType.OneWay


    //---------------------------------------------------------------------------------------------- getSpinnersData
    fun getSpinnersData() {
        job = CoroutineScope(IO).launch {
            requestGetCompanies().join()
            requestGetTaxiFavPlace().join()
        }
    }
    //---------------------------------------------------------------------------------------------- getSpinnersData


    //---------------------------------------------------------------------------------------------- requestGetCompanies
    private fun requestGetCompanies() : Job {
        return CoroutineScope(IO + exceptionHandler()).launch {
            delay(1000)
            val response = companyRepository.requestGetCompanies()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    it.data?.let { items ->
                        companies = items.items.toMutableList()
                    }?: run {
                        setMessage("اطلاعات خالی است")
                    }
                }?: run {
                    setMessage("اطلاعات خالی است")
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetCompanies



    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    private fun requestGetTaxiFavPlace() : Job {
         return CoroutineScope(IO + exceptionHandler()).launch {
             delay(1000)
            val response = taxiRepository.requestGetTaxiFavPlace()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else
                        it.data?.let { items ->
                            favPlace = items.toMutableList()
                            withContext(Main) {
                                setFavPlaceLiveData.value = true
                            }
                        } ?: run {
                            setMessage("اطلاعات خالی است")
                        }
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace



    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    fun requestAddFavPlace(request: TaxiAddFavPlaceRequest) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestAddFavPlace(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
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
                            withContext(Main) {
                                addToFavPlaceList(item)
                            }
                        } ?: run {
                            setMessage("اطلاعات خالی است")
                        }
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestAddFavPlace


    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace
    fun requestDeleteFavPlace(id: Int) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestDeleteFavPlace(id)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else
                        withContext(Main) {
                            removeItemInFavPlace(id)
                        }
                } ?: run {
                    setMessage("اطلاعات خالی است")
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestDeleteFavPlace


    //---------------------------------------------------------------------------------------------- requestTaxi
    fun requestTaxi(request: TaxiRequestModel) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestTaxi(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (!it.hasError)
                        withContext(Main) {
                            sendRequestLiveData.value = it.message
                        }
                } ?: run {
                    setMessage("اطلاعات خالی است")
                }
            } else
                setMessage(response)
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
                        withContext(Main){
                            addressLiveData.value = address
                        }
                    } ?: run {
                        setMessage("اطلاعات خالی است")
                    }
                } ?: run {
                    setMessage("اطلاعات خالی است")
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- getAddress



    //---------------------------------------------------------------------------------------------- addToFavPlaceList
    private fun addToFavPlaceList(element: TaxiFavPlaceModel) {
        if (favPlace == null)
            favPlace = mutableListOf()
        favPlace!!.add(element)
        addFavPlaceLiveData.postValue(true)
    }
    //---------------------------------------------------------------------------------------------- addToFavPlaceList


    //---------------------------------------------------------------------------------------------- removeItemInFavPlace
    private fun removeItemInFavPlace(id: Int) {
        favPlace?.removeIf { it.id == id }
        removeFavPlaceLiveData.value = true
    }
    //---------------------------------------------------------------------------------------------- removeItemInFavPlace


    //---------------------------------------------------------------------------------------------- getFavPlace
    fun getFavPlaceList() = favPlace
    //---------------------------------------------------------------------------------------------- getFavPlace


    //---------------------------------------------------------------------------------------------- getCompaniesList
    fun getCompaniesList() = companies
    //---------------------------------------------------------------------------------------------- getCompaniesList


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


    //---------------------------------------------------------------------------------------------- getUser
    fun getUser(): UserInfoEntity? {
        return userRepository.getUser()
    }
    //---------------------------------------------------------------------------------------------- getUser


    //---------------------------------------------------------------------------------------------- isDisableDaysAgo
    fun isDisableDaysAgo() = roleManager.isDisableDaysAgo()
    //---------------------------------------------------------------------------------------------- isDisableDaysAgo

}