package com.zarholding.zar.view.recycler.holder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.enum.EnumTaxiRequestStatus
import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import zar.R
import zar.databinding.ItemMyTaxiBinding

class MYTaxiHolder(
    private val binding: ItemMyTaxiBinding,
) : RecyclerView.ViewHolder(binding.root) {


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: AdminTaxiRequestModel) {
        binding.item = item
        val context = binding.root.context

        when(item.type) {
            EnumTaxiRequestType.OneWay -> {
                binding.textviewOriginDestinationDateTitle.text =
                    context.getString(R.string.departureDate)
                binding.textviewOriginDestinationTimeTitle.text =
                    context.getString(R.string.departureTimeDot)
            }
            EnumTaxiRequestType.Return -> {
                binding.textviewOriginDestinationDateTitle.text =
                    context.getString(R.string.departureReturnDate)
                binding.textviewOriginDestinationTimeTitle.text =
                    context.getString(R.string.departureReturnTime)
            }
        }


        when (item.status) {
            EnumTaxiRequestStatus.Pending -> {
                binding.linearLayoutStatus.background = ContextCompat
                    .getDrawable(context, R.drawable.drawable_spinner_select)
                binding.textViewStatus.text = context.getString(R.string.pendingForAccept)
                binding.textViewRejectReason.visibility = View.GONE
            }
            EnumTaxiRequestStatus.Confirm -> {
                binding.linearLayoutStatus.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.drawable_confirm)
                binding.textViewStatus.text = context.getString(R.string.confirm)
                binding.textViewRejectReason.visibility = View.GONE
            }
            EnumTaxiRequestStatus.Reject -> {
                binding.linearLayoutStatus.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.drawable_reject)
                binding.textViewStatus.text = context.getString(R.string.reject)
                binding.textViewRejectReason.visibility = View.VISIBLE
            }
            EnumTaxiRequestStatus.Confirmed -> {}
        }

        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind


}