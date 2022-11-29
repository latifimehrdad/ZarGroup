package com.zarholding.zar.view.recycler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import zar.R


class SpinnerStringAdapter(private val items : List<String?>)  : BaseAdapter() {

    private var inflater: LayoutInflater? = null

    override fun getCount() = items.size

    override fun getItem(p0: Int) = items[p0]

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, parent: ViewGroup?): View {
        if (inflater == null)
            inflater = LayoutInflater.from(parent!!.context)
        val view = inflater!!.inflate(R.layout.item_string_spinner, parent, false)
        val title = view.findViewById<TextView>(R.id.textViewTitle)
        title.text = items[p0]
        return view
    }

}