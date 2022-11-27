package com.zarholding.zar.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.DialogManager
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.recycler.adapter.MyServiceAdapter
import com.zarholding.zar.view.recycler.adapter.ServiceAdapter
import com.zarholding.zar.view.recycler.holder.MyServiceHolder
import com.zarholding.zar.view.recycler.holder.ServiceHolder
import com.zarholding.zar.viewmodel.TokenViewModel
import com.zarholding.zar.viewmodel.TripViewModel
import com.zarholding.zardriver.model.response.TripStationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import zar.R
import zar.databinding.FragmentServiceBinding


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class ServiceFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private var polyline: Polyline? = null

    private val tripViewModel: TripViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private var tripList: List<TripModel>? = null


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setListener()
        initMap()
//        requestGetTrips()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 10 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 10 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
        requireActivity().onBackPressed()
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.textViewMyService.setOnClickListener { selectMyService() }

        binding.textViewListService.setOnClickListener { selectListOfServices() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- requestGetTrips
    private fun requestGetTrips() {
        tripViewModel.requestGetTrips(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                response?.let {
                    if (it.hasError)
                        onError(EnumErrorType.UNKNOWN, it.message)
                    else
                        it.data?.let { trips ->
                            tripList = trips
                            selectListOfServices()
                        }
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetTrips


    //---------------------------------------------------------------------------------------------- selectMyService
    private fun selectMyService() {
        tripList?.let {
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            val myService = tripList!!.filter { it.myStationTripId != 0 }
            setMyServiceAdapter(myService)
        }
        binding.mapView.overlays.clear()
        binding.mapView.invalidate()
    }
    //---------------------------------------------------------------------------------------------- selectMyService


    //---------------------------------------------------------------------------------------------- selectListOfServices
    private fun selectListOfServices() {
        tripList?.let {
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            val myService = tripList!!.filter { it.myStationTripId == 0 }
            setServiceAdapter(myService)
        }
        binding.mapView.overlays.clear()
        binding.mapView.invalidate()
    }
    //---------------------------------------------------------------------------------------------- selectListOfServices


    //---------------------------------------------------------------------------------------------- setServiceAdapter
    private fun setServiceAdapter(tripList: List<TripModel>) {
        val click = object : ServiceHolder.Click {
            override fun serviceClick(item: TripModel) {
                initTripAndStation(item)
            }

            override fun registerStation(item: TripModel) {
                showDialogRegisterStation(item)
            }
        }
        val adapter = ServiceAdapter(tripList, click)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        linearLayoutManager.reverseLayout = true

        binding.recyclerViewService.layoutManager = linearLayoutManager
        binding.recyclerViewService.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setServiceAdapter


    //---------------------------------------------------------------------------------------------- setMyServiceAdapter
    private fun setMyServiceAdapter(tripList: List<TripModel>) {
        val click = object : MyServiceHolder.Click {
            override fun serviceClick(item: TripModel) {
                initTripAndStation(item)
            }
        }
        val adapter = MyServiceAdapter(tripList, click)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        linearLayoutManager.reverseLayout = true

        binding.recyclerViewService.layoutManager = linearLayoutManager
        binding.recyclerViewService.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setMyServiceAdapter



    //---------------------------------------------------------------------------------------------- showDialogRegisterStation
    private fun showDialogRegisterStation(item : TripModel) {
        val station = item.stations!!.map { it.stationName }
        val adapter = ArrayAdapter(
            binding.root.context,
            android.R.layout.simple_spinner_item,
            station
        )
        val dialog = DialogManager()
            .createDialogHeightMatchParent(requireContext(), R.layout.dialog_register_station)
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerStations)
        spinner.adapter = adapter
        val imageClose = dialog.findViewById<ImageView>(R.id.imageViewClose)
        imageClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    //---------------------------------------------------------------------------------------------- showDialogRegisterStation



    //---------------------------------------------------------------------------------------------- initMap
    private fun initMap() {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.minZoomLevel = 9.0
        binding.mapView.maxZoomLevel = 21.0
        val mapController: IMapController = binding.mapView.controller
        mapController.setZoom(17.0)
        val startPoint = GeoPoint(35.840378, 51.016217)
        mapController.setCenter(startPoint)
//        binding.mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS) // dark
        binding.mapView.onResume()

        val geoPoints = arrayListOf<GeoPoint>()
        geoPoints.add(GeoPoint(35.83952, 51.01509))
        geoPoints.add(GeoPoint(35.84014, 51.01408))
        val roadManager: RoadManager = OSRMRoadManager(context,"")
        CoroutineScope(IO).launch {
            val road = roadManager.getRoad(geoPoints)
            val poly: Polyline = RoadManager.buildRoadOverlay(road)
            polyline = Polyline(binding.mapView, true, false)
            polyline!!.setPoints(poly.actualPoints)
            polyline!!.outlinePaint?.color =
                resources.getColor(R.color.polyLineColor, requireContext().theme)
            polyline!!.outlinePaint?.strokeWidth = 20.0f
            polyline!!.isGeodesic = true
            polyline!!.outlinePaint?.strokeCap = Paint.Cap.ROUND
            withContext(Main) {
                binding.mapView.overlays.add(polyline)
                binding.mapView.invalidate();
            }
        }


    }
    //---------------------------------------------------------------------------------------------- initMap


    //---------------------------------------------------------------------------------------------- initTripAndStation
    private fun initTripAndStation(item: TripModel) {

        item.tripPoints?.let {
            val points = OsmManager().getGeoPoints(it)
            drawPolylineOnMap(points)
        }
        item.stations?.let {
            addStationMarker(it)
        }
    }
    //---------------------------------------------------------------------------------------------- initTripAndStation



    //---------------------------------------------------------------------------------------------- drawPolylineOnMap
    private fun drawPolylineOnMap(points: List<GeoPoint>) {
        polyline = Polyline(binding.mapView, true, false)
        polyline?.setPoints(points)
        polyline?.outlinePaint?.color =
            resources.getColor(R.color.polyLineColor, requireContext().theme)
        polyline?.outlinePaint?.strokeWidth = 20.0f
        polyline?.isGeodesic = true
        polyline?.outlinePaint?.strokeCap = Paint.Cap.ROUND
        binding.mapView.overlayManager.add(polyline)
        binding.mapView.invalidate()
        boundBoxMap(points)
    }
    //---------------------------------------------------------------------------------------------- drawPolylineOnMap


    //---------------------------------------------------------------------------------------------- boundBoxMap
    private fun boundBoxMap(points: List<GeoPoint>) {
        val box = OsmManager().getBoundingBoxFromPoints(points)
        CoroutineScope(IO).launch {
            delay(1000)
            withContext(Main) {
                binding.mapView.zoomToBoundingBox(box, true)
                drawArrowOnPolyline(points)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- boundBoxMap


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


    //---------------------------------------------------------------------------------------------- addStationMarker
    private fun addStationMarker(stations: List<TripStationModel>) {
        for (item in stations) {
            val station = GeoPoint(item.stationLat.toDouble(), item.sationLong.toDouble())
            val marker = Marker(binding.mapView, context)
            marker.icon = resources.getDrawableForDensity(
                R.drawable.ic_map_marker,
                2,
                requireContext().theme
            )
            marker.position = station
            binding.mapView.overlayManager.add(marker)
        }
        binding.mapView.invalidate()
    }
    //---------------------------------------------------------------------------------------------- addStationMarker


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}