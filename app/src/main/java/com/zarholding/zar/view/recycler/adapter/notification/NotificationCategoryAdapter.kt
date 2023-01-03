package com.zarholding.zar.view.recycler.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.notification.NotificationCategoryModel
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.view.recycler.holder.notification.NotificationCategoryHolder
import com.zarholding.zar.view.recycler.holder.notification.NotificationItemHolder
import zar.databinding.ItemNotificationCategoryBinding

/**
 * Created by m-latifi on 11/16/2022.
 */

class NotificationCategoryAdapter(
    private val listOfCategories: List<NotificationCategoryModel>,
    private val click: NotificationItemHolder.Click
) : RecyclerView.Adapter<NotificationCategoryHolder>() {

    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationCategoryHolder {

        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)

        return NotificationCategoryHolder(ItemNotificationCategoryBinding
            .inflate(layoutInflater!!, parent, false), click)
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onBindViewHolder(holder: NotificationCategoryHolder, position: Int) {
        holder.bind(listOfCategories[position], position)
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = listOfCategories.size
    //---------------------------------------------------------------------------------------------- getItemCount


    //---------------------------------------------------------------------------------------------- setReadNotification
    fun setReadNotification(categoryPosition : Int, notificationPosition : Int) {
        listOfCategories[categoryPosition].notifications[notificationPosition].isRead = true
        notifyItemRangeChanged(categoryPosition,1)
    }
    //---------------------------------------------------------------------------------------------- setReadNotification


    //---------------------------------------------------------------------------------------------- addNotification
    fun addNotification(item : NotificationSignalrModel) {
        listOfCategories[0].notifications.add(0, item)
        notifyItemRangeChanged(0,1)
    }
    //---------------------------------------------------------------------------------------------- addNotification


    //---------------------------------------------------------------------------------------------- getListOfCategories
    fun getListOfCategories() = listOfCategories
    //---------------------------------------------------------------------------------------------- getListOfCategories
}