package com.zarholding.zar.view.recycler.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.view.recycler.holder.notification.NotificationItemHolder
import zar.databinding.ItemNotificationBinding

/**
 * Created by m-latifi on 11/16/2022.
 */

class NotificationAdapter(
    private var notificationModels: List<NotificationSignalrModel>,
    private var categoryPosition : Int,
    private val click: NotificationItemHolder.Click
) :
    RecyclerView.Adapter<NotificationItemHolder>() {

    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemHolder {

        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)

        return NotificationItemHolder(ItemNotificationBinding
            .inflate(layoutInflater!!, parent, false),categoryPosition, click)
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: NotificationItemHolder, position: Int) {
        holder.bind(notificationModels[position], position)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun getItemCount() = notificationModels.size
    //---------------------------------------------------------------------------------------------- onBindViewHolder

}