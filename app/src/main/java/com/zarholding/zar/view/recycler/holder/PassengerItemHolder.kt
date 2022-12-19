package com.zarholding.zar.view.recycler.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.database.entity.UserInfoEntity
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
        fun addClick()
        fun itemClick(item: UserInfoEntity)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: UserInfoEntity, last : Boolean, first : Boolean) {
        binding.item = item
        if (last)
            binding.linearLayoutAdd.visibility = View.VISIBLE
        else
            binding.linearLayoutAdd.visibility = View.GONE

        if (first)
            binding.materialButtonTitle.icon = null
        else
            binding.root.setOnClickListener { click.itemClick(item) }

        binding.imageViewAdd.setOnClickListener {
            click.addClick()
        }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}