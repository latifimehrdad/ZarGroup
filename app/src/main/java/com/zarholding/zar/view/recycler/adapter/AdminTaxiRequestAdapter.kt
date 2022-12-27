package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumPersonnelType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.view.recycler.holder.AdminTaxiRequestHolder
import zar.databinding.ItemAdminTaxiRequestBinding

class AdminTaxiRequestAdapter(
    private val items: MutableList<AdminTaxiRequestModel>,
    private val click: AdminTaxiRequestHolder.Click,
    private val userType : EnumPersonnelType
) : RecyclerView.Adapter<AdminTaxiRequestHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminTaxiRequestHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return AdminTaxiRequestHolder(
            ItemAdminTaxiRequestBinding
                .inflate(layoutInflater!!, parent, false), click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: AdminTaxiRequestHolder, position: Int) {
        holder.bind(items[position], userType)
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


}