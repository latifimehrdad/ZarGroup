package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.recycler.holder.RequestItemHolder
import zar.databinding.ItemRequestBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class RequestAdapter(private val apps : MutableList<AppModel>) : RecyclerView.Adapter<RequestItemHolder>() {

    private var layoutInflater : LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return RequestItemHolder(ItemRequestBinding.inflate(layoutInflater!!,parent, false))
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: RequestItemHolder, position: Int) {
        holder.bind(apps[position], position)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = apps.size
    //---------------------------------------------------------------------------------------------- getItemCount

}