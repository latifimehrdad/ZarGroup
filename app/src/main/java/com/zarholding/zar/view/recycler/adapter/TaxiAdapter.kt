package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.view.recycler.holder.TaxiHolder
import zar.databinding.ItemTaxiBinding

class TaxiAdapter(
    private val items: List<AdminTaxiRequestModel>,
    private val click: TaxiHolder.Click
) : RecyclerView.Adapter<TaxiHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxiHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return TaxiHolder(
            ItemTaxiBinding
                .inflate(layoutInflater!!, parent, false), click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: TaxiHolder, position: Int) {
        holder.bind(items[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = items.size
    //---------------------------------------------------------------------------------------------- getItemCount


}