package com.zarholding.zar.model.response.article

import com.zarholding.zar.database.entity.ArticleEntity

data class ArticleItemsModel(
    val items : List<ArticleEntity?>
)
