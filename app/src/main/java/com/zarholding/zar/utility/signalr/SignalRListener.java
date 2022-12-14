package com.zarholding.zar.utility.signalr;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by m-latifi on 11/11/2022.
 */

public class SignalRListener {

    //    private static SignalRListener instance;
    private final HubConnection hubConnection;
    private final RemoteSignalREmitter remoteSignalREmitter;
    private Thread thread;

    //---------------------------------------------------------------------------------------------- SignalRListener
    public SignalRListener(RemoteSignalREmitter remoteSignalREmitter, String token) {
        this.remoteSignalREmitter = remoteSignalREmitter;
        String url = "http://5.160.125.98:1364/realtimenotification?access_token=" + token;
        hubConnection = HubConnectionBuilder
                .create(url)
                .build();

        hubConnection.on(
                "ReceiveDriverLocation",
                (serviceId, driverId, lat, lng) -> this.remoteSignalREmitter.onGetPoint(lat, lng),
                String.class, Integer.class, String.class, String.class);


        hubConnection.on("PreviousStationReached",
                this.remoteSignalREmitter::onPreviousStationReached, String.class);


        hubConnection.on("ReceiveMessage",
                this.remoteSignalREmitter::onReceiveMessage,
                String.class ,
                NotificationSignalrModel.class);

    }
    //---------------------------------------------------------------------------------------------- SignalRListener


    //---------------------------------------------------------------------------------------------- getInstance
    public static SignalRListener getInstance(RemoteSignalREmitter remote, String token) {
        return new SignalRListener(remote, token);
    }
    //---------------------------------------------------------------------------------------------- getInstance


    //---------------------------------------------------------------------------------------------- startConnection
    public void startConnection() {
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                        hubConnection
                                .start()
                                .doOnError(throwable -> remoteSignalREmitter.onErrorConnectToSignalR())
                                .doOnComplete(remoteSignalREmitter::onConnectToSignalR)
                                .blockingAwait();

                    hubConnection.onClosed(exception -> {
                        remoteSignalREmitter.onReConnectToSignalR();
                        interruptThread();
                    });
                } catch (Exception ignored) {
                    remoteSignalREmitter.onReConnectToSignalR();
                    interruptThread();
                }
            }
        };
        thread.start();
    }
    //---------------------------------------------------------------------------------------------- startConnection


    //---------------------------------------------------------------------------------------------- stopConnection
    public void stopConnection() {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.stop();
        interruptThread();
    }
    //---------------------------------------------------------------------------------------------- stopConnection


    //---------------------------------------------------------------------------------------------- isConnection
    public boolean isConnection() {
        return hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
    }
    //---------------------------------------------------------------------------------------------- isConnection


    //---------------------------------------------------------------------------------------------- registerToGroupForService
    public void registerToGroupForService(Integer tripId, Integer stationId) {
        List<String> groups = new ArrayList<>();
        String trip = "trip" + tripId;
        String station = trip + "station" + stationId;
        groups.add(trip);
        groups.add(station);
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("RegisterToGroup", groups);
    }
    //---------------------------------------------------------------------------------------------- registerToGroupForService


    //---------------------------------------------------------------------------------------------- registerToGroupForNotification
    public void registerToGroupForNotification(String userId) {
        List<String> groups = new ArrayList<>();
        groups.add(userId);
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("RegisterToGroup", groups);
    }
    //---------------------------------------------------------------------------------------------- registerToGroupForNotification


    //---------------------------------------------------------------------------------------------- notificationReceived
    public void notificationReceived(Integer id) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("NotificationReceived", id);
    }
    //---------------------------------------------------------------------------------------------- notificationReceived


    //---------------------------------------------------------------------------------------------- interruptThread
    public void interruptThread() {
        if (thread != null)
            thread.interrupt();
    }
    //---------------------------------------------------------------------------------------------- interruptThread


}
