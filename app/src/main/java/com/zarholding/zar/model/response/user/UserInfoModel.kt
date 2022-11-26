package com.zarholding.zar.model.response.user

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by m-latifi on 11/26/2022.
 */

data class UserInfoModel(
    val id : Int,
    val userName : String?,
    val fullName : String?,
    val personnelNumber : String?,
    val organizationUnit : String?,
    val phone : String?,
    val email : String?,
    val mobile : String?
) : Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(userName)
        parcel.writeString(fullName)
        parcel.writeString(personnelNumber)
        parcel.writeString(organizationUnit)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(mobile)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfoModel> {
        override fun createFromParcel(parcel: Parcel): UserInfoModel {
            return UserInfoModel(parcel)
        }

        override fun newArray(size: Int): Array<UserInfoModel?> {
            return arrayOfNulls(size)
        }
    }
}
