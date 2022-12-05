package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.recycler.holder.DashboardItemHolder
import zar.databinding.ItemAppDashboardBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class DashboardAppAdapter(
    private val apps: MutableList<AppModel>,
    private val click: DashboardItemHolder.Click
) : RecyclerView.Adapter<DashboardItemHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return DashboardItemHolder(
            ItemAppDashboardBinding.inflate(layoutInflater!!, parent, false),
            click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: DashboardItemHolder, position: Int) {
        holder.bind(apps[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = apps.size
    //---------------------------------------------------------------------------------------------- getItemCount

}