package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.enum.EnumAdminTaxiType
import com.zarholding.zar.model.request.TaxiChangeStatusRequest
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.repository.TaxiRepository
import com.zarholding.zar.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdminTaxiListViewModel @Inject constructor(
    private val taxiRepository: TaxiRepository
) : ViewModel() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var enumAdminTaxiType: EnumAdminTaxiType? = null
    private var job: Job? = null
    val errorLiveDate = MutableLiveData<ErrorApiModel>()
    val taxiRequestListLiveData = MutableLiveData<List<AdminTaxiRequestModel>>()


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
            val response = taxiRepository.requestTaxiList(tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let {items ->
                            taxiRequestListLiveData.value = items
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
            val response = taxiRepository.requestMyTaxiRequestList(tokenRepository.getBearerToken())
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let {items ->
                            taxiRequestListLiveData.value = items
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
            val response = taxiRepository.requestChangeStatusOfTaxiRequests(
                request,
                tokenRepository.getBearerToken()
            )
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
        CoroutineScope(Dispatchers.Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler



    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}