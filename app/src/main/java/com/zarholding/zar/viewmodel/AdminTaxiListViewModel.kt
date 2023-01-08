package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
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
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdminTaxiListViewModel @Inject constructor(
    private val taxiRepository: TaxiRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var enumAdminTaxiType: EnumAdminTaxiType? = null
    private var job: Job? = null
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    val taxiRequestListLiveData = SingleLiveEvent<List<AdminTaxiRequestModel>>()

    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(response: Response<*>?) {
        withContext(Main) {
            checkResponseError(response, errorLiveDate)
        }
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError


    //---------------------------------------------------------------------------------------------- setError
    private suspend fun setError(message: String) {
        withContext(Main) {
            errorLiveDate.value = ErrorApiModel(EnumApiError.Error, message)
        }
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- setError



    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType
    fun setEnumAdminTaxiType(type: String) {
        when (type) {
            EnumAdminTaxiType.REQUEST.name -> enumAdminTaxiType = EnumAdminTaxiType.REQUEST
            EnumAdminTaxiType.HISTORY.name -> enumAdminTaxiType = EnumAdminTaxiType.HISTORY
            EnumAdminTaxiType.MY.name -> enumAdminTaxiType = EnumAdminTaxiType.MY
        }
    }
    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType




    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType
    fun getEnumAdminTaxiType() = enumAdminTaxiType
    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType



    //---------------------------------------------------------------------------------------------- getUserType
    fun getUserType() = userRepository.getUserType()
    //---------------------------------------------------------------------------------------------- getUserType



    //---------------------------------------------------------------------------------------------- getTaxiList
    fun getTaxiList() = when(enumAdminTaxiType!!) {
        EnumAdminTaxiType.MY -> requestMyTaxiRequestList()
        EnumAdminTaxiType.REQUEST ->  requestTaxiList()
        EnumAdminTaxiType.HISTORY -> requestHistoryTaxiList()
    }
    //---------------------------------------------------------------------------------------------- getTaxiList



    //---------------------------------------------------------------------------------------------- requestTaxiList
    private fun requestTaxiList() {
        job = CoroutineScope(IO + exceptionHandler()) .launch {
            val type = if (getUserType() == EnumPersonnelType.Driver)
                EnumPersonnelType.Driver
            else
                EnumPersonnelType.Personnel
            val response = taxiRepository.requestTaxiList(type)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let {items ->
                            withContext(Main){
                                taxiRequestListLiveData.value = items
                            }
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestTaxiList



    //---------------------------------------------------------------------------------------------- requestHistoryTaxiList
    private fun requestHistoryTaxiList() {
        job = CoroutineScope(IO + exceptionHandler()) .launch {
            val response = taxiRepository.requestTaxiList(getUserType())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let {items ->
                            withContext(Main){
                                taxiRequestListLiveData.value = items
                            }
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestHistoryTaxiList



    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList
    private fun requestMyTaxiRequestList() {
        job = CoroutineScope(IO + exceptionHandler()) .launch {
            val response = taxiRepository.requestMyTaxiRequestList()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let {items ->
                            withContext(Main){
                                taxiRequestListLiveData.value = items
                            }
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }

    }
    //---------------------------------------------------------------------------------------------- requestMyTaxiRequestList



    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests
    fun requestChangeStatusOfTaxiRequests(request : TaxiChangeStatusRequest) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = taxiRepository.requestChangeStatusOfTaxiRequests(request)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        setError(it.message)
                        getTaxiList()
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests



    //---------------------------------------------------------------------------------------------- requestDriverChangeTripStatus
    fun requestDriverChangeTripStatus(item : AdminTaxiRequestModel,lat : Double, lon : Double) {

        val status = when(item.tripStatus) {
            EnumTripStatus.Assigned -> EnumTripStatus.Started
            EnumTripStatus.Started -> EnumTripStatus.Finished
            else ->{
                EnumTripStatus.None
            }
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
                        setError(it.message)
                    else {
                        setError(it.message)
                        taxiRepository.pageNumber = 0
                        getTaxiList()
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestDriverChangeTripStatus




    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler


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