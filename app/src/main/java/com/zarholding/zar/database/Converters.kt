package com.zarholding.zar.database

import androidx.room.TypeConverter
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

}