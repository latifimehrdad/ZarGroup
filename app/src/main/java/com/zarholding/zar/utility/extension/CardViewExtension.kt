package com.zarholding.zar.utility.extension

import androidx.cardview.widget.CardView
import com.zarholding.zar.model.enum.EnumTripStatus
import zar.R

/**
 * Created by m-latifi on 01/04/2023.
 */

//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun CardView.setRegisterStationStatus(status: EnumTripStatus?) {
    status?.let {
        when (status) {
            EnumTripStatus.Pending -> setCardBackgroundColor(
                context.resources.getColor(
                    R.color.waiting,
                    context.theme
                )
            )
            EnumTripStatus.Confirmed -> setCardBackgroundColor(
                context.resources.getColor(
                    R.color.positive,
                    context.theme
                )
            )
            EnumTripStatus.Reject -> setCardBackgroundColor(
                context.resources.getColor(
                    R.color.negative,
                    context.theme
                )
            )
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
