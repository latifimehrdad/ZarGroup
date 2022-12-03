package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.database.entity.ArticleEntity
import zar.databinding.ItemNewsBinding

/**
 * Created by m-latifi on 11/15/2022.
 */

class NewsItemHolder(
    private val binding: ItemNewsBinding,
    private val click: Click
) : RecyclerView.ViewHolder(binding.root) {


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun detailArticle(article: ArticleEntity)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: ArticleEntity) {
        binding.item = item
        binding.buttonShow.setOnClickListener { click.detailArticle(item) }
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}