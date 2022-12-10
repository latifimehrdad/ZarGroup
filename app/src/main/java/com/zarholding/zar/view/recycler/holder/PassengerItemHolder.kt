package com.zarholding.zar.view.recycler.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import zar.databinding.ItemPassengerBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class PassengerItemHolder(
    private val binding: ItemPassengerBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun appClick(action: Int)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AppModel, last : Boolean) {
        binding.item = item
        if (last)
            binding.linearLayoutAdd.visibility = View.VISIBLE
        else
            binding.linearLayoutAdd.visibility = View.GONE
        binding.cardViewAdd.setOnClickListener {
            binding.powerSpinnerPassenger.visibility = View.VISIBLE
        }

        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}