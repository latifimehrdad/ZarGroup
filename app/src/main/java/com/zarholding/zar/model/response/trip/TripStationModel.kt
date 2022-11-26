package com.zarholding.zardriver.model.response

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripStationModel(
    val id : Int,
    val commuteTripsId : Int,
    val commuteTripName : String?,
    val destinationName : String?,
    val originName : String?,
    val stationName : String?,
    val arriveTime : String?,
    val stationLat : Float,
    val sationLong : Float,
    val stationNum : Float,
    val isActive : Boolean,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(commuteTripsId)
        parcel.writeString(commuteTripName)
        parcel.writeString(destinationName)
        parcel.writeString(originName)
        parcel.writeString(stationName)
        parcel.writeString(arriveTime)
        parcel.writeFloat(stationLat)
        parcel.writeFloat(sationLong)
        parcel.writeFloat(stationNum)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TripStationModel> {
        override fun createFromParcel(parcel: Parcel): TripStationModel {
            return TripStationModel(parcel)
        }

        override fun newArray(size: Int): Array<TripStationModel?> {
            return arrayOfNulls(size)
        }
    }

}
