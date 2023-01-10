package com.zarholding.zar.background

import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.zarholding.zar.model.request.NotificationUnreadCountRequestModel
import com.zarholding.zar.repository.NotificationRepository
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.CompanionValues.Companion.NOTIFICATION_WORKER_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import zar.R
import javax.inject.Inject

/**
 * Create by Mehrdad on 1/9/2023
 */

@HiltWorker
class FetchDataWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override suspend fun doWork(): Result {
        Log.e("meri", "doWork")
        if (!tokenRepository.getToken().isNullOrEmpty())
            requestGetNotificationUnreadCount()
        else
            showNotification("worker is running...")
        val outputData = Data.Builder()
            .putString("NOTIFICATION_DATA", "Hi Da")
            .build()
        return Result.success(outputData)
    }


    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount
    private fun requestGetNotificationUnreadCount() {
        CoroutineScope(IO).launch {
            val request = NotificationUnreadCountRequestModel(
                0,
                "MIM",
                sharedPreferences.getInt(CompanionValues.notificationLast, 0)
            )
            Log.e("meri", "broadcast request = ${request.lastId}")
            val response = notificationRepository.requestGetNotificationUnreadCount(request)
            if (response?.isSuccessful == true)
                response.body()?.let {
                    it.data?.let { count ->
                        if (count.unreadCount > 0) {
                            sharedPreferences
                                .edit()
                                .putInt(CompanionValues.notificationLast, count.lastId)
                                .apply()
                            showNotification("Unread : ${count.unreadCount}")
                        } else
                            showNotification("worker is running...")
                    }
                }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount


    //---------------------------------------------------------------------------------------------- showNotification
    private fun showNotification(sender: String) {
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val notifyManager = NotificationManagerCompat.from(appContext)
        val notificationBuilder = NotificationCompat
            .Builder(appContext, CompanionValues.channelId)
        val notification = notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSubText("Work Manager")
            .setContentText("start work manager")
            .setContentTitle(sender)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVibrate(vibrate)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        notifyManager.notify(NOTIFICATION_WORKER_ID, notification.build())
    }
    //---------------------------------------------------------------------------------------------- showNotification


}