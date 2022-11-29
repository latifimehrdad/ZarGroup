package com.zarholding.zar.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import com.zarholding.zar.model.response.trip.TripPointModel
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import kotlin.math.*


/**
 * Created by m-latifi on 11/19/2022.
 */


class OsmManager {


    //---------------------------------------------------------------------------------------------- measureDistance
    fun measureDistance(Old: GeoPoint, New: GeoPoint): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            Old.latitude, Old.longitude,
            New.latitude, New.longitude, results
        )
        return if (results.isNotEmpty()) results[0] else 0f
    }
    //---------------------------------------------------------------------------------------------- measureDistance



    //---------------------------------------------------------------------------------------------- getBoundingBoxFromPoints
    fun getBoundingBoxFromPoints(points: List<GeoPoint>): BoundingBox {
        var north = 0.0
        var south = 0.0
        var west = 0.0
        var east = 0.0
        for (i in points.indices) {
            val lat = points[i].latitude
            val lon = points[i].longitude
            if (i == 0 || lat > north) north = lat
            if (i == 0 || lat < south) south = lat
            if (i == 0 || lon < west) west = lon
            if (i == 0 || lon > east) east = lon
        }
        north += 0.02
        south -= 0.02
        east += 0.02
        west -= 0.02
        return BoundingBox(north, east, south, west)
    }
    //---------------------------------------------------------------------------------------------- getBoundingBoxFromPoints



    //---------------------------------------------------------------------------------------------- getBearing
    fun getBearing(
        from: GeoPoint,
        to: GeoPoint
    ): Double {
        val degreesPerRadian = 180.0 / PI
        val lat1: Double = from.latitude * PI / 180.0
        val lon1: Double = from.longitude * PI / 180.0
        val lat2: Double = to.latitude * PI / 180.0
        val lon2: Double = to.longitude * PI / 180.0
        var angle = -atan2(
            sin(lon1 - lon2) * cos(lat2),
            cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon1 - lon2)
        )
        if (angle < 0.0) angle += PI * 2.0
        angle *= degreesPerRadian
        return angle
    }
    //---------------------------------------------------------------------------------------------- getBearing



    //---------------------------------------------------------------------------------------------- getGeoPoints
    fun getGeoPoints(tripPoints : List<TripPointModel>) : ArrayList<GeoPoint> {
        val geoPoints = ArrayList<GeoPoint>()
        for (item in tripPoints)
            geoPoints.add(GeoPoint(item.lat.toDouble(), item.long.toDouble()))
        return geoPoints
    }
    //---------------------------------------------------------------------------------------------- getGeoPoints


}