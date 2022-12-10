package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import zar.databinding.ItemPassengerBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class PassengerAdapter(
    private val apps: MutableList<AppModel>,
    private val click: PassengerItemHolder.Click
) : RecyclerView.Adapter<PassengerItemHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return PassengerItemHolder(
            ItemPassengerBinding.inflate(layoutInflater!!, parent, false),
            click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: PassengerItemHolder, position: Int) {
        holder.bind(apps[position], position == apps.size-1)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = apps.size
    //---------------------------------------------------------------------------------------------- getItemCount

}