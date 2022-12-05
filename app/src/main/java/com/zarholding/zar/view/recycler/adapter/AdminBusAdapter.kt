package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.trip.TripRequestRegisterModel
import com.zarholding.zar.model.response.trip.TripRequestRegisterResponseModel
import com.zarholding.zar.view.recycler.holder.AdminBusItemHolder
import zar.databinding.ItemAdminBusBinding

/**
 * Created by m-latifi on 11/16/2022.
 */

class AdminBusAdapter(
    private var items: List<TripRequestRegisterModel>,
    private val choose: AdminBusItemHolder.Choose
) :
    RecyclerView.Adapter<AdminBusItemHolder>() {

    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBusItemHolder {

        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)

        return AdminBusItemHolder(
            ItemAdminBusBinding
            .inflate(layoutInflater!!, parent, false), choose)
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: AdminBusItemHolder, position: Int) {
        holder.bind(items[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun getItemCount() = items.size
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItems
    fun getItems() = items
    //---------------------------------------------------------------------------------------------- getItems


}