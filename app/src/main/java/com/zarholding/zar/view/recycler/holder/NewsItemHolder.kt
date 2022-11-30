package com.zarholding.zar.view.recycler.holder

import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.database.entity.ArticleEntity
import zar.databinding.ItemNewsBinding

/**
 * Created by m-latifi on 11/15/2022.
 */

class NewsItemHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

    //---------------------------------------------------------------------------------------------- bind
    fun bind(item: ArticleEntity) {
        binding.item = item
        binding.executePendingBindings()
    }
    //---------------------------------------------------------------------------------------------- bind

}