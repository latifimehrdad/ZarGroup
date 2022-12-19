package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import zar.databinding.ItemPassengerBinding

/**
 * Created by m-latifi on 11/14/2022.
 */

class PassengerAdapter(
    private val users: MutableList<UserInfoEntity>,
    private val click: PassengerItemHolder.Click
) : RecyclerView.Adapter<PassengerItemHolder>() {

    private var layoutInflater: LayoutInflater? = null


    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerItemHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        return PassengerItemHolder(
            ItemPassengerBinding.inflate(layoutInflater!!, parent, false),
            click
        )
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder


    //---------------------------------------------------------------------------------------------- onBindViewHolder
    override fun onBindViewHolder(holder: PassengerItemHolder, position: Int) {
        holder.bind(users[position], position == users.size-1, position == 0)
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getItemCount
    override fun getItemCount() = users.size
    //---------------------------------------------------------------------------------------------- getItemCount



    //---------------------------------------------------------------------------------------------- addUser
    fun addUser(item: UserInfoEntity) {
        users.add(item)
        notifyItemRangeChanged(0, users.size)
    }
    //---------------------------------------------------------------------------------------------- addUser



    //---------------------------------------------------------------------------------------------- deleteUser
    fun deleteUser(item: UserInfoEntity) {
        users.remove(item)
        notifyItemRangeChanged(0, users.size)
    }
    //---------------------------------------------------------------------------------------------- deleteUser



    //---------------------------------------------------------------------------------------------- getList
    fun getList() = users
    //---------------------------------------------------------------------------------------------- getList

}