package com.zarholding.zar.view.recycler.holder

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import org.osmdroid.util.GeoPoint
import zar.R
import zar.databinding.ItemDriverTaxiRequestBinding

class DriverTaxiRequestHolder(
    private val binding: ItemDriverTaxiRequestBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun showOnMap(
            latOrigin: Double,
            lngOrigin: Double,
            latDestination: Double,
            lngDestination: Double
        )
        fun changeTripStatus(position : Int)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AdminTaxiRequestModel, position : Int) {
        binding.item = item
        val context = binding.root.context
        when (item.type) {
            EnumTaxiRequestType.OneWay -> {
                binding.linearLayoutReturn.visibility = View.INVISIBLE
            }
            EnumTaxiRequestType.Return -> {
                binding.linearLayoutReturn.visibility = View.VISIBLE
            }
        }

        when(item.tripStatus) {
            EnumTripStatus.Assigned -> {
                binding.textViewTripStatus.background =
                    AppCompatResources.getDrawable(context, R.drawable.drawable_confirm)
                binding.textViewTripStatus
                    .setTextColor(context.resources.getColor(R.color.textViewColor1, context.theme))
                binding.textViewTripStatus.text = context.getString(R.string.startTrip)
            }
            EnumTripStatus.Started -> {
                binding.textViewTripStatus.background =
                    AppCompatResources.getDrawable(context, R.drawable.drawable_reject)
                binding.textViewTripStatus
                    .setTextColor(context.resources.getColor(R.color.textViewColor1, context.theme))
                binding.textViewTripStatus.text = context.getString(R.string.finishTrip)
            }
            else ->{
                binding.textViewTripStatus.background =
                    AppCompatResources.getDrawable(context, R.drawable.drawable_spinner_select)
                binding.textViewTripStatus
                    .setTextColor(context.resources.getColor(R.color.textViewColor2, context.theme))
                binding.textViewTripStatus.text = context.getString(R.string.finishedTrip)
            }

        }

        binding.textViewTripStatus.setOnClickListener {
            when(item.tripStatus) {
                EnumTripStatus.Assigned,
                    EnumTripStatus.Started,
                    EnumTripStatus.Finished -> click.changeTripStatus(position)
                else -> {}
            }
        }

        binding.linearLayoutMap.setOnClickListener {
            click.showOnMap(
                item.originLat,
                item.originLong,
                item.destinationLat,
                item.destinationLong
            )
        }


        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind


}