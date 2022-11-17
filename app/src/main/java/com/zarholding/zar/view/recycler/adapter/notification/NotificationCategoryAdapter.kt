package com.zarholding.zar.view.recycler.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.notification.NotificationCategoryModel
import com.zarholding.zar.view.recycler.holder.notification.NotificationCategoryHolder
import zar.databinding.ItemNotificationCategoryBinding

/**
 * Created by m-latifi on 11/16/2022.
 */

class NotificationCategoryAdapter(
    private val listOfCategories: List<NotificationCategoryModel>
) : RecyclerView.Adapter<NotificationCategoryHolder>() {

    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationCategoryHolder {

        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)

        return NotificationCategoryHolder(ItemNotificationCategoryBinding
            .inflate(layoutInflater!!, parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onBindViewHolder(holder: NotificationCategoryHolder, position: Int) {
        holder.bind(listOfCategories[position])
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = listOfCategories.size
    //---------------------------------------------------------------------------------------------- getItemCount


}