package com.zarholding.zar.utility.signalr

import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel


/**
 * Created by m-latifi on 11/21/2022.
 */

interface RemoteSignalREmitter {

    fun onConnectToSignalR(){}
    fun onErrorConnectToSignalR(){}
    fun onReConnectToSignalR(){}
    fun onGetPoint(lat : String, lng : String){}
    fun onPreviousStationReached(message : String){}
    fun onReceiveMessage(user : String, message : NotificationSignalrModel){}
}