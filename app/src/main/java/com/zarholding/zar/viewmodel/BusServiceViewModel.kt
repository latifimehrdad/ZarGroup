package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class BusServiceViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private var job: Job? = null
    private var tripList: List<TripModel>? = null
    val errorLiveDate = MutableLiveData<ErrorApiModel>()
    val tripModelLiveData = MutableLiveData<List<TripModel>?>()


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(response: Response<*>?) {
        job?.cancel()
        withContext(Main) {
            checkResponseError(response, errorLiveDate)
        }
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(message: String) {
        job?.cancel()
        withContext(Main) {
            errorLiveDate.value = ErrorApiModel(EnumApiError.Error, message)
        }
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    fun requestGetAllTrips() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestGetAllTrips(tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        tripList = it.data
                        withContext(Main) {
                            tripModelLiveData.value = tripList
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(tripId: Int, stationId: Int) {
        val requestModel = RequestRegisterStationModel(tripId, stationId)
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestRegisterStation(
                requestModel,
                tokenRepository.getBearerToken()
            )
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        requestGetAllTrips()
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation
    fun requestDeleteRegisteredStation(id: Int) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestDeleteRegisteredStation(
                id,
                tokenRepository.getBearerToken()
            )
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        requestGetAllTrips()
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisteredStation



    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler


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
    fun getToken() = tokenRepository.getToken()
    //---------------------------------------------------------------------------------------------- getToken


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}