package com.zarholding.zar.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.database.entity.UserInfoEntity

@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userInfoEntity: UserInfoEntity)

    @Query("SELECT * FROM UserInfo LIMIT 1")
    fun getUserInfo(): UserInfoEntity?

    @Query("DELETE FROM userinfo")
    fun deleteAllRole()

}