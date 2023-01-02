package com.zarholding.zar.view.recycler.holder.notification

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import zar.databinding.ItemNotificationBinding

/**
 * Created by m-latifi on 11/17/2022.
 */

class NotificationItemHolder(private val binding: ItemNotificationBinding) :
    RecyclerView.ViewHolder(binding.root)
{

    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: NotificationSignalrModel)
    {
        binding.item = item
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind
}