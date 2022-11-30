package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.request.ArticleRequestModel
import com.zarholding.zar.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(private val repo : ArticleRepository) : ViewModel(){


    //---------------------------------------------------------------------------------------------- requestGetArticles
    fun requestGetArticles(request : ArticleRequestModel, token : String) =
        repo.requestGetArticles(request, token)
    //---------------------------------------------------------------------------------------------- requestGetArticles

}