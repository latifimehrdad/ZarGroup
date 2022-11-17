package com.zarholding.zar.view.recycler.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.notification.NotificationModel
import com.zarholding.zar.view.recycler.holder.notification.NotificationItemHolder
import zar.databinding.ItemNotificationBinding

/**
 * Created by m-latifi on 11/16/2022.
 */

class NotificationAdapter(private var notificationModels: List<NotificationModel>) :
    RecyclerView.Adapter<NotificationItemHolder>() {

    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemHolder {

        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)

        return NotificationItemHolder(ItemNotificationBinding
            .inflate(layoutInflater!!, parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: NotificationItemHolder, position: Int) {
        holder.bind(notificationModels[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun getItemCount() = notificationModels.size
    //---------------------------------------------------------------------------------------------- onBindViewHolder

}