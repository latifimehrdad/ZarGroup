package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.TripRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import zar.R
import javax.inject.Inject

@HiltViewModel
class BusServiceViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {

    private var tripList: List<TripModel>? = null
    val tripModelLiveData = SingleLiveEvent<List<TripModel>?>()


    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    fun requestGetAllTrips() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestGetAllTrips()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        tripList = it.data
                        withContext(Main) {
                            tripModelLiveData.value = tripList
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(tripId: Int, stationId: Int) {
        val requestModel = RequestRegisterStationModel(tripId, stationId)
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestRegisterStation(requestModel)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else
                        requestGetAllTrips()
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    fun requestDeleteRegisteredStation(id: Int) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestDeleteRegisteredStation(id)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else
                        requestGetAllTrips()
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation


    //---------------------------------------------------------------------------------------------- getAllTripList
    fun getAllTripList(): List<TripModel>? {
        tripList?.let {
            return tripList!!.filter { it.myStationTripId == 0 }
        }
        return null
    }
    //---------------------------------------------------------------------------------------------- getAllTripList


    //---------------------------------------------------------------------------------------------- getMyTripList
    fun getMyTripList(): List<TripModel>? {
        tripList?.let {
            return tripList!!.filter { it.myStationTripId != 0 }
        }
        return null
    }
    //---------------------------------------------------------------------------------------------- getMyTripList


    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = tripRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken


}