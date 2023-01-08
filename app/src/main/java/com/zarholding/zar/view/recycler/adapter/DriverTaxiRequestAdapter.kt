package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumPersonnelType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.view.recycler.holder.DriverTaxiRequestHolder
import zar.databinding.ItemDriverTaxiRequestBinding

class DriverTaxiRequestAdapter(
    private val items: MutableList<AdminTaxiRequestModel>,
    private val click: DriverTaxiRequestHolder.Click
) : RecyclerView.Adapter<DriverTaxiRequestHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverTaxiRequestHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return DriverTaxiRequestHolder(
            ItemDriverTaxiRequestBinding
                .inflate(layoutInflater!!, parent, false), click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: DriverTaxiRequestHolder, position: Int) {
        holder.bind(items[position], position)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = items.size
    //---------------------------------------------------------------------------------------------- getItemCount


    //---------------------------------------------------------------------------------------------- addRequest
    fun addRequest(list: List<AdminTaxiRequestModel>) {
        val oldSize = items.size
        items.addAll(list)
        notifyItemRangeChanged(oldSize-1, items.size)
    }
    //---------------------------------------------------------------------------------------------- addRequest


    //---------------------------------------------------------------------------------------------- getList
    fun getList() = items
    //---------------------------------------------------------------------------------------------- getList

}