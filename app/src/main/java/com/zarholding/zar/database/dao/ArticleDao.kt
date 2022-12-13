package com.zarholding.zar.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zarholding.zar.database.entity.ArticleEntity

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(articleEntity: ArticleEntity)

    @Query("SELECT * FROM Article WHERE articleType = :articleType")
    fun getArticles(articleType: String): List<ArticleEntity>

}