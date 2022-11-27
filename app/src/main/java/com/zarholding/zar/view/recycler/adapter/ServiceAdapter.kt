package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.view.recycler.holder.ServiceHolder
import zar.databinding.ItemServiceBinding

/**
 * Created by m-latifi on 11/20/2022.
 */

class ServiceAdapter(
    private val tripList: List<TripModel>,
    private val click: ServiceHolder.Click
) :
    RecyclerView.Adapter<ServiceHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return ServiceHolder(ItemServiceBinding.inflate(layoutInflater!!, parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: ServiceHolder, position: Int) {
        holder.bind(tripList[position], click)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = tripList.size
    //---------------------------------------------------------------------------------------------- getItemCount


}