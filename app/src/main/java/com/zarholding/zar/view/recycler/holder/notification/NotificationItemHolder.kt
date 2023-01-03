package com.zarholding.zar.view.recycler.holder.notification

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import zar.databinding.ItemNotificationBinding

/**
 * Created by m-latifi on 11/17/2022.
 */

class NotificationItemHolder(
    private val binding: ItemNotificationBinding,
    private var categoryPosition : Int,
    private val click: Click
) :
    RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun showDetail(categoryPosition : Int, notificationPosition : Int)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: NotificationSignalrModel, notificationPosition : Int) {
        binding.item = item
        binding.root.setOnClickListener { click.showDetail(categoryPosition, notificationPosition) }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind
}