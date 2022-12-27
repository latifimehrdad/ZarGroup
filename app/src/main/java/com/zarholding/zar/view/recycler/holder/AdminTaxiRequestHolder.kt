package com.zarholding.zar.view.recycler.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumPersonnelType
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import zar.R
import zar.databinding.ItemAdminTaxiRequestBinding

class AdminTaxiRequestHolder(
    private val binding: ItemAdminTaxiRequestBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun accept(item: AdminTaxiRequestModel)
        fun reject(item: AdminTaxiRequestModel)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AdminTaxiRequestModel, userType: EnumPersonnelType) {
        binding.item = item
        binding.textviewOriginDestinationDateTitle.text = when (item.type) {
            EnumTaxiRequestType.OneWay ->
                binding.root.resources.getString(R.string.departureDate)
            EnumTaxiRequestType.Return ->
                binding.root.resources.getString(R.string.departureReturnDate)
        }

        binding.textviewOriginDestinationTimeTitle.text = when (item.type) {
            EnumTaxiRequestType.OneWay ->
                binding.root.resources.getString(R.string.departureTimeDot)
            EnumTaxiRequestType.Return ->
                binding.root.resources.getString(R.string.departureReturnTime)
        }
        when (userType) {
            EnumPersonnelType.Driver -> {
                binding.buttonReject.visibility = View.GONE
                binding.buttonConfirm.visibility = View.GONE
            }
            else -> {
                binding.buttonReject.visibility = View.VISIBLE
                binding.buttonConfirm.visibility = View.VISIBLE
            }
        }
        binding.buttonReject.setOnClickListener { click.reject(item) }
        binding.buttonConfirm.setOnClickListener { click.accept(item) }

        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind


}