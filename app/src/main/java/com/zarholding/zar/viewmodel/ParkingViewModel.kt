package com.zarholding.zar.viewmodel

import androidx.lifecycle.MutableLiveData
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.request.CarPlaqueEditRequest
import com.zarholding.zar.model.request.UserInfoRequest
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.SingleLiveEvent
import com.zarholding.zar.view.extension.toCarPlaque
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import javax.inject.Inject

@HiltViewModel
class ParkingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {

    var userInfoEntity: UserInfoEntity? = null
    val successLiveData = SingleLiveEvent<UserInfoEntity>()
    val plaqueNumber1 = MutableLiveData<String>()
    val plaqueNumber2 = MutableLiveData<String>()
    val plaqueCity = MutableLiveData<String>()
    val plaqueAlphabet = MutableLiveData<String>()
    val carModel = MutableLiveData<String>()


    //---------------------------------------------------------------------------------------------- initUserCarPlaque
    fun initUserCarPlaque() {
        getUserInfo()?.let {
            carModel.value = it.carModel ?: ""
            plaqueNumber1.value = it.pelak.toCarPlaque("number1")
            plaqueNumber2.value = it.pelak.toCarPlaque("number2")
            plaqueCity.value = it.pelak.toCarPlaque("city")
            plaqueAlphabet.value = it.pelak.toCarPlaque("alphabet")
        }
    }
    //---------------------------------------------------------------------------------------------- initUserCarPlaque


    //---------------------------------------------------------------------------------------------- requestChangeCarPlaque
    fun requestChangeCarPlaque() {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            if (checkPlaqueValidation())
                setMessage(resourcesProvider.getString(R.string.plaqueInformationIsEmpty))
            else {
                val user = getUserInfo()
                if (user == null)
                    setMessage(resourcesProvider.getString(R.string.dataSendingIsEmpty))
                else {
                    val plaque = plaqueNumber1.value.persianNumberToEnglishNumber() +
                            plaqueAlphabet.value +
                            plaqueNumber2.value.persianNumberToEnglishNumber() +
                            plaqueCity.value.persianNumberToEnglishNumber()
                    val model = CarPlaqueEditRequest(carModel.value, plaque)
                    val response = userRepository.requestChangeCarPlaque(model)
                    if (response?.isSuccessful == true) {
                        val userInfo = response.body()
                        userInfo?.let {
                            if (it.hasError)
                                setMessage(it.message)
                            else {
                                user.pelak = plaque
                                user.carModel = carModel.value
                                userRepository.insertUserInfo(user)
                                withContext(Main) {
                                    successLiveData.value = userInfoEntity
                                }
                            }
                        } ?: run {
                            setMessage(resourcesProvider.getString(R.string.dataReceivedIsEmpty))
                        }
                    } else
                        setMessage(response)
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestChangeCarPlaque


    //---------------------------------------------------------------------------------------------- requestGetUserInfo
    fun requestGetUserInfo() {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            if (checkPlaqueValidation())
                setMessage(resourcesProvider.getString(R.string.plaqueInformationIsEmpty))
            else {
                val plaque = plaqueNumber1.value.persianNumberToEnglishNumber() +
                        plaqueAlphabet.value +
                        plaqueNumber2.value.persianNumberToEnglishNumber() +
                        plaqueCity.value.persianNumberToEnglishNumber()
                val request = UserInfoRequest(null, plaque)
                val response = userRepository.requestGetUserInfo(request)
                if (response?.isSuccessful == true) {
                    val userInfo = response.body()
                    userInfo?.let {
                        if (it.hasError)
                            setMessage(it.message)
                        else {
                            it.data?.let { temp ->
                                userInfoEntity = temp
                                withContext(Main) {
                                    successLiveData.value = userInfoEntity
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
    }
    //---------------------------------------------------------------------------------------------- requestGetUserInfo


    //---------------------------------------------------------------------------------------------- checkPlaqueValidation
    private fun checkPlaqueValidation(): Boolean =
        plaqueNumber1.value?.length != 2 || plaqueNumber2.value?.length != 3 ||
                plaqueCity.value?.length != 2 || plaqueAlphabet.value.isNullOrEmpty()
    //---------------------------------------------------------------------------------------------- checkPlaqueValidation


    //---------------------------------------------------------------------------------------------- getBearerToken
    fun getBearerToken() = userRepository.getBearerToken()
    //---------------------------------------------------------------------------------------------- getBearerToken


    //---------------------------------------------------------------------------------------------- getAlphabet
    fun getAlphabet() = listOf(
        "الف",
        "ب",
        "پ",
        "ت",
        "ث",
        "ج",
        "چ",
        "ح",
        "خ",
        "د",
        "ذ",
        "ر",
        "ز",
        "ژ",
        "س",
        "ش",
        "ص",
        "ض",
        "ط",
        "ظ",
        "ع",
        "غ",
        "ف",
        "ق",
        "ک",
        "گ",
        "ل",
        "م",
        "ن",
        "و",
        "ه",
        "ی"
    )
    //---------------------------------------------------------------------------------------------- getAlphabet


    //---------------------------------------------------------------------------------------------- getUserInfo
    private fun getUserInfo() = userRepository.getUser()
    //---------------------------------------------------------------------------------------------- getUserInfo


}