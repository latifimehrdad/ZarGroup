package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zar.core.enums.EnumApiError
import com.zar.core.models.ErrorApiModel
import com.zar.core.tools.api.checkResponseError
import com.zarholding.zar.model.notification.NotificationCategoryModel
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.repository.NotificationRepository
import com.zarholding.zar.utility.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import javax.inject.Inject
import java.time.LocalDateTime
import java.time.Duration

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private var job: Job? = null
    private var rawListNotification: MutableList<NotificationSignalrModel>? = null
    private var categoryNotification = mutableListOf<NotificationCategoryModel>()
    val errorLiveDate = SingleLiveEvent<ErrorApiModel>()
    val readLiveData = SingleLiveEvent<Boolean>()
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




    fun getNotification() {
        if (rawListNotification.isNullOrEmpty())
            requestGetNotification()
        else {
            CoroutineScope(IO).launch {
                initListNotification()
            }
        }
    }



    //---------------------------------------------------------------------------------------------- requestGetNotification
    private fun requestGetNotification() {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = notificationRepository.requestGetNotification()
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    rawListNotification = it.data.toMutableList()
                    initListNotification()
                }
            } else
                setError(response)
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetNotification




    //---------------------------------------------------------------------------------------------- requestReadNotification
    fun requestReadNotification(ids : List<Int>) {
        job = CoroutineScope(IO + exceptionHandler()).launch {
            val response = notificationRepository.requestReadNotification(ids)
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    withContext(Main) {
                        readLiveData.value = it.data
                    }
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestReadNotification



    //---------------------------------------------------------------------------------------------- initListNotification
    private suspend fun initListNotification() {
        rawListNotification?.let { list ->
            setTodayNotification()
            setYesterdayNotification()
            categoryNotification.add(
                NotificationCategoryModel(
                    "همه",
                    null,
                    list.toMutableList()
                )
            )
            withContext(Main) {
                notificationResponseLiveData.value = categoryNotification
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initListNotification


    //---------------------------------------------------------------------------------------------- addNotification
    fun addNotification(item : NotificationSignalrModel) {
        rawListNotification?.add(0, item)
        categoryNotification.removeAll(categoryNotification)
        CoroutineScope(IO).launch {
            initListNotification()
        }
    }
    //---------------------------------------------------------------------------------------------- addNotification


    //---------------------------------------------------------------------------------------------- setTodayNotification
    private fun setTodayNotification() {
        val now = LocalDateTime.now()
        val todayDate = LocalDateTime.of(
            now.year,
            now.month,
            now.dayOfMonth,
            0, 0, 0
        )
        val todayNotification = rawListNotification!!.filter { item ->
            val strDate = item.lastUpdate?.substring(0, 11) + "00:00:00"
            val date = LocalDateTime.parse(strDate)
            val dayBetween = Duration.between(todayDate, date).toDays()
            dayBetween == 0L
        }
        if (todayNotification.isNotEmpty())
            categoryNotification.add(
                NotificationCategoryModel(
                    "امروز",
                    todayDate,
                    todayNotification.toMutableList()
                )
            )
    }
    //---------------------------------------------------------------------------------------------- setTodayNotification


    //---------------------------------------------------------------------------------------------- setYesterdayNotification
    private fun setYesterdayNotification() {
        val now = LocalDateTime.now().minusDays(1)
        val yesterdayDate = LocalDateTime.of(
            now.year,
            now.month,
            now.dayOfMonth,
            0, 0, 0
        )
        val yesterday = rawListNotification!!.filter { item ->
            val strDate = item.lastUpdate?.substring(0, 11) + "00:00:00"
            val date = LocalDateTime.parse(strDate)
            val dayBetween = Duration.between(yesterdayDate, date).toDays()
            dayBetween == 0L
        }
        if (yesterday.isNotEmpty())
            categoryNotification.add(
                NotificationCategoryModel(
                    "دیروز",
                    yesterdayDate,
                    yesterday.toMutableList()
                )
            )
    }
    //---------------------------------------------------------------------------------------------- setYesterdayNotification


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