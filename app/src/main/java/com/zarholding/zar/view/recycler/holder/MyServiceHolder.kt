package com.zarholding.zar.view.recycler.holder

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.trip.TripModel
import zar.databinding.ItemMyServiceBinding


/**
 * Created by m-latifi on 11/20/2022.
 */

class MyServiceHolder(private val binding: ItemMyServiceBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun serviceClick(item: TripModel)
        fun deleteRegisterStation(item: TripModel)
    }
    //---------------------------------------------------------------------------------------------- Click



    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: TripModel, click: Click) {
        binding.item = item
        binding.root.setOnClickListener { click.serviceClick(item) }
        binding.imageViewShowMore.setOnClickListener {
            if (binding.expandableMore.isExpanded)
                hideMore()
            else
                showMore()
        }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind



    //---------------------------------------------------------------------------------------------- showMore
    private fun showMore() {
        binding.expandableMore.expand()

        val rotate = RotateAnimation(
            0f,
            -90f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 350
        rotate.interpolator = LinearInterpolator()
        rotate.fillAfter = true
        binding.imageViewShowMore.startAnimation(rotate)
    }
    //---------------------------------------------------------------------------------------------- showMore



    //---------------------------------------------------------------------------------------------- hideMore
    private fun hideMore() {
        binding.expandableMore.collapse()
        val rotate = RotateAnimation(
            -90f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 400
        rotate.interpolator = LinearInterpolator()
        rotate.fillAfter = true
        binding.imageViewShowMore.startAnimation(rotate)
    }
    //---------------------------------------------------------------------------------------------- hideMore

}