package com.zarholding.zar.model.response.trip

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripPointModel(
    val lat : Float,
    val long : Float
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(lat)
        parcel.writeFloat(long)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TripPointModel> {
        override fun createFromParcel(parcel: Parcel): TripPointModel {
            return TripPointModel(parcel)
        }

        override fun newArray(size: Int): Array<TripPointModel?> {
            return arrayOfNulls(size)
        }
    }
}
