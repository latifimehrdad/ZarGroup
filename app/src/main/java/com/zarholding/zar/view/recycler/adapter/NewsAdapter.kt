package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.response.news.NewsModel
import com.zarholding.zar.view.recycler.holder.NewsItemHolder
import zar.databinding.ItemNewsBinding

/**
 * Created by m-latifi on 11/15/2022.
 */

class NewsAdapter(private val news : MutableList<NewsModel>) :
    RecyclerView.Adapter<NewsItemHolder>() {


    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return NewsItemHolder(ItemNewsBinding.inflate(layoutInflater!!,parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: NewsItemHolder, position: Int) {
        holder.bind(news[position], position)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = news.size
    //---------------------------------------------------------------------------------------------- getItemCount


}