package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.model.notification_signalr.NotificationCategoryModel
import com.zarholding.zar.repository.NotificationRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject
import java.time.LocalDateTime
import java.time.Duration
import java.time.LocalDate

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        var notificationCount: Int = 0
    }

    private var job: Job? = null
    private var rawListNotification: List<NotificationSignalrModel>? = null
    private var categoryNotification = mutableListOf<NotificationCategoryModel>()
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    val notificationResponseLiveData = SingleLiveEvent<MutableList<NotificationCategoryModel>>()


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


    //---------------------------------------------------------------------------------------------- requestGetNotification
    fun requestGetNotification() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = notificationRepository.requestGetNotification()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    rawListNotification = it.data
                    initListNotification()
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetNotification


    //---------------------------------------------------------------------------------------------- deleteAllData
    fun deleteAllData() {
        userRepository.deleteUser()
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, null)
            .putString(CompanionValues.userName, null)
            .putString(CompanionValues.passcode, null)
            .putInt(CompanionValues.notificationLastId, 0)
            .apply()
    }
    //---------------------------------------------------------------------------------------------- deleteAllData


    //---------------------------------------------------------------------------------------------- setLastNotificationIdToZero
    fun setLastNotificationIdToZero() {
        sharedPreferences
            .edit()
            .putInt(CompanionValues.notificationLastId, 0)
            .apply()
    }
    //---------------------------------------------------------------------------------------------- setLastNotificationIdToZero


    //---------------------------------------------------------------------------------------------- initListNotification
    private suspend fun initListNotification() {
        rawListNotification?.let { list ->
            var now = LocalDateTime.now()
            val todayDate = LocalDateTime.of(
                now.year,
                now.month,
                now.dayOfMonth,
                0,0,0
            )
            val todayNotification = list.filter { item ->
                val strDate = item.lastUpdate?.substring(0,11) + "00:00:00"
                val date = LocalDateTime.parse(strDate)
                val dayBetween = Duration.between(todayDate, date).toDays()
                dayBetween == 0L
            }
            categoryNotification.add(
                NotificationCategoryModel(
                    "امروز",
                    todayDate,
                    todayNotification
                )
            )

            now = LocalDateTime.now().minusDays(1)
            val yesterdayDate = LocalDateTime.of(
                now.year,
                now.month,
                now.dayOfMonth,
                0,0,0
            )
            val yesterday = list.filter { item ->
                val strDate = item.lastUpdate?.substring(0,11) + "00:00:00"
                val date = LocalDateTime.parse(strDate)
                val dayBetween = Duration.between(yesterdayDate, date).toDays()
                dayBetween == 0L
            }

            categoryNotification.add(
                NotificationCategoryModel(
                    "دیروز",
                    yesterdayDate,
                    yesterday
                )
            )

            categoryNotification.add(
                NotificationCategoryModel(
                    "همه",
                    todayDate,
                    list
                )
            )

            withContext(Main) {
                notificationResponseLiveData.value = categoryNotification
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initListNotification


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