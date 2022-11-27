package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.view.recycler.holder.MyServiceHolder
import zar.databinding.ItemMyServiceBinding

/**
 * Created by m-latifi on 11/20/2022.
 */

class MyServiceAdapter(
    private val tripList: List<TripModel>,
    private val click: MyServiceHolder.Click
) :
    RecyclerView.Adapter<MyServiceHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyServiceHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return MyServiceHolder(
            ItemMyServiceBinding
                .inflate(layoutInflater!!, parent, false)
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: MyServiceHolder, position: Int) {
        holder.bind(tripList[position], click)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = tripList.size
    //---------------------------------------------------------------------------------------------- getItemCount


}