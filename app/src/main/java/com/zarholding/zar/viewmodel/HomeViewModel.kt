package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.model.enum.EnumArticleType
import com.zarholding.zar.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {



    //---------------------------------------------------------------------------------------------- getArticles
    fun getArticles(type : EnumArticleType) : List<ArticleEntity>{
        return articleRepository.getArticles(type)
    }
    //---------------------------------------------------------------------------------------------- getArticles

}