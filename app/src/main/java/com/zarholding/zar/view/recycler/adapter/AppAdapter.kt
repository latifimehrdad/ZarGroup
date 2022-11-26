package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.recycler.holder.AppItemHolder
import zar.databinding.ItemAppBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class AppAdapter(
    private val apps: MutableList<AppModel>,
    private val click: AppItemHolder.Click
) : RecyclerView.Adapter<AppItemHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return AppItemHolder(
            ItemAppBinding.inflate(layoutInflater!!, parent, false),
            click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: AppItemHolder, position: Int) {
        holder.bind(apps[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = apps.size
    //---------------------------------------------------------------------------------------------- getItemCount

}