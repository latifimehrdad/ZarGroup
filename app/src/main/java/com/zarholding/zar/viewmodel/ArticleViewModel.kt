package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.database.dao.ArticleDao
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.model.enum.EnumArticleType
import com.zarholding.zar.model.request.ArticleRequestModel
import com.zarholding.zar.repository.ArticleRepository
import com.zarholding.zar.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repo: ArticleRepository,
    private val articleDao: ArticleDao,
    private val tokenRepository: TokenRepository
) : ViewModel() {


    //---------------------------------------------------------------------------------------------- requestGetArticles
    fun requestGetArticles(request: ArticleRequestModel) =
        repo.requestGetArticles(request, tokenRepository.getBearerToken())
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