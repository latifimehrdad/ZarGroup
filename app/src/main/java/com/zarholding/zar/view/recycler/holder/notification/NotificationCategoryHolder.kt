package com.zarholding.zar.view.recycler.holder.notification

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.notification.NotificationCategoryModel
import com.zarholding.zar.view.recycler.adapter.notification.NotificationAdapter
import zar.databinding.ItemNotificationCategoryBinding

/**
 * Created by m-latifi on 11/17/2022.
 */

class NotificationCategoryHolder(
    private val binding: ItemNotificationCategoryBinding,
    private val click: NotificationItemHolder.Click) :
    RecyclerView.ViewHolder(binding.root) {


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: NotificationCategoryModel, categoryPosition : Int) {
        binding.item = item
        val adapter = NotificationAdapter(item.notifications, categoryPosition, click)
        binding.recyclerViewNotification.adapter = adapter
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind
}