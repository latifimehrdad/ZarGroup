package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.view.recycler.holder.MYTaxiHolder
import zar.databinding.ItemMyTaxiBinding

class MyTaxiAdapter(
    private val items: List<AdminTaxiRequestModel>,
) : RecyclerView.Adapter<MYTaxiHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MYTaxiHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return MYTaxiHolder(ItemMyTaxiBinding.inflate(layoutInflater!!, parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: MYTaxiHolder, position: Int) {
        holder.bind(items[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = items.size
    //---------------------------------------------------------------------------------------------- getItemCount


}