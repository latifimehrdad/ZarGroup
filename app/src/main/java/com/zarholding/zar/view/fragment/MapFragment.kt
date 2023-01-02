package com.zarholding.zar.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.model.response.trip.TripPointModel
import com.zarholding.zar.utility.OsmManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import zar.R
import zar.databinding.FragmentMapBinding
import java.util.*
import javax.inject.Inject
import kotlin.let
import kotlin.run


@AndroidEntryPoint
class MapFragment : Fragment() {

    @Inject
    lateinit var themeManagers: ThemeManager

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var osmManager: OsmManager
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    var latPrimary: Double = 0.0
    var lngPrimary: Double = 0.0

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        binding.textViewLoading.visibility = View.VISIBLE
        CoroutineScope(IO).launch {
            showPointOnMap()
        }

        binding.materialButtonChooseNavigationApp.setOnClickListener {
            binding.materialButtonChooseNavigationApp.visibility = View.GONE
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    String.format(
                        Locale.US,
                        "geo:%.8f,%.8f", latPrimary, lngPrimary
                    )
                )
            )
            startActivity(intent)
        }

    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showPointOnMap
    private suspend fun showPointOnMap() {
        arguments?.let {
            val latOrigin = it.getDouble("latOrigin")
            val lngOrigin = it.getDouble("lngOrigin")
            val latDestination = it.getDouble("latDestination", 0.0)
            val lngDestination = it.getDouble("lngDestination", 0.0)

            val pointOrigin = GeoPoint(latOrigin, lngOrigin)
            var pointDestination: GeoPoint? = null
            if (latDestination != 0.0)
                pointDestination = GeoPoint(latDestination, lngDestination)

            val points = listOf(
                TripPointModel(latOrigin.toFloat(), lngOrigin.toFloat()),
                TripPointModel(latDestination.toFloat(), lngDestination.toFloat())
            )
            osmManager.drawPolyLine(points)

            val iconOrigin = osmManager.createMarkerIconDrawable(
                Size(159, 94),
                R.drawable.icon_marker_origin
            )
            originMarker = osmManager.addMarker(iconOrigin, pointOrigin, null)
            originMarker!!.setOnMarkerClickListener { marker, _ ->
                latPrimary = marker.position.latitude
                lngPrimary = marker.position.longitude
                binding.materialButtonChooseNavigationApp.visibility = View.VISIBLE
                true
            }

            pointDestination?.let { point ->
                val iconDestination = osmManager.createMarkerIconDrawable(
                    Size(159, 94),
                    R.drawable.icon_marker_destination
                )
                destinationMarker = osmManager.addMarker(iconDestination, point, null)
                destinationMarker!!.setOnMarkerClickListener { marker, _ ->
                    latPrimary = marker.position.latitude
                    lngPrimary = marker.position.longitude
                    binding.materialButtonChooseNavigationApp.visibility = View.VISIBLE
                    true
                }
                withContext(Main) {
                    binding.textViewLoading.visibility = View.GONE
                }

            } ?: run {
                osmManager.moveCamera(pointOrigin, 1L)
            }

        } ?: run {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
    //---------------------------------------------------------------------------------------------- showPointOnMap

}