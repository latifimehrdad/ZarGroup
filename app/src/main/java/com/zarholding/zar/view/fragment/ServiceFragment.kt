package com.zarholding.zar.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.view.activity.MainActivity
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
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
        init()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //---------------------------------------------------------------------------------------------- onError



    //---------------------------------------------------------------------------------------------- init
    private fun init() {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.minZoomLevel = 10.0
        binding.mapView.maxZoomLevel = 20.0
        val mapController: IMapController = binding.mapView.controller
        mapController.setZoom(17.0)
        val startPoint = GeoPoint(35.840378, 51.016217)
        mapController.setCenter(startPoint)
//        binding.mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS) // dark
        binding.mapView.onResume()


        val line = Polyline(binding.mapView, true, false)
        line.setPoints(OsmManager().addPolyline())
        line.isGeodesic = true
        line.setColor(resources.getColor(R.color.ML_PolyLine))
        line.setWidth(25.0f)
        line.outlinePaint.strokeCap = Paint.Cap.ROUND
        binding.mapView.overlayManager.add(line)
    }
    //---------------------------------------------------------------------------------------------- init



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}