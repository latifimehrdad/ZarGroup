package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import zar.databinding.ItemAppBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class AppItemHolder(
    private val binding: ItemAppBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun appClick(action: Int)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AppModel) {
        binding.item = item
        binding.root.setOnClickListener {
            click.appClick(item.link)
        }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}