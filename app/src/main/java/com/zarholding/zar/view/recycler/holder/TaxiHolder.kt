package com.zarholding.zar.view.recycler.holder

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import zar.R
import zar.databinding.ItemTaxiBinding

class TaxiHolder(
    private val binding : ItemTaxiBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun accept(item : AdminTaxiRequestModel)
        fun reject(item : AdminTaxiRequestModel)
    }
    //---------------------------------------------------------------------------------------------- Click



    //---------------------------------------------------------------------------------------------- bind
    fun bind(item : AdminTaxiRequestModel) {
        binding.item = item
        binding.textviewOriginDestinationDateTitle.text = when(item.type) {
                EnumTaxiRequestType.OneWay ->
                    binding.root.resources.getString(R.string.departureDate)
                EnumTaxiRequestType.Return ->
                    binding.root.resources.getString(R.string.departureReturnDate)
            }

        binding.textviewOriginDestinationTimeTitle.text = when(item.type) {
            EnumTaxiRequestType.OneWay ->
                binding.root.resources.getString(R.string.departureTimeDot)
            EnumTaxiRequestType.Return ->
                binding.root.resources.getString(R.string.departureReturnTime)
        }

        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind


}