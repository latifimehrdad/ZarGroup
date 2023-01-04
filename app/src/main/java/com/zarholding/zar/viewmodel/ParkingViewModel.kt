package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.request.UserInfoRequest
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ParkingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel(){

    var userInfoEntity : UserInfoEntity? = null
    private var job: Job? = null
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    val successLiveData = SingleLiveEvent<UserInfoEntity>()
    var plaqueNumber1 : String? = null
    var plaqueNumber2 : String? = null
    var plaqueCity : String? = null
    var plaqueAlphabet : String? = null

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


    //---------------------------------------------------------------------------------------------- requestUserInfo
    fun requestUserInfo() {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            if (plaqueNumber1.isNullOrEmpty() || plaqueNumber2.isNullOrEmpty() ||
                plaqueCity.isNullOrEmpty() || plaqueAlphabet.isNullOrEmpty()
            ) {
                setError("اطلاعات پلاک را کامل وارد نمایید")
            } else {
                val plaque = plaqueNumber1 + plaqueAlphabet + plaqueNumber2 + plaqueCity
                val request = UserInfoRequest(null, plaque)
                val response = userRepository.requestUserInfo(request)
                if (response?.isSuccessful == true) {
                    val userInfo = response.body()
                    userInfo?.let {
                        if (it.hasError)
                            setError(it.message)
                        else {
                            it.data?.let { temp ->
                                userInfoEntity = temp
                                withContext(Main) {
                                    successLiveData.value = userInfoEntity
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
    }
    //---------------------------------------------------------------------------------------------- requestUserInfo



    //---------------------------------------------------------------------------------------------- exceptionHandler
    private fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            throwable.localizedMessage?.let { setError(it) }
        }
    }
    //---------------------------------------------------------------------------------------------- exceptionHandler



    //---------------------------------------------------------------------------------------------- getBearerToken
    fun getBearerToken() = tokenRepository.getBearerToken()
    //---------------------------------------------------------------------------------------------- getBearerToken


    //---------------------------------------------------------------------------------------------- getAlphabet
    fun getAlphabet() = listOf(
        "الف","ب","پ","ت","ث","ج","چ","ح","خ","د","ذ","ر","ز","ژ","س","ش","ص","ض","ط","ظ","ع","غ",
        "ف","ق","ک","گ","ل","م","ن","و","ه","ی"
    )
    //---------------------------------------------------------------------------------------------- getAlphabet


    //---------------------------------------------------------------------------------------------- onCleared
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- onCleared


}