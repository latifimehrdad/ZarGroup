package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import zar.databinding.ItemAppBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class AppItemHolder(private val binding : ItemAppBinding) : RecyclerView.ViewHolder(binding.root)
{
    //---------------------------------------------------------------------------------------------- bind
    fun bind(item : AppModel, position : Int) {
        binding.item = item
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind
}