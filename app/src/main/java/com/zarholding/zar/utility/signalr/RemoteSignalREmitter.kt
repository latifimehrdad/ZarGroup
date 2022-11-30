package com.zarholding.zar.utility.signalr

/**
 * Created by m-latifi on 11/21/2022.
 */

interface RemoteSignalREmitter {

    fun onConnectToSignalR()
    fun onErrorConnectToSignalR()
    fun onReConnectToSignalR()
    fun onGetPoint(lat : String, lng : String)
}