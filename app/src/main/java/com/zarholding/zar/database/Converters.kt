package com.zarholding.zar.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class Converters {

    //---------------------------------------------------------------------------------------------- fromTimestamp
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime?{
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), TimeZone.getDefault().toZoneId()) }
    }
    //---------------------------------------------------------------------------------------------- fromTimestamp


    //---------------------------------------------------------------------------------------------- dateToTimestamp
    @TypeConverter
    fun dateToTimestamp(localDateTime: LocalDateTime?) : Long? {
        return localDateTime?.atZone(TimeZone.getDefault().toZoneId())?.toEpochSecond()
    }
    //---------------------------------------------------------------------------------------------- dateToTimestamp


    //---------------------------------------------------------------------------------------------- fromString
    @TypeConverter
    fun fromString(value: String?): List<String?>? {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }
    //---------------------------------------------------------------------------------------------- fromString


    //---------------------------------------------------------------------------------------------- fromList
    @TypeConverter
    fun fromList(list: List<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
    //---------------------------------------------------------------------------------------------- fromList
}