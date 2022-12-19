package com.zarholding.zar.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by m-latifi on 11/26/2022.
 */

@Entity(tableName = "UserInfo")
data class UserInfoEntity(
    val userName : String?,
    val fullName : String?,
    val personnelNumber : String?,
    val personnelJobKeyCode : String?,
    val personnelJobKeyText : String?,
    val companyCode : String?,
    val organizationUnit : String?,
    val phone : String?,
    val email : String?,
    val mobile : String?,
    val roles : List<String>?
) {
    @PrimaryKey(autoGenerate = false)
    var id : Int = 0
}

