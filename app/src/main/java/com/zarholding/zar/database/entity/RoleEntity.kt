package com.zarholding.zar.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Role")
data class RoleEntity(
    @PrimaryKey val permission : String
)
