package com.zarholding.zar.utility

import android.location.Location
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
        north += 0.01
        south -= 0.01
        east += 0.01
        west -= 0.01
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






    fun addPolyline(): List<GeoPoint> {
        val geoPoints = mutableListOf<GeoPoint>()
        geoPoints.add(GeoPoint(35.84033, 51.01617))
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
        geoPoints.add(GeoPoint(35.83580, 51.01002))
        geoPoints.add(GeoPoint(35.83575, 51.00994))
        geoPoints.add(GeoPoint(35.83570, 51.00980))
        geoPoints.add(GeoPoint(35.83566, 51.00963))
        geoPoints.add(GeoPoint(35.83563, 51.00943))
        geoPoints.add(GeoPoint(35.83572, 51.00783))
        geoPoints.add(GeoPoint(35.83579, 51.00623))
        geoPoints.add(GeoPoint(35.83591, 51.003773))
        geoPoints.add(GeoPoint(35.83592, 51.00313))
        geoPoints.add(GeoPoint(35.83599, 51.00289))
        geoPoints.add(GeoPoint(35.83612, 51.00282))
        geoPoints.add(GeoPoint(35.83628, 51.00259))
        geoPoints.add(GeoPoint(35.83626, 51.00227))
        geoPoints.add(GeoPoint(35.83610, 51.00207))
        geoPoints.add(GeoPoint(35.83537, 51.00181))
        geoPoints.add(GeoPoint(35.83439, 51.00091))
        geoPoints.add(GeoPoint(35.83368, 51.00031))
        geoPoints.add(GeoPoint(35.83278, 50.99952))
        geoPoints.add(GeoPoint(35.83195, 50.99877))
        geoPoints.add(GeoPoint(35.83159, 50.99812))
        geoPoints.add(GeoPoint(35.83133, 50.99798))
        geoPoints.add(GeoPoint(35.83107, 50.99799))
        geoPoints.add(GeoPoint(35.83094, 50.99793))
        geoPoints.add(GeoPoint(35.82999, 50.99706))
        geoPoints.add(GeoPoint(35.82711, 50.99460))
        geoPoints.add(GeoPoint(35.82521, 50.99300))
        geoPoints.add(GeoPoint(35.82383, 50.99181))
        geoPoints.add(GeoPoint(35.82169, 50.99036))
        geoPoints.add(GeoPoint(35.82154, 50.99002))
        geoPoints.add(GeoPoint(35.82153, 50.98989))
        geoPoints.add(GeoPoint(35.82158, 50.98966))
        geoPoints.add(GeoPoint(35.82206, 50.98869))
        geoPoints.add(GeoPoint(35.82294, 50.98678))
        geoPoints.add(GeoPoint(35.82432, 50.98345))
        geoPoints.add(GeoPoint(35.82511, 50.98045))
        geoPoints.add(GeoPoint(35.82688, 50.97510))
        geoPoints.add(GeoPoint(35.82918, 50.96758))
        geoPoints.add(GeoPoint(35.83396, 50.95234))
        geoPoints.add(GeoPoint(35.83679, 50.94326))
        geoPoints.add(GeoPoint(35.8398, 50.9338))
        geoPoints.add(GeoPoint(35.8478, 50.9141))
        geoPoints.add(GeoPoint(35.84810, 50.91366))
        geoPoints.add(GeoPoint(35.84937, 50.91072))
        geoPoints.add(GeoPoint(35.85037, 50.90976))
        geoPoints.add(GeoPoint(35.85353, 50.90951))
        geoPoints.add(GeoPoint(35.85404, 50.909791))
        geoPoints.add(GeoPoint(35.85474, 50.90958))
        geoPoints.add(GeoPoint(35.85516, 50.90916))
        geoPoints.add(GeoPoint(35.85620, 50.90883))
        geoPoints.add(GeoPoint(35.85676, 50.90824))
        geoPoints.add(GeoPoint(35.85812, 50.90770))
        geoPoints.add(GeoPoint(35.85915, 50.90693))
        geoPoints.add(GeoPoint(35.8640, 50.9041))
        geoPoints.add(GeoPoint(35.8685, 50.9000))
        geoPoints.add(GeoPoint(35.8730, 50.8950))
        geoPoints.add(GeoPoint(35.8782, 50.8881))
        geoPoints.add(GeoPoint(35.8851, 50.8789))
        geoPoints.add(GeoPoint(35.8895, 50.8730))
        geoPoints.add(GeoPoint(35.8945, 50.8664))
        geoPoints.add(GeoPoint(35.8978, 50.8617))
        geoPoints.add(GeoPoint(35.9002, 50.8582))
        geoPoints.add(GeoPoint(35.9035, 50.8519))
        geoPoints.add(GeoPoint(35.9087, 50.8380))
        geoPoints.add(GeoPoint(35.9120, 50.8294))
        geoPoints.add(GeoPoint(35.9134, 50.8239))
        geoPoints.add(GeoPoint(35.9141, 50.8202))
        geoPoints.add(GeoPoint(35.9130, 50.8201))
        geoPoints.add(GeoPoint(35.91116, 50.81994))
        geoPoints.add(GeoPoint(35.90948, 50.81978))
        geoPoints.add(GeoPoint(35.90700, 50.81954))
        geoPoints.add(GeoPoint(35.90561, 50.81941))
        return geoPoints.toList()
    }


}