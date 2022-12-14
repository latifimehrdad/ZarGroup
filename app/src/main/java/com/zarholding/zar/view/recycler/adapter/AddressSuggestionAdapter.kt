package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.view.recycler.holder.AddressSuggestionItemHolder
import zar.databinding.ItemAddressSuggestionBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class AddressSuggestionAdapter(
    private val items: MutableList<AddressSuggestionModel>,
    private val click: AddressSuggestionItemHolder.Click
) : RecyclerView.Adapter<AddressSuggestionItemHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressSuggestionItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return AddressSuggestionItemHolder(
            ItemAddressSuggestionBinding.inflate(layoutInflater!!, parent, false), click)
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: AddressSuggestionItemHolder, position: Int) {
        holder.bind(items[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = items.size
    //---------------------------------------------------------------------------------------------- getItemCount



    //---------------------------------------------------------------------------------------------- addAddress
    fun addAddress(list : List<AddressSuggestionModel>) {
        val olsSize = items.size
        items.addAll(list)
        notifyItemRangeChanged(olsSize-1, items.size)
    }
    //---------------------------------------------------------------------------------------------- addAddress



    //---------------------------------------------------------------------------------------------- getList
    fun getList() = items
    //---------------------------------------------------------------------------------------------- getList

}