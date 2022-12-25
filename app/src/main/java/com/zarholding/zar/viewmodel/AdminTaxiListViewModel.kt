package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.enum.EnumAdminTaxiType
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
        }
    }
    //---------------------------------------------------------------------------------------------- setEnumAdminTaxiType


    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType
    fun getEnumAdminTaxiType() = enumAdminTaxiType
    //---------------------------------------------------------------------------------------------- getEnumAdminTaxiType



    //---------------------------------------------------------------------------------------------- getTaxiList
    fun getTaxiList() = when(enumAdminTaxiType!!) {
        EnumAdminTaxiType.REQUEST ->  requestTaxiList()
        EnumAdminTaxiType.HISTORY -> requestMyTaxiRequestList()
    }
    //---------------------------------------------------------------------------------------------- getTaxiList



    //---------------------------------------------------------------------------------------------- requestTaxiList
    private fun requestTaxiList() {
        job = CoroutineScope(IO + exceptionHandler()) .launch {
            val response = taxiRepository.requestTaxiList()
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
        CoroutineScope(IO + exceptionHandler()).launch {
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


    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler


    //--------------------------------------
    fun getUser() = userRepository.getUser()


    //---------------------------------------------------------------------------------------------- isAdministrativeUser
    fun isAdministrativeUser() = userRepository.getUser()?.isAdministrative
    //---------------------------------------------------------------------------------------------- isAdministrativeUser



    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}