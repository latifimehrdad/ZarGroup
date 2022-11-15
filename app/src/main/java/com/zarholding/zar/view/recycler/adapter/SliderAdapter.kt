package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter
import com.zarholding.zar.model.other.AppModel
import zar.R

/**
 * Created by m-latifi on 11/14/2022.
 */

class SliderAdapter(private val apps: MutableList<AppModel>) : CardSliderAdapter<SliderAdapter.MovieViewHolder>() {

    override fun getItemCount() = apps.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return MovieViewHolder(view)
    }

    override fun bindVH(holder: MovieViewHolder, position: Int) {
        //TODO bind item object with item layout view
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view)
}