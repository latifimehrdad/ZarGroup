package com.zarholding.zar.utility.extension

import androidx.cardview.widget.CardView
import com.zarholding.zar.model.enum.EnumStatus
import zar.R

/**
 * Created by m-latifi on 01/04/2023.
 */

//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun CardView.setRegisterStationStatus(status: EnumStatus?) {
    status?.let {
        when (status) {
            EnumStatus.Pending -> setCardBackgroundColor(
                context.resources.getColor(
                    R.color.waiting,
                    context.theme
                )
            )
            EnumStatus.Confirmed -> setCardBackgroundColor(
                context.resources.getColor(
                    R.color.positive,
                    context.theme
                )
            )
            EnumStatus.Reject -> setCardBackgroundColor(
                context.resources.getColor(
                    R.color.negative,
                    context.theme
                )
            )
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
