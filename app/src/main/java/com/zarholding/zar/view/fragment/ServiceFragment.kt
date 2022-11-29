package com.zarholding.zar.view.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.loadings.LoadingManager
import com.zar.core.tools.manager.DialogManager
import com.zarholding.zar.model.request.RequestRegisterStationModel
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
    private val loadingManager = LoadingManager()
    private var tripList: List<TripModel>? = null

    private enum class ShowTrip {
        ALL,
        MY
    }

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
        requestGetAllTrips(ShowTrip.ALL)
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
        loadingManager.stopLoadingView()
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
        loadingManager.stopLoadingView()
        requireActivity().onBackPressed()
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.textViewMyService.setOnClickListener { selectMyService() }

        binding.textViewListService.setOnClickListener { selectListAllServices() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading(view: View) {
        loadingManager.setViewLoading(
            view,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow
        )
    }
    //---------------------------------------------------------------------------------------------- startLoading


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


    //---------------------------------------------------------------------------------------------- selectListAllServices
    private fun selectListAllServices() {
        tripList?.let {
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            val myService = tripList!!.filter { it.myStationTripId == 0 }
            setServiceAdapter(myService)
        }
        binding.mapView.overlays.clear()
        binding.mapView.invalidate()
    }
    //---------------------------------------------------------------------------------------------- selectListAllServices


    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    private fun requestGetAllTrips(showTrip: ShowTrip) {
        startLoading(binding.nestedScrollView)
        tripViewModel.requestGetAllTrips(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingView()
                response?.let {
                    if (it.hasError)
                        onError(EnumErrorType.UNKNOWN, it.message)
                    else
                        it.data?.let { trips ->
                            tripList = trips
                            when (showTrip) {
                                ShowTrip.ALL -> selectListAllServices()
                                ShowTrip.MY -> selectMyService()
                            }
                        }
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetAllTrips


    //---------------------------------------------------------------------------------------------- setServiceAdapter
    private fun setServiceAdapter(tripList: List<TripModel>) {
        val click = object : ServiceHolder.Click {
            override fun serviceClick(item: TripModel) {
                drawPolylineOnMap(item)
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
                drawPolylineOnMap(item)
            }

            override fun deleteRegisterStation(item: TripModel) {
                showDialogDeleteRegisterStation(item)
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
    private fun showDialogRegisterStation(item: TripModel) {
        val station = item.stations!!.map { it.stationName }
        val adapter = ArrayAdapter(
            binding.root.context,
            android.R.layout.simple_spinner_item,
            station
        )
        val dialog = DialogManager().createDialogHeightWrapContent(
            requireContext(),
            R.layout.dialog_register_station,
            Gravity.BOTTOM,
            150
        )
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerStations)
        spinner.adapter = adapter
        val imageClose = dialog.findViewById<ImageView>(R.id.imageViewClose)
        imageClose.setOnClickListener { dialog.dismiss() }
        val buttonRegister = dialog.findViewById<MaterialButton>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            requestRegisterStation(item.id, item.stations[spinner.selectedItemPosition].id)
            dialog.dismiss()
        }
        dialog.show()
    }
    //---------------------------------------------------------------------------------------------- showDialogRegisterStation


    //---------------------------------------------------------------------------------------------- requestRegisterStation
    private fun requestRegisterStation(tripId: Int, stationId: Int) {
        startLoading(binding.nestedScrollView)
        val requestModel = RequestRegisterStationModel(tripId, stationId)
        tripViewModel.requestRegisterStation(requestModel, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingView()
                response?.let {
                    onError(EnumErrorType.UNKNOWN, it.message)
                    if (!it.hasError)
                        requestGetAllTrips(ShowTrip.MY)
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestRegisterStation




    //---------------------------------------------------------------------------------------------- showDialogDeleteRegisterStation
    private fun showDialogDeleteRegisterStation(item: TripModel) {
        val dialog = DialogManager().createDialogHeightWrapContent(
            requireContext(),
            R.layout.dialog_confirmation,
            Gravity.BOTTOM,
            150
        )
        val imageClose = dialog.findViewById<ImageView>(R.id.imageViewClose)
        imageClose.setOnClickListener { dialog.dismiss() }
        val buttonYes = dialog.findViewById<MaterialButton>(R.id.buttonYes)
        buttonYes.setOnClickListener {
            requestDeleteRegisterStation(item.id, item.stations[spinner.selectedItemPosition].id)
            dialog.dismiss()
        }
        val buttonNo = dialog.findViewById<MaterialButton>(R.id.buttonNo)
        buttonNo.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    //---------------------------------------------------------------------------------------------- showDialogDeleteRegisterStation



    //---------------------------------------------------------------------------------------------- requestDeleteRegisterStation
    private fun requestDeleteRegisterStation(tripId: Int, stationId: Int) {
        startLoading(binding.nestedScrollView)
        val requestModel = RequestRegisterStationModel(tripId, stationId)
        tripViewModel.requestRegisterStation(requestModel, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingView()
                response?.let {
                    onError(EnumErrorType.UNKNOWN, it.message)
                    if (!it.hasError)
                        requestGetAllTrips(ShowTrip.MY)
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisterStation




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
    }
    //---------------------------------------------------------------------------------------------- initMap


    //---------------------------------------------------------------------------------------------- drawPolylineOnMap
    private fun drawPolylineOnMap(item: TripModel) {
        item.tripPoints?.let {
            binding.cardViewMap
                .startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.alpha))
            val points = OsmManager().getGeoPoints(it)
            val roadManager: RoadManager = OSRMRoadManager(context, "")
            CoroutineScope(IO).launch {
                val road = roadManager.getRoad(points)
                val poly: Polyline = RoadManager.buildRoadOverlay(road)
                polyline = Polyline(binding.mapView, true, false)
                polyline!!.setPoints(poly.actualPoints)
                polyline!!.outlinePaint?.color =
                    resources.getColor(R.color.polyLineColor, requireContext().theme)
                polyline!!.outlinePaint?.strokeWidth = 18.0f
                polyline!!.isGeodesic = true
                polyline!!.outlinePaint?.strokeCap = Paint.Cap.ROUND
                delay(500)
                withContext(Main) {
                    binding.cardViewMap.clearAnimation()
                    binding.mapView.overlays.add(polyline)
                    binding.mapView.invalidate()
                    boundBoxMap(points)
                    item.stations?.let { stations ->
                        addStationMarker(stations)
                    }
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- drawPolylineOnMap


    //---------------------------------------------------------------------------------------------- boundBoxMap
    private fun boundBoxMap(points: List<GeoPoint>) {
        val box = OsmManager().getBoundingBoxFromPoints(points)
        CoroutineScope(IO).launch {
            delay(1000)
            withContext(Main) {
                binding.mapView.zoomToBoundingBox(box, true)
//                drawArrowOnPolyline(points)
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
        val start = GeoPoint(stations[0].stationLat.toDouble(), stations[0].sationLong.toDouble())
        val markerStart = Marker(binding.mapView, context)
        val iconStart = Bitmap
            .createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_start_marker),
                100,
                97,
                true
            )
        markerStart.icon = BitmapDrawable(resources, iconStart)
        markerStart.position = start
        binding.mapView.overlayManager.add(markerStart)

        val end = GeoPoint(
            stations[stations.size - 1].stationLat.toDouble(),
            stations[stations.size - 1].sationLong.toDouble()
        )
        val markerEnd = Marker(binding.mapView, context)
        val iconEnd = Bitmap
            .createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_end_marker),
                100,
                97,
                true
            )
        markerEnd.icon = BitmapDrawable(resources, iconEnd)
        markerEnd.position = end
        binding.mapView.overlayManager.add(markerEnd)

        val iconStation = Bitmap
            .createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.icon_station_marker),
                70,
                100,
                true
            )
        val icon = BitmapDrawable(resources, iconStation)
        for (i in 1..stations.size - 2) {
            val station =
                GeoPoint(stations[i].stationLat.toDouble(), stations[i].sationLong.toDouble())
            val marker = Marker(binding.mapView, context)
            marker.icon = icon
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