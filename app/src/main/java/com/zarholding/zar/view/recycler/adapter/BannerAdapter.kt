package com.zarholding.zar.view.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.view.recycler.holder.BannerItemHolder
import com.zarholding.zar.view.autoimageslider.SliderViewAdapter
import zar.databinding.ItemBannerSliderBinding

/**
 * Created by m-latifi on 11/15/2022.
 */

class BannerAdapter(private val banners : List<ArticleEntity>) :
    SliderViewAdapter<BannerItemHolder>() {

    private var layoutInflater : LayoutInflater? = null
    lateinit var context: Context

    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup?): BannerItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent!!.context)
        context = parent!!.context
        return BannerItemHolder(ItemBannerSliderBinding.inflate(layoutInflater!!,parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder



    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(viewHolder: BannerItemHolder?, position: Int) {
        viewHolder?.bind(banners[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder



    //---------------------------------------------------------------------------------------------- getCount
    override fun getCount() = banners.size
    //---------------------------------------------------------------------------------------------- getCount


}