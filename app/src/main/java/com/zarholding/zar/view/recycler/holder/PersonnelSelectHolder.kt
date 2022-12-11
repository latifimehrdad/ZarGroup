package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.database.entity.UserInfoEntity
import zar.databinding.ItemPersonnelSelectBinding

class PersonnelSelectHolder(
    private val binding: ItemPersonnelSelectBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun select(item : UserInfoEntity)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item : UserInfoEntity) {
        binding.item = item
        binding.root.setOnClickListener {
            click.select(item)
        }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}