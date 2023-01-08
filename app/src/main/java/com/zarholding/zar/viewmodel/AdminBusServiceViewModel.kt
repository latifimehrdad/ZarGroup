package com.zarholding.zar.viewmodel

import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import com.zarholding.zar.model.response.trip.TripRequestRegisterModel
import com.zarholding.zar.repository.TripRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import javax.inject.Inject

@HiltViewModel
class AdminBusServiceViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {

    val tripModelLiveData = SingleLiveEvent<List<TripRequestRegisterModel>>()


    //---------------------------------------------------------------------------------------------- requestGetTripForRegister
    fun requestGetTripForRegister() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestGetTripForRegister()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { list ->
                            withContext(Main) {
                                tripModelLiveData.value = list
                            }
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                        }
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetTripForRegister


    //---------------------------------------------------------------------------------------------- requestChangeStatusTripRegister
    fun requestChangeStatusTripRegister(request: List<TripRequestRegisterStatusModel>) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestChangeStatusTripRegister(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else
                        requestGetTripForRegister()
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestChangeStatusTripRegister

}