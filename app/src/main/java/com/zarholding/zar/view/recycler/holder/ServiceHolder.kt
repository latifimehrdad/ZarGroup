package com.zarholding.zar.view.recycler.holder

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.ShowImageModel
import com.zarholding.zar.model.response.trip.TripModel
import zar.R
import zar.databinding.ItemServiceBinding


/**
 * Created by m-latifi on 11/20/2022.
 */

class ServiceHolder(private val binding: ItemServiceBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun serviceClick(item: TripModel)
        fun registerStation(item: TripModel)
        fun showImage(item: ShowImageModel)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: TripModel, click: Click) {
        binding.item = item
        binding.root.setOnClickListener { click.serviceClick(item) }
        binding.textViewPlus.setOnClickListener { click.registerStation(item) }
        binding.imageViewPlus.setOnClickListener { click.registerStation(item) }
        binding.imageViewBus.setOnClickListener {
            click.showImage(
                ShowImageModel(
                    item.carImageName!!,
                    binding.imageViewBus.context.resources.getString(R.string.carEntityType),
                    null
                )
            )
        }
        binding.imageViewDriver.setOnClickListener {
            click.showImage(
                ShowImageModel(
                    item.driverImageName!!,
                    binding.imageViewBus.context.resources.getString(R.string.driversEntityType),
                    null
                )
            )
        }
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