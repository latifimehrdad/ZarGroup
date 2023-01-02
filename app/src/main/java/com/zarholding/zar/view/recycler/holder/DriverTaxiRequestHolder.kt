package com.zarholding.zar.view.recycler.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumTaxiRequestType
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
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AdminTaxiRequestModel) {
        binding.item = item
        when (item.type) {
            EnumTaxiRequestType.OneWay -> {
                binding.linearLayoutReturn.visibility = View.INVISIBLE
            }
            EnumTaxiRequestType.Return -> {
                binding.linearLayoutReturn.visibility = View.VISIBLE
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