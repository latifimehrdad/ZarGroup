package com.zarholding.zar.background

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.google.gson.Gson
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.signalr.RemoteSignalREmitter
import com.zarholding.zar.utility.signalr.SignalRListener
import com.zarholding.zar.view.extension.getMessageContent
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import javax.inject.Inject

@AndroidEntryPoint
class ZarNotificationService : LifecycleService(), RemoteSignalREmitter {


    @Inject
    lateinit var tokenRepository: TokenRepository
    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var signalRListener: SignalRListener


    //---------------------------------------------------------------------------------------------- onStartCommand
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        scheduleAlarm()
        initSignalR()
        return START_STICKY
    }
    //---------------------------------------------------------------------------------------------- onStartCommand



    //---------------------------------------------------------------------------------------------- initSignalR
    private fun initSignalR() {
        signalRListener = SignalRListener.getInstance(
            this@ZarNotificationService, tokenRepository.getToken()
        )
        startSignalR()
    }
    //---------------------------------------------------------------------------------------------- initSignalR



    //---------------------------------------------------------------------------------------------- scheduleAlarm
    private fun scheduleAlarm() {
        val intent = Intent(applicationContext, ZarBackgroundBroadcast::class.java)
        val pIntent = PendingIntent.getBroadcast(this, 123521,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val firstMillis = System.currentTimeMillis() // alarm is set right away
        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, firstMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent
        )
    }
    //---------------------------------------------------------------------------------------------- scheduleAlarm



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
    }
    //---------------------------------------------------------------------------------------------- onConnectToSignalR


    //---------------------------------------------------------------------------------------------- onErrorConnectToSignalR
    override fun onErrorConnectToSignalR() {
        super.onErrorConnectToSignalR()
        Log.e("meri", "onErrorConnectToSignalR")
    }
    //---------------------------------------------------------------------------------------------- onErrorConnectToSignalR


    //---------------------------------------------------------------------------------------------- onReConnectToSignalR
    override fun onReConnectToSignalR() {
        super.onReConnectToSignalR()
        Log.e("meri", "onReConnectToSignalR")
    }
    //---------------------------------------------------------------------------------------------- onReConnectToSignalR


    //---------------------------------------------------------------------------------------------- onReceiveMessage
    override fun onReceiveMessage(user: String, message: NotificationSignalrModel) {
        super.onReceiveMessage(user, message)
        signalRListener.notificationReceived(message.id)
        val gson = Gson()
        val item = gson.toJson(message)
        val intent = Intent("com.zarholding.zar.receive.message")
        intent.putExtra(CompanionValues.notificationLast, item)
        sendBroadcast(intent)
        showNotification(message)
    }
    //---------------------------------------------------------------------------------------------- onReceiveMessage


    //---------------------------------------------------------------------------------------------- showNotification
    private fun showNotification(message: NotificationSignalrModel) {
        if (message.message.isNullOrEmpty())
            return
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val notifyManager = NotificationManagerCompat.from(this)
        val notificationBuilder = NotificationCompat
            .Builder(this, CompanionValues.channelId)
        val notification = notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSubText(message.senderName)
            .setContentTitle(message.getMessageContent())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVibrate(vibrate)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        notifyManager.notify(7126, notification.build())
    }
    //---------------------------------------------------------------------------------------------- showNotification

}