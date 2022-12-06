package com.zarholding.zar.view.recycler.holder

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.other.ShowImageModel
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.view.extension.setRegisterStationStatus
import zar.R
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
        fun showImage(item: ShowImageModel)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: TripModel, click: Click) {
        binding.item = item
        binding.root.setOnClickListener { click.serviceClick(item) }
        binding.textViewDelete.setOnClickListener { click.deleteRegisterStation(item) }
        binding.imageViewDelete.setOnClickListener { click.deleteRegisterStation(item) }
        binding.cardViewImage.setRegisterStationStatus(item.myStationTripStatus)
        binding.imageViewStatus.setRegisterStationStatus(item.myStationTripStatus)
        binding.textViewStatus.setRegisterStationStatus(item.myStationTripStatus)
        item.myStationTripStatus?.let {
            if (it == EnumTripStatus.Reject)
                binding.textViewReason.visibility = View.VISIBLE
            else
                binding.textViewReason.visibility = View.GONE
        } ?: run {
            binding.textViewReason.visibility = View.GONE
        }
        binding.imageViewBus.setOnClickListener {
            click.showImage(
                ShowImageModel(
                    item.carImageName!!,
                    binding.imageViewBus.context.resources.getString(R.string.carEntityType)
                )
            )
        }
        binding.imageViewDriver.setOnClickListener {
            click.showImage(
                ShowImageModel(
                    item.driverImageName!!,
                    binding.imageViewBus.context.resources.getString(R.string.driversEntityType)
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