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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.loadings.LoadingManager
import com.zar.core.tools.manager.DialogManager
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.other.ShowImageModel
import com.zarholding.zar.model.response.trip.TripModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.utility.signalr.RemoteSignalREmitter
import com.zarholding.zar.utility.signalr.SignalRListener
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.ShowImageDialog
import com.zarholding.zar.view.recycler.adapter.MyServiceAdapter
import com.zarholding.zar.view.recycler.adapter.ServiceAdapter
import com.zarholding.zar.view.adapter.SpinnerStringAdapter
import com.zarholding.zar.view.dialog.ConfirmDialog
import com.zarholding.zar.view.recycler.holder.MyServiceHolder
import com.zarholding.zar.view.recycler.holder.ServiceHolder
import com.zarholding.zar.viewmodel.BusServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import zar.R
import zar.databinding.FragmentServiceBinding
import javax.inject.Inject


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class BusServiceFragment : Fragment(){

    @Inject
    lateinit var loadingManager : LoadingManager

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    private val busServiceViewModel : BusServiceViewModel by viewModels()


    private lateinit var osmManager: OsmManager

    private var markerCar: Marker? = null
    private var job : Job? = null
    private var signalRListener: SignalRListener? = null
    private var moveMap = false
    private var tripId = 0
    private var stationId = 0
    private var selectedTripType = TripSelect.ALL

    @Inject
    lateinit var themeManagers: ThemeManager

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
        selectedTripType = TripSelect.ALL
        observeTripModelLiveData()
        observeErrorLiveDate()
        requestGetAllTrips()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated



    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
        loadingManager.stopLoadingRecycler()
    }
    //---------------------------------------------------------------------------------------------- showMessage



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


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        busServiceViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            showMessage(it.message)
            when(it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()
                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate



    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        loadingManager.setRecyclerLoading(
            binding.recyclerViewService,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- selectMyService
    private fun selectMyService() {
        osmManager.clearOverlays()
        busServiceViewModel.getMyTripList()?.let {
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            if (it.isNotEmpty())
                setMyServiceAdapter(it)
            else {
                showMessage(getString(R.string.userServiceIsEmpty))
                selectListAllServices()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- selectMyService


    //---------------------------------------------------------------------------------------------- selectListAllServices
    private fun selectListAllServices() {
        osmManager.clearOverlays()
        busServiceViewModel.getAllTripList()?.let {
            binding.textViewListService.setBackgroundResource(R.drawable.drawable_trip_select_button)
            binding.textViewMyService.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
            setServiceAdapter(it)
        }
    }
    //---------------------------------------------------------------------------------------------- selectListAllServices



    //---------------------------------------------------------------------------------------------- observeTripModelLiveData
    private fun observeTripModelLiveData() {
        busServiceViewModel.tripModelLiveData.observe(viewLifecycleOwner) {
            when (selectedTripType) {
                TripSelect.ALL -> selectListAllServices()
                TripSelect.MY -> selectMyService()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeTripModelLiveData



    //---------------------------------------------------------------------------------------------- requestGetAllTrips
    private fun requestGetAllTrips() {
        startLoading()
        busServiceViewModel.requestGetAllTrips()
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

            override fun showImage(item: ShowImageModel) {
                ShowImageDialog(requireContext(), item).show()
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

            override fun showImage(item: ShowImageModel) {
                ShowImageDialog(requireContext(), item).show()
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
                showDialogConfirmRegisterStation(
                    item.commuteTripName,
                    item.stations[spinner.selectedItemPosition].stationName,
                    item.id,
                    item.stations[spinner.selectedItemPosition].id)
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    //---------------------------------------------------------------------------------------------- showDialogRegisterStation


    //---------------------------------------------------------------------------------------------- showDialogConfirmRegisterStation
    private fun showDialogConfirmRegisterStation(
        tripName: String?,
        stationName: String?,
        tripId: Int,
        stationId: Int) {
        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                requestRegisterStation(tripId, stationId)
            }
        }
        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.ADD,
            getString(R.string.confirmForRegisterStation, stationName, tripName),
            click
        ).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogConfirmRegisterStation



    //---------------------------------------------------------------------------------------------- requestRegisterStation
    private fun requestRegisterStation(tripId: Int, stationId: Int) {
        startLoading()
        selectedTripType = TripSelect.MY
        busServiceViewModel.requestRegisterStation(tripId, stationId)
    }
    //---------------------------------------------------------------------------------------------- requestRegisterStation


    //---------------------------------------------------------------------------------------------- showDialogDeleteRegisterStation
    private fun showDialogDeleteRegisterStation(item: TripModel) {
        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                requestDeleteRegisterStation(item.myStationTripId)
            }
        }
        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.DELETE,
            getString(R.string.confirmForDelete, item.myStationName),
            click
        ).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogDeleteRegisterStation


    //---------------------------------------------------------------------------------------------- requestDeleteRegisterStation
    private fun requestDeleteRegisterStation(stationId: Int) {
        startLoading()
        selectedTripType = TripSelect.MY
        busServiceViewModel.requestDeleteRegisteredStation(stationId)
    }
    //---------------------------------------------------------------------------------------------- requestDeleteRegisterStation


    //---------------------------------------------------------------------------------------------- drawRoadOnMap
    private fun drawRoadOnMap(item: TripModel, tripSelect: TripSelect) {
        if (binding.textViewLoading.visibility == View.VISIBLE) {
            val snack = Snackbar.make(binding.constraintLayoutParent,
                getString(R.string.bePatientToLoadMap), 5 * 1000)
            snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
            snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
            snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
            snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
            snack.show()
            return
        }
        binding.textViewLoading.visibility = View.VISIBLE
        tripId = item.id
        stationId = item.myStationTripId

        job = CoroutineScope(IO).launch {

            CoroutineScope(IO).launch {
                delay(300)
                if (!item.tripPoints.isNullOrEmpty())
                    osmManager.drawPolyLine(item.tripPoints)
            }.join()

            CoroutineScope(IO).launch {
                delay(300)
                if (!item.stations.isNullOrEmpty())
                    osmManager.addStationMarker(item.stations)
            }.join()

            CoroutineScope(Main).launch {
                delay(300)
                binding.textViewLoading.visibility = View.GONE
                if (tripSelect == TripSelect.MY && item.myStationTripStatus == EnumTripStatus.Confirmed)
                    startSignalR()
            }.join()
        }
    }
    //---------------------------------------------------------------------------------------------- drawRoadOnMap



    //---------------------------------------------------------------------------------------------- startSignalR
    private fun startSignalR() {
        val remote = object : RemoteSignalREmitter {
            override fun onConnectToSignalR() {
                signalRListener?.let {
                    if (it.isConnection)
                        it.registerToGroupForService(tripId, stationId)
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
                busServiceViewModel.getToken()
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