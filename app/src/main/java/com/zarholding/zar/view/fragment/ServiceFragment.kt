package com.zarholding.zar.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.model.response.MyServiceModel
import com.zarholding.zar.model.response.ServiceModel
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.recycler.adapter.MyServiceAdapter
import com.zarholding.zar.view.recycler.adapter.ServiceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import zar.R
import zar.databinding.FragmentServiceBinding


/**
 * Created by m-latifi on 11/19/2022.
 */

class ServiceFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!


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
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //---------------------------------------------------------------------------------------------- onError



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.textViewMyService.setOnClickListener { selectMyService() }

        binding.textViewListService.setOnClickListener { selectListOfServices() }
    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- selectMyService
    private fun selectMyService() {
        binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        initMyService()
    }
    //---------------------------------------------------------------------------------------------- selectMyService




    //---------------------------------------------------------------------------------------------- selectListOfServices
    private fun selectListOfServices() {
        binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        initListOfService()
    }
    //---------------------------------------------------------------------------------------------- selectListOfServices



    //---------------------------------------------------------------------------------------------- initListOfService
    private fun initListOfService() {
        val service: MutableList<ServiceModel> = mutableListOf()
        service.add(ServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png", "اتوبوس کد 00512 - آقای فرحی", "مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(ServiceModel("https://fs.noorgram.ir/xen/2022/02/3098_5479a701607915b9791e6c23f3e07cb6.png","اتوبوس کد 00512 - آقای فرحی", "مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(ServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png","اتوبوس کد 00512 - آقای فرحی", "مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(ServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png","اتوبوس کد 00512 - آقای فرحی", "مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(ServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png","اتوبوس کد 00512 - آقای فرحی", "مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        setServiceAdapter(service)
    }
    //---------------------------------------------------------------------------------------------- initListOfService



    //---------------------------------------------------------------------------------------------- setServiceAdapter
    private fun setServiceAdapter(services: MutableList<ServiceModel>) {
        val adapter = ServiceAdapter(services)

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



    //---------------------------------------------------------------------------------------------- initMyService
    private fun initMyService() {
        val service: MutableList<MyServiceModel> = mutableListOf()
        service.add(MyServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png", "اتوبوس کد 00512 - آقای فرحی", "ایسنگاه من : یه راه گوهردشت","مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(MyServiceModel("https://fs.noorgram.ir/xen/2022/02/3098_5479a701607915b9791e6c23f3e07cb6.png","اتوبوس کد 00512 - آقای فرحی", "ایسنگاه من : یه راه گوهردشت","مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(MyServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png","اتوبوس کد 00512 - آقای فرحی", "ایسنگاه من : یه راه گوهردشت","مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(MyServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png","اتوبوس کد 00512 - آقای فرحی", "ایسنگاه من : یه راه گوهردشت","مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        service.add(MyServiceModel("https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png","اتوبوس کد 00512 - آقای فرحی", "ایسنگاه من : یه راه گوهردشت","مبدا : سه راه گوهردشت/مقصد: کارخانه زرماکارون","ایستگاه ها : سه راه گوهردشت - میان جاده - چهارراه گلشهر - چهارراه گلزار"))
        setMyServiceAdapter(service)
    }
    //---------------------------------------------------------------------------------------------- initMyService



    //---------------------------------------------------------------------------------------------- setMyServiceAdapter
    private fun setMyServiceAdapter(services: MutableList<MyServiceModel>) {
        val adapter = MyServiceAdapter(services)

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

        val points = OsmManager().addPolyline()
        val box = OsmManager().getBoundingBoxFromPoints(points)
        val line = Polyline(binding.mapView, true, false)
        line.setPoints(points)
        line.outlinePaint.color = resources.getColor(R.color.polyLineColor, requireContext().theme)
        line.outlinePaint.strokeWidth = 18.0f

        line.isGeodesic = true
        line.outlinePaint.strokeCap = Paint.Cap.ROUND
        binding.mapView.overlayManager.add(line)
        CoroutineScope(IO).launch {
            delay(1500)
            withContext(Main) {
                binding.mapView.zoomToBoundingBox(box, true)
            }
        }


    }
    //---------------------------------------------------------------------------------------------- initMap



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}