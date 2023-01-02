package com.zarholding.zar.background

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zarholding.zar.model.request.NotificationUnreadCountRequestModel
import com.zarholding.zar.repository.NotificationRepository
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.utility.CompanionValues
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import zar.R
import javax.inject.Inject

@AndroidEntryPoint
class ZarBackgroundBroadcast : HiltBroadcastReceiver() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var context: Context


    //---------------------------------------------------------------------------------------------- onReceive
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        this.context = context
        requestGetNotificationUnreadCount()
    }
    //---------------------------------------------------------------------------------------------- onReceive


    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount
    private fun requestGetNotificationUnreadCount() {
        CoroutineScope(IO).launch {
            val request = NotificationUnreadCountRequestModel(
                0,
                "MIM",
                sharedPreferences.getInt(CompanionValues.notificationLastId,0)
            )
            Log.e("meri", "broadcast request = ${request.lastId}")
            val response = notificationRepository.requestGetNotificationUnreadCount(request)
            if (response?.isSuccessful == true)
                response.body()?.let {
                    it.data?.let { count ->
                        if (count.unreadCount > 0) {
                            sharedPreferences
                                .edit()
                                .putInt(CompanionValues.notificationLastId, count.lastId)
                                .apply()
                            showNotificationPreviousStationReached(context)
                        }
                    }
                }
        }
    }
    //---------------------------------------------------------------------------------------------- requestGetNotificationUnreadCount


    //---------------------------------------------------------------------------------------------- showNotificationPreviousStationReached
    private fun showNotificationPreviousStationReached(context: Context) {
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifyManager = NotificationManagerCompat.from(context)
        val notificationBuilder = NotificationCompat
            .Builder(context, CompanionValues.channelId)
        val notification = notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(context.getString(R.string.youHaveUnreadMessage))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVibrate(vibrate)
            .setSound(alarmSound)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        notifyManager.notify(7126, notification.build())
    }
    //---------------------------------------------------------------------------------------------- showNotificationPreviousStationReached


}