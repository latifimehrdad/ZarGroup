package com.zarholding.zar.view.recycler.holder

import com.zar.core.tools.autoimageslider.SliderViewAdapter
import com.zarholding.zar.database.entity.ArticleEntity
import zar.databinding.ItemBannerSliderBinding

/**
 * Created by m-latifi on 11/15/2022.
 */

class BannerItemHolder(private val binding: ItemBannerSliderBinding) :
    SliderViewAdapter.ViewHolder(binding.root) {


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: ArticleEntity) {
        binding.item = item
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind


}