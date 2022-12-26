package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.enum.EnumDriverType
import com.zarholding.zar.model.request.AssignDriverRequest
import com.zarholding.zar.model.response.driver.DriverModel
import com.zarholding.zar.repository.DriverRepository
import com.zarholding.zar.repository.TaxiRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val taxiRepository: TaxiRepository
) : ViewModel() {

    private var drivers : List<DriverModel>? = null
    private var job: Job? = null
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    val driversListLiveData = SingleLiveEvent<List<DriverModel>>()
    val assignDriverLiveData = SingleLiveEvent<String>()
    var selectedDriver : DriverModel? = null

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



    //---------------------------------------------------------------------------------------------- requestGetDriver
    fun requestGetDriver(type : EnumDriverType, companyCode : String?) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = driverRepository.requestGetDriver(type, companyCode)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    if (it.hasError)
                        setError(it.message)
                    else
                        it.data?.let { list ->
                            drivers = list
                            withContext(Main) {
                                driversListLiveData.value = list
                            }
                        } ?: run {
                            setError("اطلاعات خالی است")
                        }
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetDriver



    //---------------------------------------------------------------------------------------------- requestAssignDriverToRequest
    fun requestAssignDriverToRequest(requestId : String) {
        selectedDriver?.let { driver ->
            val request = AssignDriverRequest(requestId,driver.id.toString())
            job = CoroutineScope(IO + exceptionHandler()).launch {
                val response = taxiRepository.requestAssignDriverToRequest(request)
                if (response?.isSuccessful == true) {
                    response.body()?.let {
                        if (it.hasError)
                            setError(it.message)
                        else {
                            withContext(Main) {
                                assignDriverLiveData.value = it.message
                            }
                        }
                    } ?: run {
                        setError("اطلاعات خالی است")
                    }
                } else
                    setError(response)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestAssignDriverToRequest



    //---------------------------------------------------------------------------------------------- getDriverList
    fun getDriverList() = drivers
    //---------------------------------------------------------------------------------------------- getDriverList



    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler



    //---------------------------------------------------------------------------------------------- selectDriverByIndex
    fun selectDriverByIndex(index : Int) {
        drivers?.let {
            selectedDriver = it[index]
        }
    }
    //---------------------------------------------------------------------------------------------- selectDriverByIndex


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}