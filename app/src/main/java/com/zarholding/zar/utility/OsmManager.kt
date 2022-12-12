package com.zarholding.zar.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.TextView
import com.zarholding.zar.model.response.trip.TripPointModel
import com.zarholding.zardriver.model.response.TripStationModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import zar.R
import kotlin.math.*


/**
 * Created by m-latifi on 11/19/2022.
 */


class OsmManager(private val map: MapView) {

    //---------------------------------------------------------------------------------------------- mapInitialize
    fun mapInitialize(theme: Int) {
        Configuration.getInstance().userAgentValue = map.context.packageName
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)
        map.minZoomLevel = 9.0
        map.maxZoomLevel = 21.0
        moveCamera(GeoPoint(35.90576170621037, 50.819428313684675))
        if (theme == android.content.res.Configuration.UI_MODE_NIGHT_YES)
            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS) // dark
        map.onResume()
    }
    //---------------------------------------------------------------------------------------------- mapInitialize


    //---------------------------------------------------------------------------------------------- removeMarker
    fun removeMarkerAndMove(marker: Marker) {
        map.overlays.remove(marker)
        moveCamera(marker.position)
    }
    //---------------------------------------------------------------------------------------------- removeMarker


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
    fun getGeoPoints(tripPoints: List<TripPointModel>): ArrayList<GeoPoint> {
        val geoPoints = ArrayList<GeoPoint>()
        for (item in tripPoints)
            geoPoints.add(GeoPoint(item.lat.toDouble(), item.long.toDouble()))
        return geoPoints
    }
    //---------------------------------------------------------------------------------------------- getGeoPoints


    //---------------------------------------------------------------------------------------------- clearOverlays
    fun clearOverlays() {
        map.overlays.clear()
        map.invalidate()
    }
    //---------------------------------------------------------------------------------------------- clearOverlays


    //---------------------------------------------------------------------------------------------- drawPolyLine
    suspend fun drawPolyLine(ripPoints: List<TripPointModel>) {
        clearOverlays()
        val points = getGeoPoints(ripPoints)
        val roadManager: RoadManager = OSRMRoadManager(map.context, "")
        val road = roadManager.getRoad(points)
        val poly: Polyline = RoadManager.buildRoadOverlay(road)
        val polyline = Polyline(map, true, false)
        polyline.setPoints(poly.actualPoints)
        polyline.outlinePaint?.color =
            map.context.resources.getColor(R.color.polyLineColor, map.context.theme)
        polyline.outlinePaint?.strokeWidth = 18.0f
        polyline.isGeodesic = true
        polyline.outlinePaint?.strokeCap = Paint.Cap.ROUND
        withContext(Dispatchers.Main) {
            map.overlays.add(polyline)
            val box = getBoundingBoxFromPoints(points)
            map.zoomToBoundingBox(box, true)
        }
    }
    //---------------------------------------------------------------------------------------------- drawPolyLine


    //---------------------------------------------------------------------------------------------- addStationMarker
    suspend fun addStationMarker(stations: List<TripStationModel>, view: View) {
        withContext(Main) {
            addStartStationMarker(stations[0])
            addEndStationMarker(stations[stations.size - 1])

            val iconStation = createMarkerIconDrawable(
                Size(70, 100),
                R.drawable.icon_station_marker
            )
            for (i in 1..stations.size - 2) {
                val position =
                    GeoPoint(stations[i].stationLat.toDouble(), stations[i].sationLong.toDouble())
                val infoWindows = getInfoWindows(
                    stations[i].stationName,
                    map.context.resources.getString(R.string.attendanceTime, stations[i].arriveTime)
                )
                addMarker(iconStation, position, infoWindows)
            }
            view.visibility = View.GONE
        }
    }
    //---------------------------------------------------------------------------------------------- addStationMarker


    //---------------------------------------------------------------------------------------------- addStartStationMarker
    private fun addStartStationMarker(station: TripStationModel) {
        val start = GeoPoint(
            station.stationLat.toDouble(),
            station.sationLong.toDouble()
        )
        val startIcon = createMarkerIconDrawable(
            Size(100, 97),
            R.drawable.icon_start_marker
        )
        val infoWindows = getInfoWindows(
            station.stationName,
            map.context.resources.getString(R.string.attendanceTime, station.arriveTime)
        )
        addMarker(startIcon, start, infoWindows)
    }
    //---------------------------------------------------------------------------------------------- addStartStationMarker


    //---------------------------------------------------------------------------------------------- addEndStationMarker
    private fun addEndStationMarker(station: TripStationModel) {
        val end = GeoPoint(
            station.stationLat.toDouble(),
            station.sationLong.toDouble()
        )
        val endIcon = createMarkerIconDrawable(
            Size(100, 97),
            R.drawable.icon_end_marker
        )
        val infoWindows = getInfoWindows(
            station.stationName,
            map.context.resources.getString(R.string.attendanceTime, station.arriveTime)
        )
        addMarker(endIcon, end, infoWindows)
    }
    //---------------------------------------------------------------------------------------------- addEndStationMarker


    //---------------------------------------------------------------------------------------------- addMarker
    fun addMarker(icon: Drawable, position: GeoPoint, infoWindows: MarkerInfoWindow?): Marker {
        val marker = Marker(map, map.context)
        marker.icon = icon
        marker.position = position
        marker.setInfoWindow(infoWindows)
        map.overlayManager.add(marker)
        map.invalidate()
        return marker
    }
    //---------------------------------------------------------------------------------------------- addMarker


    //---------------------------------------------------------------------------------------------- getInfoWindows
    fun getInfoWindows(title: String?, content: String?): MarkerInfoWindow {
        val infoWindow: MarkerInfoWindow =
            object : MarkerInfoWindow(R.layout.layout_marker_info, map) {
                override fun onOpen(item: Any) {
                    val titleTextView = mView.findViewById<TextView>(R.id.textViewTitle)
                    titleTextView.text = title
                    val textViewContent = view.findViewById<TextView>(R.id.textViewContent)
                    textViewContent.text = content
                }

                override fun onClose() {}
            }
        return infoWindow
    }
    //---------------------------------------------------------------------------------------------- getInfoWindows


    //---------------------------------------------------------------------------------------------- createMarkerIconDrawable
    fun createMarkerIconDrawable(size: Size, icon: Int): Drawable {
        val iconStart = Bitmap
            .createScaledBitmap(
                BitmapFactory.decodeResource(map.context.resources, icon),
                size.width,
                size.height,
                true
            )
        return BitmapDrawable(map.context.resources, iconStart)
    }
    //---------------------------------------------------------------------------------------------- createMarkerIconDrawable


    //---------------------------------------------------------------------------------------------- moveCamera
    fun moveCamera(geoPoint: GeoPoint) {
        val mapController: IMapController = map.controller
        mapController.animateTo(geoPoint, 18.0, 1000)
    }
    //---------------------------------------------------------------------------------------------- moveCamera



    //---------------------------------------------------------------------------------------------- moveCameraZoomUp
    fun moveCameraZoomUp(geoPoint: GeoPoint) {
        val mapController: IMapController = map.controller
        val point = GeoPoint(geoPoint.latitude + 0.002, geoPoint.longitude + 0.002)
        mapController.animateTo(point, 16.0, 1000)
    }
    //---------------------------------------------------------------------------------------------- moveCameraZoomUp


/*
    //---------------------------------------------------------------------------------------------- drawArrowOnPolyline
    private fun drawArrowOnPolyline(points: List<GeoPoint>) {
        for (i in 0..points.size step 2)
            if (i + 1 < points.size) {
                val from = points[i]
                val to = points[i + 1]
                val distance = OsmManager().measureDistance(from, to)
                if (distance > 80) {
                    val angel = OsmManager().getBearing(from, to)
                    val marker = Marker(binding.mapView, context)
                    val center = GeoPoint.fromCenterBetween(from, to)
                    marker.icon = resources.getDrawableForDensity(
                        R.drawable.ic_icon_map_location_arrow,
                        2,
                        requireContext().theme
                    )
                    marker.position = center
                    marker.rotation = 360 - angel.toFloat()
                    binding.mapView.overlayManager.add(marker)
                }
            }
        binding.mapView.invalidate()
    }
    //---------------------------------------------------------------------------------------------- drawArrowOnPolyline
*/


}