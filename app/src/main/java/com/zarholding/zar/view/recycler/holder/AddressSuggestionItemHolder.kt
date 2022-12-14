package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import zar.databinding.ItemAddressSuggestionBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class AddressSuggestionItemHolder(
    private val binding: ItemAddressSuggestionBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun selectItem(item : AddressSuggestionModel)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AddressSuggestionModel) {
        binding.item = item
        binding.root.setOnClickListener { click.selectItem(item) }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}