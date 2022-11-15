package com.zarholding.zar.view.recycler.holder

import android.graphics.Bitmap
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import com.zarholding.zar.model.response.banner.BannerModel
import com.zarholding.zar.view.autoimageslider.SliderViewAdapter
import zar.databinding.ItemBannerSliderBinding

/**
 * Created by m-latifi on 11/15/2022.
 */

class BannerItemHolder(private val binding: ItemBannerSliderBinding) :
    SliderViewAdapter.ViewHolder(binding.root) {


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: BannerModel) {
        binding.item = item
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind


}