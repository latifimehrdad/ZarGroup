package com.zarholding.zar.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zarholding.zar.database.dao.ArticleDao
import com.zarholding.zar.database.dao.RoleDao
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.database.entity.UserInfoEntity


@Database(entities = [ArticleEntity::class, UserInfoEntity::class, RoleEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun articleDao() : ArticleDao
    abstract fun userInfoDao() : UserInfoDao
    abstract fun roleDao() : RoleDao
}