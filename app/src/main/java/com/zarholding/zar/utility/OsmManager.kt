package com.zarholding.zar.utility

import android.util.Size
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import java.lang.Math.*


/**
 * Created by m-latifi on 11/19/2022.
 */


class OsmManager {


    fun addPolyline() : List<GeoPoint> {
        val geoPoints = mutableListOf<GeoPoint>()
        geoPoints.add(GeoPoint(35.84033, 51.01642))
        geoPoints.add(GeoPoint(35.84035, 51.01581))
        geoPoints.add(GeoPoint(35.84041, 51.01533))
        geoPoints.add(GeoPoint(35.84063, 51.01539))
        geoPoints.add(GeoPoint(35.84073, 51.01529))
        geoPoints.add(GeoPoint(35.84070, 51.01510))
        geoPoints.add(GeoPoint(35.84058, 51.01492))
        geoPoints.add(GeoPoint(35.84052, 51.01476))
        geoPoints.add(GeoPoint(35.84013, 51.01408))
        geoPoints.add(GeoPoint(35.83967, 51.01332))
        geoPoints.add(GeoPoint(35.83947, 51.01305))
        geoPoints.add(GeoPoint(35.83921, 51.01283))
        geoPoints.add(GeoPoint(35.83902, 51.01263))
        geoPoints.add(GeoPoint(35.83875, 51.01226))
        geoPoints.add(GeoPoint(35.83825, 51.01178))
        geoPoints.add(GeoPoint(35.83698, 51.01058))
        geoPoints.add(GeoPoint(35.83659, 51.01027))
        geoPoints.add(GeoPoint(35.83605, 51.01020))
        return geoPoints.toList()
    }


    fun getGeo() : List<GeoPoint> {
        val geoPoints = mutableListOf<GeoPoint>()
        geoPoints.add(GeoPoint(35.84033, 51.01642))
        geoPoints.add(GeoPoint(35.84035, 51.01581))
        geoPoints.add(GeoPoint(35.84041, 51.01533))
        geoPoints.add(GeoPoint(35.84063, 51.01539))
        geoPoints.add(GeoPoint(35.84073, 51.01529))
        geoPoints.add(GeoPoint(35.84070, 51.01510))
        geoPoints.add(GeoPoint(35.84033, 51.01642))
        return geoPoints.toList()
    }

    fun computeArea(): BoundingBox {
        var nord = 0.0
        var sud = 0.0
        var ovest = 0.0
        var est = 0.0
        for (i in getGeo().indices) {
            if (getGeo()[i] == null) continue
            val lat = getGeo()[i]!!.latitude
            val lon = getGeo()[i]!!.longitude
            if (i == 0 || lat > nord) nord = lat
            if (i == 0 || lat < sud) sud = lat
            if (i == 0 || lon < ovest) ovest = lon
            if (i == 0 || lon > est) est = lon
        }
        return BoundingBox(nord, est, sud, ovest)
    }


}