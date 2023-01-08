package com.zarholding.zar.viewmodel

import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.enum.EnumAdminTaxiType
import com.zarholding.zar.model.enum.EnumPersonnelType
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.request.DriverChangeTripStatus
import com.zarholding.zar.model.request.TaxiChangeStatusRequest
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.repository.TaxiRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import javax.inject.Inject

@HiltViewModel
class AdminTaxiListViewModel @Inject constructor(
    private val taxiRepository: TaxiRepository,
    private val userRepository: UserRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {

    private lateinit var enumAdminTaxiType: EnumAdminTaxiType
    val taxiRequestListLiveData = SingleLiveEvent<List<AdminTaxiRequestModel>>()


    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType
    fun setEnumAdminTaxiType(type: String) {
        enumAdminTaxiType = enumValueOf(type)
    }
    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType


    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType
    fun getEnumAdminTaxiType() = enumAdminTaxiType
    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType


    //---------------------------------------------------------------------------------------------- getUserType
    fun getUserType() = userRepository.getUserType()
    //---------------------------------------------------------------------------------------------- getUserType


    //---------------------------------------------------------------------------------------------- getTaxiList
    fun getTaxiList() =  when (enumAdminTaxiType) {
        EnumAdminTaxiType.MY -> requestMyTaxiRequestList()
        EnumAdminTaxiType.REQUEST -> requestTaxiList()
        EnumAdminTaxiType.HISTORY -> requestHistoryTaxiList()
    }
    //---------------------------------------------------------------------------------------------- getTaxiList


    //---------------------------------------------------------------------------------------------- requestTaxiList
    private fun requestTaxiList() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val type = if (getUserType() == EnumPersonnelType.Driver)
                EnumPersonnelType.Driver
            else
                EnumPersonnelType.Personnel
            val response = taxiRepository.requestTaxiList(type)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { items ->
                            withContext(Main) {
                                taxiRequestListLiveData.value = items
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
    //---------------------------------------------------------------------------------------------- requestTaxiList


    //---------------------------------------------------------------------------------------------- requestHistoryTaxiList
    private fun requestHistoryTaxiList() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestTaxiList(getUserType())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { items ->
                            withContext(Main) {
                                taxiRequestListLiveData.value = items
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
    //---------------------------------------------------------------------------------------------- requestHistoryTaxiList


    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList
    private fun requestMyTaxiRequestList() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestMyTaxiRequestList()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        it.data?.let { items ->
                            withContext(Main) {
                                taxiRequestListLiveData.value = items
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
    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList


    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests
    fun requestChangeStatusOfTaxiRequests(request: TaxiChangeStatusRequest) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestChangeStatusOfTaxiRequests(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        setMessage(it.message)
                        getTaxiList()
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests


    //---------------------------------------------------------------------------------------------- requestDriverChangeTripStatus
    fun requestDriverChangeTripStatus(item: AdminTaxiRequestModel, lat: Double, lon: Double) {

        val status = when (item.tripStatus) {
            EnumTripStatus.Assigned -> EnumTripStatus.Started
            EnumTripStatus.Started -> EnumTripStatus.Finished
            else -> EnumTripStatus.None
        }
        val request = DriverChangeTripStatus(
            item.id,
            status,
            lat,
            lon
        )
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestDriverChangeTripStatus(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setMessage(it.message)
                    else {
                        setMessage(it.message)
                        taxiRepository.pageNumber = 0
                        getTaxiList()
                    }
                } ?: run {
                    setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                }
            } else
                setMessage(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestDriverChangeTripStatus


    //---------------------------------------------------------------------------------------------- userRepository
    fun getUser() = userRepository.getUser()
    //---------------------------------------------------------------------------------------------- userRepository


    //---------------------------------------------------------------------------------------------- setPageNumberToZero
    fun setPageNumberToZero() {
        taxiRepository.request.PageNumber = 0
    }
    //---------------------------------------------------------------------------------------------- setPageNumberToZero


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}