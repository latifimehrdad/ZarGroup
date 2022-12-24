package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.database.dao.ArticleDao
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.model.enum.EnumArticleType
import com.zarholding.zar.model.request.ArticleRequestModel
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val api : ApiSuperApp,
    private val articleDao: ArticleDao) {

    //---------------------------------------------------------------------------------------------- requestGetArticles
    suspend fun requestGetArticles(request : ArticleRequestModel, token : String) =
        apiCall{ api.requestGetArticles(request, token) }
    //---------------------------------------------------------------------------------------------- requestGetArticles


    //---------------------------------------------------------------------------------------------- insertArticle
    fun insertArticle(items: List<ArticleEntity?>) {
        for (item in items)
            item?.let { articleDao.insertArticle(it) }
    }
    //---------------------------------------------------------------------------------------------- insertArticle


    //---------------------------------------------------------------------------------------------- getArticles
    fun getArticles(type : EnumArticleType) : List<ArticleEntity>{
        return articleDao.getArticles(type.name)
    }
    //---------------------------------------------------------------------------------------------- getArticles

}