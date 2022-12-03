package com.zarholding.zar.view.fragment

import android.annotation.SuppressLint
import android.app.Notification
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.*
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.utility.ThemeManagers
import com.zarholding.zar.utility.signalr.RemoteSignalREmitter
import com.zarholding.zar.utility.signalr.SignalRListener
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.recycler.adapter.MyServiceAdapter
import com.zarholding.zar.view.recycler.adapter.ServiceAdapter
import com.zarholding.zar.view.recycler.adapter.SpinnerStringAdapter
import com.zarholding.zar.view.recycler.holder.MyServiceHolder
import com.zarholding.zar.view.recycler.holder.ServiceHolder
import com.zarholding.zar.viewmodel.TokenViewModel
import com.zarholding.zar.viewmodel.TripViewModel
import com.zarholding.zardriver.model.response.TripStationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import zar.R
import zar.databinding.FragmentServiceBinding
import javax.inject.Inject


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class ServiceFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    private val tripViewModel: TripViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private val loadingManager = LoadingManager()
    private lateinit var osmManager: OsmManager
    private var tripList: List<TripModel>? = null
    private var markerCar: Marker? = null
    private var job : Job? = null
    private var signalRListener: SignalRListener? = null
    private var moveMap = false
    private var tripId = 0
    private var stationId = 0

    @Inject
    lateinit var themeManagers: ThemeManagers

    //---------------------------------------------------------------------------------------------- ShowTrip
    private enum class TripSelect {
        ALL,
        MY
    }
    //---------------------------------------------------------------------------------------------- ShowTrip


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
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        setListener()
        requestGetAllTrips(TripSelect.ALL)
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
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
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
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
    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        binding.textViewMyService.setOnClickListener { selectMyService() }
        binding.textViewListService.setOnClickListener { selectListAllServices() }
        binding.mapView.setOnTouchListener { _, _ ->
            moveMap = false
            false
        }

        binding.textViewService.setOnClickListener {
            showNotificationPreviousStationReached()
        }
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
        osmManager.clearOverlays()
        tripList?.let {
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            val myService = tripList!!.filter { it.myStationTripId != 0 }
            if (myService.isNotEmpty())
                setMyServiceAdapter(myService)
            else
                selectListAllServices()
        }
    }
    //---------------------------------------------------------------------------------------------- selectMyService


    //---------------------------------------------------------------------------------------------- selectListAllServices
    private fun selectListAllServices() {
        osmManager.clearOverlays()
        tripList?.let {
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            val myService = tripList!!.filter { it.myStationTripId == 0 }
            setServiceAdapter(myService)
        }
    }
    //---------------------------------------------------------------------------------------------- selectListAllServices


    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    private fun requestGetAllTrips(tripSelect: TripSelect) {
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
                            when (tripSelect) {
                                TripSelect.ALL -> selectListAllServices()
                                TripSelect.MY -> selectMyService()
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
                drawRoadOnMap(item, TripSelect.ALL)
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
                drawRoadOnMap(item, TripSelect.MY)
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
        item.stations?.let {
            val adapter = SpinnerStringAdapter(it)
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
                        requestGetAllTrips(TripSelect.MY)
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
        val textViewTitle = dialog.findViewById<TextView>(R.id.textViewTitle)
        textViewTitle.text = resources.getString(R.string.confirmForDelete, item.myStationName)
        val imageClose = dialog.findViewById<ImageView>(R.id.imageViewClose)
        imageClose.setOnClickListener { dialog.dismiss() }
        val buttonYes = dialog.findViewById<MaterialButton>(R.id.buttonYes)
        buttonYes.setOnClickListener {
            requestDeleteRegisterStation(item.myStationTripId)
            dialog.dismiss()
        }
        val buttonNo = dialog.findViewById<MaterialButton>(R.id.buttonNo)
        buttonNo.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    //---------------------------------------------------------------------------------------------- showDialogDeleteRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisterStation
    private fun requestDeleteRegisterStation(stationId: Int) {
        startLoading(binding.nestedScrollView)
        tripViewModel.requestDeleteRegisteredStation(stationId, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingView()
                response?.let {
                    onError(EnumErrorType.UNKNOWN, it.message)
                    if (!it.hasError)
                        requestGetAllTrips(TripSelect.MY)
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisterStation


    //---------------------------------------------------------------------------------------------- drawRoadOnMap
    private fun drawRoadOnMap(item: TripModel, tripSelect: TripSelect) {

        tripId = item.id
        stationId = item.myStationTripId


        item.tripPoints?.let {
            job = CoroutineScope(IO).launch {
                osmManager.drawPolyLine(it)
            }
        }

        CoroutineScope(Main).launch {
            binding.textViewLoading.visibility = View.VISIBLE
            delay(300)
            item.stations?.let { station ->
                CoroutineScope(IO).launch {
                    job?.join()
                    osmManager.addStationMarker(station, binding.textViewLoading)
                }
            }
        }


        if (tripSelect == TripSelect.MY)
            startSignalR()
    }
    //---------------------------------------------------------------------------------------------- drawRoadOnMap









    //---------------------------------------------------------------------------------------------- startSignalR
    private fun startSignalR() {
        val remote = object : RemoteSignalREmitter {
            override fun onConnectToSignalR() {
                signalRListener?.let {
                    if (it.isConnection)
                        it.registerToGroup(tripId, stationId)
                }
            }

            override fun onErrorConnectToSignalR() {

            }

            override fun onReConnectToSignalR() {

            }

            override fun onGetPoint(lat: String, lng: String) {
                CoroutineScope(IO).launch {
                    withContext(Main) {
                        moveCarMarker(GeoPoint(lat.toDouble(), lng.toDouble()))
                    }
                }
            }

            override fun onPreviousStationReached(message: String) {
                showNotificationPreviousStationReached()
            }
        }

        CoroutineScope(IO).launch {
            delay(2000)
            signalRListener = SignalRListener.getInstance(
                remote,
                tokenViewModel.getToken()
            )
            if (!signalRListener!!.isConnection)
                signalRListener!!.startConnection()
            moveMap = true
        }
    }
    //---------------------------------------------------------------------------------------------- startSignalR


    //---------------------------------------------------------------------------------------------- moveCarMarker
    private fun moveCarMarker(position: GeoPoint) {
        markerCar?.let {
            it.position = position
        } ?: run {
            val iconBus = osmManager
                .createMarkerIconDrawable(Size(70, 100), R.drawable.icon_bus_marker)
            markerCar = osmManager.addMarker(iconBus, position, null)
        }

        if (moveMap)
            osmManager.moveCamera(position)
        else
            binding.mapView.invalidate()
    }
    //---------------------------------------------------------------------------------------------- moveCarMarker


    //---------------------------------------------------------------------------------------------- showNotificationPreviousStationReached
    private fun showNotificationPreviousStationReached() {
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifyManager = NotificationManagerCompat.from(requireContext())
        val notificationBuilder = NotificationCompat
            .Builder(requireContext(), CompanionValues.channelId)
        val notification = notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(
                requireContext()
                    .resources.getString(R.string.messagePreviousStationReached)
            )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVibrate(vibrate)
            .setSound(alarmSound)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        notifyManager.notify(7126, notification.build())
    }
    //---------------------------------------------------------------------------------------------- showNotificationPreviousStationReached


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        signalRListener?.let {
            if (it.isConnection)
                it.stopConnection()
        }
        binding.mapView.onPause()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}