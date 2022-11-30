package com.zarholding.zar.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Article")
data class ArticleEntity(
    val title : String?,
    val summary : String?,
    val body : String?,
    val articleType : String,
    val orderNum : Int,
    val isActive : Boolean,
    val imageName : String?
) {
    @PrimaryKey(autoGenerate = false)
    var id : Int = 0
}
