package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.trip.TripRequestRegisterModel
import zar.databinding.ItemAdminBusBinding

/**
 * Created by m-latifi on 11/17/2022.
 */

class AdminBusItemHolder(
    private val binding: ItemAdminBusBinding,
    private val choose: Choose
) :
    RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Choose
    interface Choose {
        fun choose()
    }
    //---------------------------------------------------------------------------------------------- Choose


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: TripRequestRegisterModel) {
        binding.item = item
        binding.checkboxChoose.setOnClickListener { choose.choose() }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind
}