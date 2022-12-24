package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import com.zarholding.zar.model.response.trip.TripRequestRegisterModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdminBusServiceViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private var job: Job? = null
    val errorLiveDate = MutableLiveData<ErrorApiModel>()
    val tripModelLiveData = MutableLiveData<List<TripRequestRegisterModel>>()


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


    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister
    fun requestGetTripRequestRegister() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestGetTripRequestRegister(
                tokenRepository.getBearerToken()
            )
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else {
                        it.data?.let { list ->
                            withContext(Main) {
                                tripModelLiveData.value = list
                            }
                        }
                    }
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister


    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister
    fun requestConfirmAndRejectTripRequestRegister(request: List<TripRequestRegisterStatusModel>) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = tripRepository.requestConfirmAndRejectTripRequestRegister(
                request,
                tokenRepository.getBearerToken()
            )
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        requestGetTripRequestRegister()
                } ?: run {
                    setError("اطلاعات خالی است")
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister


    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
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