package com.zarholding.zar.background

import android.app.Notification
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.signalr.RemoteSignalREmitter
import com.zarholding.zar.utility.signalr.SignalRListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zar.R
import javax.inject.Inject

@AndroidEntryPoint
class SignalrNotificationBackgroundService : LifecycleService(), RemoteSignalREmitter {

    @Inject
    lateinit var tokenRepository: TokenRepository
    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var signalRListener: SignalRListener

    //---------------------------------------------------------------------------------------------- onStartCommand
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        lifecycleScope.launch(Dispatchers.Default) {
/*            signalRListener = SignalRListener.getInstance(
                this@SignalrNotificationBackgroundService, tokenRepository.getToken()
            )
            startSignalR()*/
        }
        return START_STICKY
    }
    //---------------------------------------------------------------------------------------------- onStartCommand


    //---------------------------------------------------------------------------------------------- startSignalR
    private fun startSignalR() {
        if (!signalRListener.isConnection)
            signalRListener.startConnection()
    }
    //---------------------------------------------------------------------------------------------- startSignalR


    //---------------------------------------------------------------------------------------------- onConnectToSignalR
    override fun onConnectToSignalR() {
        super.onConnectToSignalR()
        userRepository.getUser()?.let {
            signalRListener.registerToGroupForNotification(it.id.toString())
            Log.d("meri", "registerToGroupForNotification ${it.id}")
        }
        Log.d("meri", "onConnectToSignalR")
    }
    //---------------------------------------------------------------------------------------------- onConnectToSignalR


    //---------------------------------------------------------------------------------------------- onErrorConnectToSignalR
    override fun onErrorConnectToSignalR() {
        super.onErrorConnectToSignalR()
        Log.d("meri", "onErrorConnectToSignalR")
    }
    //---------------------------------------------------------------------------------------------- onErrorConnectToSignalR


    //---------------------------------------------------------------------------------------------- onReConnectToSignalR
    override fun onReConnectToSignalR() {
        super.onReConnectToSignalR()
        Log.d("meri", "onReConnectToSignalR")
    }
    //---------------------------------------------------------------------------------------------- onReConnectToSignalR


    //---------------------------------------------------------------------------------------------- onReceiveMessage
    override fun onReceiveMessage(user: String, message: NotificationSignalrModel) {
        super.onReceiveMessage(user, message)
        Log.d("meri", "onReceiveMessage")
        showNotification(message)
    }
    //---------------------------------------------------------------------------------------------- onReceiveMessage


    //---------------------------------------------------------------------------------------------- showNotification
    private fun showNotification(message: NotificationSignalrModel) {
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifyManager = NotificationManagerCompat.from(this)
        val notificationBuilder = NotificationCompat
            .Builder(this, CompanionValues.channelId)
        val notification = notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSubText(message.senderName)
            .setContentTitle(message.message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVibrate(vibrate)
            .setSound(alarmSound)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        notifyManager.notify(7126, notification.build())
    }
    //---------------------------------------------------------------------------------------------- showNotification


}