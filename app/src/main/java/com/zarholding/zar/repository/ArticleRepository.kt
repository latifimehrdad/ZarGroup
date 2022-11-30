package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.ArticleRequestModel
import javax.inject.Inject

class ArticleRepository @Inject constructor(private val api : ApiSuperApp) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestGetArticles
    fun requestGetArticles(request : ArticleRequestModel, token : String) =
        apiCall(emitter){api.requestGetArticles(request, token)}
    //---------------------------------------------------------------------------------------------- requestGetArticles

}