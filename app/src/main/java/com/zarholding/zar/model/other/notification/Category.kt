package com.zarholding.zar.model.other.notification

/**
 * Created by m-latifi on 11/16/2022.
 */

class Category(val name: String, vararg item: Item) {

    val listOfItem: List<Item> = item.toList()
}