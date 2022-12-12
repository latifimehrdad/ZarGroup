package com.zarholding.zar.utility

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumAuthorizationType
import com.zarholding.zar.view.activity.MainActivity
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zar.R

@Module
@InstallIn(SingletonComponent::class)
class UnAuthorizationManager {

    private var activity: FragmentActivity? = null
    private lateinit var type: EnumAuthorizationType
    private lateinit var message: String
    private lateinit var view: View

    //---------------------------------------------------------------------------------------------- handel
    fun handel(
        activity: FragmentActivity?,
        type: EnumAuthorizationType,
        message: String,
        view: View
    ) {
        this.activity = activity
        this.type = type
        this.message = message
        this.view = view

        when (type) {
            EnumAuthorizationType.UnAuthorization -> unAuthorization()
            EnumAuthorizationType.UnAccess -> unAccess()
        }
    }
    //---------------------------------------------------------------------------------------------- handel


    //---------------------------------------------------------------------------------------------- unAuthorization
    private fun unAuthorization() {
        (activity as MainActivity?)?.gotoFirstFragment()
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- unAccess
    private fun unAccess() {
        val snack = Snackbar.make(view, message, 5 * 1000)
        snack.setBackgroundTint(view.resources.getColor(R.color.primaryColor, view.context.theme))
        snack.setTextColor(view.resources.getColor(R.color.textViewColor3, view.context.theme))
        snack.setAction(view.context.getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(
            view.context.resources.getColor(
                R.color.textViewColor1,
                view.context.theme
            )
        )
        snack.show()
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
    //---------------------------------------------------------------------------------------------- unAccess

}