package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.view.recycler.holder.PersonnelSelectHolder
import zar.databinding.ItemPersonnelSelectBinding

class PersonnelSelectAdapter(
    private val items: MutableList<UserInfoEntity>,
    private val click: PersonnelSelectHolder.Click
) : RecyclerView.Adapter<PersonnelSelectHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonnelSelectHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return PersonnelSelectHolder(
            ItemPersonnelSelectBinding.inflate(layoutInflater!!, parent, false), click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: PersonnelSelectHolder, position: Int) {
        holder.bind(items[position])
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun getItemCount() = items.size
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- addPerson
    fun addPerson(list: List<UserInfoEntity>) {
        val oldSize = items.size
        items.addAll(list)
        notifyItemRangeChanged(oldSize-1, items.size)
    }
    //---------------------------------------------------------------------------------------------- addPerson

}