package com.zarholding.zar.utility

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumAuthorizationType
import zar.R

class UnAuthorizationManager(
    private val fragmentActivity: FragmentActivity?,
    private val type: EnumAuthorizationType,
    private val message: String,
    private val view: View
) {

    init {
        when (type) {
            EnumAuthorizationType.UnAuthorization -> unAuthorization()
            EnumAuthorizationType.UnAccess -> unAccess()
        }
    }
    

    private fun unAuthorization() {
        val snack = Snackbar.make(view, message, 5 * 1000)
        snack.setBackgroundTint(view.resources.getColor(R.color.primaryColor, view.context.theme))
        snack.setTextColor(view.resources.getColor(R.color.textViewColor3, view.context.theme))
        snack.setAction(view.context.getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(view.context.resources.getColor(R.color.textViewColor1, view.context.theme))
        snack.show()
        fragmentActivity?.onBackPressedDispatcher?.onBackPressed()
    }

    private fun unAccess() {

    }
}