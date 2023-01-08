package com.zarholding.zar.utility.extension

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.hilt.Providers
import com.zarholding.zar.model.enum.EnumStatus
import zar.R

/**
 * Created by m-latifi on 01/04/2023.
 */

//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun ImageView.setRegisterStationStatus(status: EnumStatus?) {
    status?.let {
        when (status) {
            EnumStatus.Pending -> setImageResource(R.drawable.ic_pending)
            EnumStatus.Confirmed -> setImageResource(R.drawable.ic_check)
            EnumStatus.Reject -> setImageResource(R.drawable.ic_delete)
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus


//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon
@BindingAdapter("setAppIcon")
fun ImageView.setAppIcon(icon: Int) {
    setImageResource(icon)
}
//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon


//-------------------------------------------------------------------------------------------------- loadImage
@BindingAdapter("loadImage", "setEntityType")
fun ImageView.loadImage(url: String, entityType: String) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    val link = "${Providers.url}${ApiSuperApp.v1}/Content/file?entityType=$entityType&fileName=$url"
    Glide
        .with(this)
        .load(link)
        .into(this)
}
//-------------------------------------------------------------------------------------------------- loadImage



//-------------------------------------------------------------------------------------------------- loadImageProfile
@BindingAdapter("loadImageProfile", "bearerToken")
fun ImageView.loadImageProfile(url: String?, token : String) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    val link = "${Providers.url}${ApiSuperApp.v1}/LogIn/get-user-avatar/$url"
    val glideUrl = GlideUrl(
        link,
        LazyHeaders.Builder().addHeader("Authorization", token).build()
    )
    Glide
        .with(this)
        .load(glideUrl)
        .into(this)
}
//-------------------------------------------------------------------------------------------------- loadImageProfile
