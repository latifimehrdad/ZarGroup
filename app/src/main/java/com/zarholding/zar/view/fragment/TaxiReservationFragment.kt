package com.zarholding.zar.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Size
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.extensions.toSolarDate
import com.zar.core.tools.loadings.LoadingManager
import com.zar.core.tools.manager.ThemeManager
import com.zar.core.view.picker.date.customviews.DateRangeCalendarView
import com.zar.core.view.picker.date.dialog.DatePickerDialog
import com.zar.core.view.picker.time.ZarTimePicker
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.enum.FavPlaceType
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.request.TaxiRequestModel
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.*
import com.zarholding.zar.view.extension.getAddress
import com.zarholding.zar.view.extension.hideKeyboard
import com.zarholding.zar.view.extension.setApplicatorNameToTextView
import com.zarholding.zar.view.recycler.adapter.PassengerAdapter
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import com.zarholding.zar.viewmodel.AddressViewModel
import com.zarholding.zar.viewmodel.TaxiViewModel
import com.zarholding.zar.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.util.GeoPoint
import zar.R
import zar.databinding.FragmentTaxiBinding
import java.time.LocalTime
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class TaxiReservationFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentTaxiBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var themeManagers: ThemeManager

    @Inject
    lateinit var unAuthorizationManager: UnAuthorizationManager

    @Inject
    lateinit var loadingManager: LoadingManager

    private val addressViewModel: AddressViewModel by viewModels()
    private val taxiViewModel: TaxiViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()

    private lateinit var osmManager: OsmManager
    private lateinit var passengersAdapter: PassengerAdapter

    //---------------------------------------------------------------------------------------------- OnBackPressedCallback
    private val backClick = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (taxiViewModel.getDestinationMarker() != null)
                removeDestinationMarker()
            else if (taxiViewModel.getOriginMarker() != null)
                removeOriginMarker()
            else {
                this.isEnabled = false
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- OnBackPressedCallback


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentTaxiBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = taxiViewModel
        initView()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, context?.theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, context?.theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, context?.theme))
        snack.show()
        binding.textViewLoading.visibility = View.GONE
        loadingManager.stopLoadingView()
        taxiViewModel.loadingLiveDate.value = false
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        binding.textViewLoading.visibility = View.GONE
        backClick.isEnabled = false
        loadingManager.stopLoadingView()
        unAuthorizationManager.handel(activity, type, message, binding.constraintLayoutParent)
        taxiViewModel.loadingLiveDate.value = false
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backClick)
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        setListener()
        setApplicatorNameToTextView()
        clickOnDepartureServiceTextView()
        setPassengersAdapter()
        requestGetTaxiFavPlace()
    }
    //---------------------------------------------------------------------------------------------- initView



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        fixMapScrolling()
        binding.textViewDeparture.setOnClickListener { clickOnDepartureServiceTextView() }
        binding.textViewReturning.setOnClickListener { clickOnReturningServicesTextView() }
        binding.buttonDepartureTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonReturnTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonDepartureDate.setOnClickListener { showDatePickerDialog() }
        binding.buttonReturnDate.setOnClickListener { showDatePickerDialog() }
        binding.imageViewMarker.setOnClickListener { getCenterLocationOfMap() }
        binding.imageViewFavOrigin.setOnClickListener { clickImageviewFavOrigin() }
        binding.imageViewFavDestination.setOnClickListener { clickImageviewFavDestination() }
        binding.cardViewSearch.setOnClickListener { showDialogSearchAddress() }
        binding.powerSpinnerOrigin.setOnClickListener { powerSpinnerOriginClick() }
        binding.powerSpinnerDestination.setOnClickListener { powerSpinnerDestinationClick() }
        binding.buttonSendRequest.setOnClickListener { requestTaxi() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- powerSpinnerOriginClick
    private fun powerSpinnerOriginClick() {
        if (context == null)
            return
        taxiViewModel.getOriginMarker()?.let {
            ConfirmDialog(requireContext(),
                ConfirmDialog.ConfirmType.DELETE,
                getString(R.string.originLocationIsSelect),
                object : ConfirmDialog.Click {
                    override fun clickYes() {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }).show()
        } ?: run {
            if (binding.powerSpinnerOrigin.isShowing)
                binding.powerSpinnerOrigin.dismiss()
            else
                binding.powerSpinnerOrigin.show()
        }
    }
    //---------------------------------------------------------------------------------------------- powerSpinnerOriginClick


    //---------------------------------------------------------------------------------------------- powerSpinnerDestinationClick
    private fun powerSpinnerDestinationClick() {
        if (context == null)
            return
        taxiViewModel.getDestinationMarker()?.let {
            ConfirmDialog(requireContext(),
                ConfirmDialog.ConfirmType.DELETE,
                getString(R.string.destinationLocationIsSelect),
                object : ConfirmDialog.Click {
                    override fun clickYes() {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }).show()
        } ?: run {
            if (binding.powerSpinnerDestination.isShowing)
                binding.powerSpinnerDestination.dismiss()
            else
                binding.powerSpinnerDestination.show()
        }
    }
    //---------------------------------------------------------------------------------------------- powerSpinnerDestinationClick


    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    private fun requestGetTaxiFavPlace() {
        loadingManager.setViewLoading(
            binding.constraintLayoutFooter,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow
        )
        taxiViewModel.requestGetTaxiFavPlace()
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingView()
                response?.let {
                    if (it.hasError)
                        onError(EnumErrorType.UNKNOWN, it.message)
                    else {
                        it.data?.let { items ->
                            taxiViewModel.setFavPlaceList(items)
                            initOriginSpinner()
                            initDestinationSpinner()
                        }
                    }
                }

            }
    }
    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace


    //---------------------------------------------------------------------------------------------- getCenterLocationOfMap
    private fun getCenterLocationOfMap() {
        binding.powerSpinnerOrigin.dismiss()
        binding.powerSpinnerDestination.dismiss()
        val center =
            GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
        binding.textViewLoading.visibility = View.VISIBLE
        addressViewModel.getAddress(center).observe(viewLifecycleOwner) { response ->
            response?.let { info ->
                info.address?.let { address ->
                    if (taxiViewModel.getOriginMarker() == null) {
                        setAddressToTextview(address, binding.textViewOrigin)
                        addOriginMarker(GeoPoint(center.latitude, center.longitude))
                    } else if (taxiViewModel.getDestinationMarker() == null) {
                        setAddressToTextview(address, binding.textViewDestination)
                        addDestinationMarker(GeoPoint(center.latitude, center.longitude))
                    }
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- getCenterLocationOfMap


    //---------------------------------------------------------------------------------------------- setAddressToTextview
    private fun setAddressToTextview(address: AddressModel, textView: TextView) {
        textView.isSelected = true
        textView.text = address.getAddress()
        textView.setTextColor(resources.getColor(R.color.primaryColor, context?.theme))
    }
    //---------------------------------------------------------------------------------------------- setAddressToTextview


    //---------------------------------------------------------------------------------------------- clickOnDepartureServiceTextView
    private fun clickOnDepartureServiceTextView() {
        binding.textViewDeparture.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewReturning.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.constraintLayoutReturn.visibility = View.INVISIBLE
        binding.textViewReturnTitle.visibility = View.INVISIBLE
        taxiViewModel.setTimePickMode(ZarTimePicker.PickerMode.DEPARTURE)
    }
    //---------------------------------------------------------------------------------------------- clickOnDepartureServiceTextView


    //---------------------------------------------------------------------------------------------- clickOnReturningServicesTextView
    private fun clickOnReturningServicesTextView() {
        binding.textViewReturning.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewDeparture.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.constraintLayoutReturn.visibility = View.VISIBLE
        binding.textViewReturnTitle.visibility = View.VISIBLE
        taxiViewModel.setTimePickMode(ZarTimePicker.PickerMode.RETURNING)
    }
    //---------------------------------------------------------------------------------------------- clickOnReturningServicesTextView


    //---------------------------------------------------------------------------------------------- setApplicatorNameToTextView
    private fun setApplicatorNameToTextView() {
        val fullName = userViewModel.getUser()?.fullName
        binding.textViewApplicator.setApplicatorNameToTextView(fullName)
    }
    //---------------------------------------------------------------------------------------------- setApplicatorNameToTextView


    //---------------------------------------------------------------------------------------------- showDatePickerDialog
    private fun showDatePickerDialog() {
        if (context == null)
            return
        val persianDate = LocalDateTime.now().toSolarDate()!!
        val datePickerDialog = DatePickerDialog(requireContext())
        when (taxiViewModel.getTimePickMode()) {
            ZarTimePicker.PickerMode.DEPARTURE ->
                datePickerDialog.selectionMode = DateRangeCalendarView.SelectionMode.Single
            ZarTimePicker.PickerMode.RETURNING ->
                datePickerDialog.selectionMode = DateRangeCalendarView.SelectionMode.Range
            else -> {}
        }
        datePickerDialog.acceptButtonColor =
            resources.getColor(R.color.datePickerConfirmButtonBackColor, requireContext().theme)
        datePickerDialog.headerBackgroundColor =
            resources.getColor(R.color.datePickerConfirmButtonBackColor, requireContext().theme)
        datePickerDialog.headerTextColor =
            resources.getColor(R.color.textViewColor1, requireContext().theme)
        datePickerDialog.weekColor =
            resources.getColor(R.color.buttonDisable, requireContext().theme)
        datePickerDialog.disableDateColor =
            resources.getColor(R.color.buttonDisable, requireContext().theme)
        datePickerDialog.defaultDateColor =
            resources.getColor(R.color.datePickerDateBackColor, requireContext().theme)
        datePickerDialog.selectedDateCircleColor =
            resources.getColor(R.color.datePickerConfirmButtonBackColor, requireContext().theme)
        datePickerDialog.selectedDateColor =
            resources.getColor(R.color.textViewColor1, requireContext().theme)
        datePickerDialog.rangeDateColor =
            resources.getColor(R.color.datePickerConfirmButtonBackColor, requireContext().theme)
        datePickerDialog.rangeStripColor =
            resources.getColor(R.color.datePickerRangeColor, requireContext().theme)
        datePickerDialog.holidayColor =
            resources.getColor(R.color.negative, requireContext().theme)
        datePickerDialog.textSizeWeek = 12.0f
        datePickerDialog.textSizeDate = 14.0f
        datePickerDialog.textSizeTitle = 18.0f
        datePickerDialog.setCanceledOnTouchOutside(true)
        datePickerDialog.onSingleDateSelectedListener =
            DatePickerDialog.OnSingleDateSelectedListener { startDate ->
                val today = persianDate.getSolarDate()
                if (startDate.persianShortDate < today)
                    onError(EnumErrorType.UNKNOWN, getString(R.string.selectedDateIsThePast))
                else {
                    binding.buttonDepartureDate.text = startDate.persianShortDate
                    binding.buttonDepartureTime.text = null
                    binding.buttonReturnTime.text = null
                }
            }
        datePickerDialog.onRangeDateSelectedListener =
            DatePickerDialog.OnRangeDateSelectedListener { startDate, endDate ->
                val today = persianDate.getSolarDate()
                if (startDate.persianShortDate < today)
                    onError(EnumErrorType.UNKNOWN, getString(R.string.selectedDateIsThePast))
                else {
                    binding.buttonDepartureDate.text = startDate.persianShortDate
                    binding.buttonReturnDate.text = endDate.persianShortDate
                    binding.buttonDepartureTime.text = null
                    binding.buttonReturnTime.text = null
                }
            }

        datePickerDialog.showDialog()

    }
    //---------------------------------------------------------------------------------------------- showDatePickerDialog


    //---------------------------------------------------------------------------------------------- showTimePickerDialog
    private fun showTimePickerDialog() {

        if (context == null)
            return

        if (binding.buttonDepartureDate.text.isNullOrEmpty()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheDepartureDate))
            return
        }

        if (taxiViewModel.getTimePickMode() == ZarTimePicker.PickerMode.RETURNING) {
            if (binding.buttonReturnDate.text.isNullOrEmpty()) {
                onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheReturnDate))
                return
            }

            if (binding.buttonReturnDate.text.toString() < binding.buttonDepartureDate.text.toString()
            ) {
                onError(
                    EnumErrorType.UNKNOWN,
                    getString(R.string.theReturnDateIsBeforeTheDepartureDate)
                )
                return
            }
        }

        TimeDialog(requireContext(),
            taxiViewModel.getTimePickMode(),
            click = object : TimeDialog.Click {
                override fun clickYes(timeDeparture: String, timeReturn: String) {
                    val persianDate = LocalDateTime.now().toSolarDate()!!
                    val today = persianDate.getSolarDate()
                    if (binding.buttonDepartureDate.text.toString() == today) {
                        val time = LocalTime.now()
                        val currentTime = time.hour.toString().padStart(2, '0') +
                                ":" + time.minute.toString().padStart(2, '0')

                        if (timeDeparture < currentTime) {
                            onError(
                                EnumErrorType.UNKNOWN,
                                getString(R.string.selectedTimeIsThePast)
                            )
                            return
                        }
                    }
                    binding.buttonDepartureTime.text = timeDeparture
                    if (taxiViewModel.getTimePickMode() == ZarTimePicker.PickerMode.RETURNING) {
                        if (binding.buttonReturnDate.text.toString() > binding.buttonDepartureDate.text.toString())
                            binding.buttonReturnTime.text = timeReturn
                        else {
                            if (timeReturn < timeDeparture)
                                onError(
                                    EnumErrorType.UNKNOWN,
                                    getString(R.string.theReturnTimeIsBeforeTheDepartureTime)
                                )
                            else
                                binding.buttonReturnTime.text = timeReturn
                        }
                    }
                }
            }).show()
    }
    //---------------------------------------------------------------------------------------------- showTimePickerDialog


    //---------------------------------------------------------------------------------------------- initOriginSpinner
    private fun initOriginSpinner() {
        taxiViewModel.getFavPlaceList()?.let {
            val items = it.map { taxiFavPlaceModel ->
                taxiFavPlaceModel.locationName?.let { name ->
                    IconSpinnerItem(name)
                }
            }

            binding.powerSpinnerOrigin.apply {
                setSpinnerAdapter(IconSpinnerAdapter(this))
                setItems(items)
                getSpinnerRecyclerView().layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                lifecycleOwner = viewLifecycleOwner

                setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, _ ->
                    taxiViewModel.setOriginFavPlaceModel(it[newIndex])
                    binding.imageViewFavOrigin.setImageResource(R.drawable.ic_favorite)
                    addOriginMarker(GeoPoint(it[newIndex].lat, it[newIndex].long))
                    binding.powerSpinnerOrigin.setBackgroundResource(R.drawable.drawable_spinner_select)
                    binding.textViewOrigin.text = it[newIndex].locationAddress
                    binding.textViewOrigin.setTextColor(
                        resources.getColor(
                            R.color.primaryColor,
                            context?.theme
                        )
                    )
                }
            }
        }

    }
    //---------------------------------------------------------------------------------------------- initOriginSpinner


    //---------------------------------------------------------------------------------------------- initDestinationSpinner
    private fun initDestinationSpinner() {
        taxiViewModel.getFavPlaceList()?.let {
            val items = it.map { taxiFavPlaceModel ->
                taxiFavPlaceModel.locationName?.let { name ->
                    IconSpinnerItem(name)
                }
            }
            binding.powerSpinnerDestination.apply {
                setSpinnerAdapter(IconSpinnerAdapter(this))
                setItems(items)
                getSpinnerRecyclerView().layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, _ ->
                    taxiViewModel.setDestinationFavPlaceModel(it[newIndex])
                    binding.imageViewFavDestination.setImageResource(R.drawable.ic_favorite)
                    addDestinationMarker(GeoPoint(GeoPoint(it[newIndex].lat, it[newIndex].long)))
                    binding.powerSpinnerDestination.setBackgroundResource(R.drawable.drawable_spinner_select)
                    binding.textViewDestination.text = it[newIndex].locationAddress
                    binding.textViewDestination.setTextColor(
                        resources.getColor(
                            R.color.primaryColor,
                            context?.theme
                        )
                    )
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initDestinationSpinner


    //---------------------------------------------------------------------------------------------- addOriginMarker
    private fun addOriginMarker(geoPoint: GeoPoint) {
        binding.textViewLoading.visibility = View.GONE
        val icon = osmManager.createMarkerIconDrawable(
            Size(42, 87),
            R.drawable.icon_marker_origin
        )
        taxiViewModel.setOriginMarker(osmManager.addMarker(
            icon,
            geoPoint,
            null
        ))
        binding.imageViewMarker.setImageResource(R.drawable.ic_destination)
        osmManager.moveCameraZoomUp(geoPoint)
    }
    //---------------------------------------------------------------------------------------------- addOriginMarker


    //---------------------------------------------------------------------------------------------- removeOriginMarker
    private fun removeOriginMarker() {
        binding.textViewOrigin.isSelected = false
        osmManager.removeMarkerAndMove(taxiViewModel.getOriginMarker()!!)
        taxiViewModel.setOriginMarker(null)
        binding.imageViewFavOrigin.visibility = View.VISIBLE
        taxiViewModel.setOriginFavPlaceModel(null)
        binding.imageViewFavOrigin.setImageResource(R.drawable.ic_favorite_outline)
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        binding.powerSpinnerOrigin.clearSelectedItem()
        binding.powerSpinnerOrigin.showArrow = true
        binding.powerSpinnerOrigin.setBackgroundResource(R.drawable.drawable_spinner)
        binding.textViewOrigin.text = getString(R.string.pleaseSelectPlaceOfOrigin)
        binding.textViewOrigin.setTextColor(
            resources.getColor(
                R.color.textViewFooter,
                context?.theme
            )
        )
    }
    //---------------------------------------------------------------------------------------------- removeOriginMarker


    //---------------------------------------------------------------------------------------------- addDestinationMarker
    private fun addDestinationMarker(geoPoint: GeoPoint) {
        binding.textViewLoading.visibility = View.GONE
        val icon = osmManager.createMarkerIconDrawable(
            Size(42, 87),
            R.drawable.icon_marker_destination
        )
        taxiViewModel.setDestinationMarker(
            osmManager.addMarker(
                icon,
                geoPoint,
                null
            )
        )
        binding.imageViewMarker.visibility = View.GONE
        val points = mutableListOf<GeoPoint>()
        points.add(taxiViewModel.getOriginMarker()!!.position)
        points.add(taxiViewModel.getDestinationMarker()!!.position)
        val box = osmManager.getBoundingBoxFromPoints(points)
        binding.mapView.zoomToBoundingBox(box, true)
    }
    //---------------------------------------------------------------------------------------------- addDestinationMarker


    //---------------------------------------------------------------------------------------------- removeDestinationMarker
    private fun removeDestinationMarker() {
        taxiViewModel.setDestinationFavPlaceModel(null)
        binding.imageViewFavDestination.visibility = View.VISIBLE
        binding.imageViewFavDestination.setImageResource(R.drawable.ic_favorite_outline)
        binding.textViewDestination.isSelected = false
        osmManager.removeMarkerAndMove(taxiViewModel.getDestinationMarker()!!)
        taxiViewModel.setDestinationMarker(null)
        binding.imageViewMarker.visibility = View.VISIBLE
        binding.imageViewMarker.setImageResource(R.drawable.ic_destination)
        binding.powerSpinnerDestination.clearSelectedItem()
        binding.powerSpinnerDestination.showArrow = true
        binding.powerSpinnerDestination.setBackgroundResource(R.drawable.drawable_spinner)
        binding.textViewDestination.text =
            getString(R.string.pleaseSelectPlaceOfDestination)
        binding.textViewDestination.setTextColor(
            resources.getColor(
                R.color.textViewFooter,
                context?.theme
            )
        )
    }
    //---------------------------------------------------------------------------------------------- removeDestinationMarker


    //---------------------------------------------------------------------------------------------- setPassengersAdapter
    private fun setPassengersAdapter() {
        val users: MutableList<UserInfoEntity> = mutableListOf()
        val user = userViewModel.getUser()
        user?.let {
            users.add(it)
        }
        val click = object : PassengerItemHolder.Click {
            override fun addClick() {
                showPersonnelDialog()
            }

            override fun itemClick(item: UserInfoEntity) {
                showDialogDeletePassenger(item)
            }
        }
        passengersAdapter = PassengerAdapter(users, click)
        val gridLayoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false
        )
/*        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == users.size - 1)
                    2
                else
                    1
            }
        }*/
        binding.recyclerViewPassengers.layoutManager = gridLayoutManager
        binding.recyclerViewPassengers.layoutDirection = View.LAYOUT_DIRECTION_RTL
        binding.recyclerViewPassengers.adapter = passengersAdapter
    }
    //---------------------------------------------------------------------------------------------- setPassengersAdapter


    //---------------------------------------------------------------------------------------------- showDialogDeletePassenger
    private fun showDialogDeletePassenger(item: UserInfoEntity) {
        if (context == null)
            return
        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                passengersAdapter.deleteUser(item)
            }
        }
        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.DELETE,
            getString(R.string.confirmForDelete, item.fullName),
            click
        )
    }
    //---------------------------------------------------------------------------------------------- showDialogDeletePassenger


    //---------------------------------------------------------------------------------------------- showPersonnelDialog
    private fun showPersonnelDialog() {
        val click = object : PersonnelDialog.Click {
            override fun select(item: UserInfoEntity) {
                val exist = passengersAdapter.getList()
                    .find { userInfoEntity -> userInfoEntity.userName == item.userName }
                if (exist == null)
                    passengersAdapter.addUser(item)
                else
                    onError(EnumErrorType.UNKNOWN, getString(R.string.duplicateInsert))
            }
        }
        PersonnelDialog(click).show(childFragmentManager, "personnel dialog")
    }
    //---------------------------------------------------------------------------------------------- showPersonnelDialog


    //---------------------------------------------------------------------------------------------- clickImageviewFavOrigin
    private fun clickImageviewFavOrigin() {
        taxiViewModel.getOriginMarker()?.let {
            taxiViewModel.setFavPlaceType(FavPlaceType.ORIGIN)
            taxiViewModel.getOriginFacPlaceModel()?.let {
                if (taxiViewModel.getDestinationFavPlaceModel() == null)
                    showRemoveFromFavPlaceDialog(it.locationName, it.id)
                else
                    onError(EnumErrorType.UNKNOWN, getString(R.string.destinationLocationSelected))
            } ?: run {
                showAddToFavPlaceDialog(binding.textViewOrigin.text.toString())
            }
        } ?: run {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseChooseLocation))
        }

    }
    //---------------------------------------------------------------------------------------------- clickImageviewFavOrigin


    //---------------------------------------------------------------------------------------------- clickImageviewFavDestination
    private fun clickImageviewFavDestination() {
        taxiViewModel.getDestinationMarker()?.let {
            taxiViewModel.setFavPlaceType(FavPlaceType.DESTINATION)
            taxiViewModel.getDestinationFavPlaceModel()?.let {
                showRemoveFromFavPlaceDialog(it.locationName, it.id)
            } ?: run {
                showAddToFavPlaceDialog(binding.textViewDestination.text.toString())
            }
        } ?: run {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseChooseLocation))
        }

    }
    //---------------------------------------------------------------------------------------------- clickImageviewFavDestination


    //---------------------------------------------------------------------------------------------- showAddToFavPlaceDialog
    private fun showAddToFavPlaceDialog(placeAddress: String) {
        context?.let {
            val click = object : FavPlaceDialog.Click {
                override fun clickSend(placeName: String) {
                    requestAddFavPlace(placeName, placeAddress)
                }
            }
            FavPlaceDialog(it, click).show()
        }
    }
    //---------------------------------------------------------------------------------------------- showAddToFavPlaceDialog


    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    private fun requestAddFavPlace(placeName: String, placeAddress: String) {
        if (context == null)
            return
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_repeat)
        var geoPoint: GeoPoint? = null
        when (taxiViewModel.getFavPlaceType()) {
            FavPlaceType.ORIGIN -> {
                binding.imageViewFavOrigin.startAnimation(rotation)
                geoPoint = GeoPoint(taxiViewModel.getOriginMarker()?.position)
            }
            FavPlaceType.DESTINATION -> {
                binding.imageViewFavDestination.startAnimation(rotation)
                geoPoint = GeoPoint(taxiViewModel.getDestinationMarker()?.position)
            }
            else -> {}
        }
        geoPoint?.let {
            val request = TaxiAddFavPlaceRequest(placeName, placeAddress, it.latitude, it.longitude)
            taxiViewModel.requestAddFavPlace(request)
                .observe(viewLifecycleOwner) { response ->
                    binding.imageViewFavOrigin.clearAnimation()
                    binding.imageViewFavDestination.clearAnimation()
                    response?.let { data ->
                        onError(EnumErrorType.UNKNOWN, data.message)
                        if (!data.hasError) {
                            data.data?.let { element ->
                                taxiViewModel.addToFavPlaceList(element)
                                initOriginSpinner()
                                initDestinationSpinner()
                            }
                            when (taxiViewModel.getFavPlaceType()) {
                                FavPlaceType.ORIGIN -> {
                                    taxiViewModel.setOriginFavPlaceModel(data.data)
                                    binding
                                        .imageViewFavOrigin
                                        .setImageResource(R.drawable.ic_favorite)
                                }
                                FavPlaceType.DESTINATION -> {
                                    taxiViewModel.setDestinationFavPlaceModel(data.data)
                                    binding
                                        .imageViewFavDestination
                                        .setImageResource(R.drawable.ic_favorite)
                                }
                                else -> {}
                            }
                        }
                    }
                }
        }

    }
    //---------------------------------------------------------------------------------------------- requestAddFavPlace


    //---------------------------------------------------------------------------------------------- showRemoveFromFavPlaceDialog
    private fun showRemoveFromFavPlaceDialog(placeName: String?, id: Int) {
        if (context == null)
            return
        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                requestRemoveFavPlace(id)
            }
        }
        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.DELETE,
            getString(R.string.confirmForDelete, placeName),
            click
        ).show()
    }
    //---------------------------------------------------------------------------------------------- showRemoveFromFavPlaceDialog


    //---------------------------------------------------------------------------------------------- requestRemoveFavPlace
    private fun requestRemoveFavPlace(id: Int) {
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_repeat)
        when (taxiViewModel.getFavPlaceType()) {
            FavPlaceType.ORIGIN -> {
                binding.imageViewFavOrigin.startAnimation(rotation)
            }
            FavPlaceType.DESTINATION -> {
                binding.imageViewFavDestination.startAnimation(rotation)
            }
            else -> {}
        }
        taxiViewModel.requestDeleteFavPlace(id)
            .observe(viewLifecycleOwner) { response ->
                binding.imageViewFavOrigin.clearAnimation()
                binding.imageViewFavDestination.clearAnimation()
                response?.let { it ->
                    onError(EnumErrorType.UNKNOWN, it.message)
                    if (!it.hasError) {
                        when (taxiViewModel.getFavPlaceType()) {
                            FavPlaceType.ORIGIN -> removeOriginMarker()
                            FavPlaceType.DESTINATION -> removeDestinationMarker()
                            else -> {}
                        }
                        taxiViewModel.removeItemInFavPlace(id)
                        initOriginSpinner()
                        initDestinationSpinner()
                    }
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestRemoveFavPlace


    //---------------------------------------------------------------------------------------------- showDialogSearchAddress
    private fun showDialogSearchAddress() {
        binding.editTextReason.clearFocus()
        val selectItem = object : SearchAddressDialog.Click {
            override fun select(item: AddressSuggestionModel) {
                osmManager.moveCamera(GeoPoint(item.lat, item.lon))
            }
        }

        SearchAddressDialog(selectItem).show(childFragmentManager, "search address")
    }
    //---------------------------------------------------------------------------------------------- showDialogSearchAddress


    //---------------------------------------------------------------------------------------------- requestTaxi
    private fun requestTaxi() {
        hideKeyboard()
        if (checkValueBeforeRequest() && taxiViewModel.loadingLiveDate.value == false) {
            val passenger = passengersAdapter.getList().map { userInfoEntity -> userInfoEntity.id }
            val user = userViewModel.getUser()
            val request = TaxiRequestModel(
                taxiViewModel.getEnumTaxiRequest(),
                binding.buttonDepartureDate.text.toString(),
                binding.buttonDepartureTime.text.toString(),
                binding.buttonReturnDate.text.toString(),
                binding.buttonReturnTime.text.toString(),
                taxiViewModel.getOriginMarker()!!.position.latitude,
                taxiViewModel.getOriginMarker()!!.position.longitude,
                binding.textViewOrigin.text.toString(),
                taxiViewModel.getDestinationMarker()!!.position.latitude,
                taxiViewModel.getDestinationMarker()!!.position.longitude,
                binding.textViewDestination.text.toString(),
                passenger,
                binding.editTextReason.text.toString(),
                user?.companyCode,
                user?.personnelJobKeyCode
            )
            taxiViewModel.loadingLiveDate.value = true
            taxiViewModel.requestTaxi(request)
                .observe(viewLifecycleOwner) { response ->
                    taxiViewModel.loadingLiveDate.value = false
                    response?.let {
                        backClick.isEnabled = false
                        onError(EnumErrorType.UNKNOWN, response.message)
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }
        }
    }
    //---------------------------------------------------------------------------------------------- requestTaxi


    //---------------------------------------------------------------------------------------------- checkValueBeforeRequest
    private fun checkValueBeforeRequest(): Boolean {

        if (context == null)
            return false

        if (taxiViewModel.getOriginMarker() == null) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseChooseOriginLocation))
            return false
        }

        if (taxiViewModel.getDestinationMarker() == null) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseChooseDestinationLocation))
            return false
        }

        if (binding.buttonDepartureDate.text.toString().isEmpty()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheDepartureDate))
            return false
        }

        if (binding.buttonDepartureTime.text.toString().isEmpty()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheDepartureTime))
            return false
        }

        if (taxiViewModel.getTimePickMode() == ZarTimePicker.PickerMode.RETURNING) {

            if (binding.buttonReturnDate.text.toString().isEmpty()) {
                onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheReturnDate))
                return false
            }

            if (binding.buttonReturnTime.text.toString().isEmpty()) {
                onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheReturnTime))
                return false
            }

        }

        if (passengersAdapter.getList().isEmpty()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseSelectPassenger))
            return false
        }

        return true
    }
    //---------------------------------------------------------------------------------------------- checkValueBeforeRequest


    //---------------------------------------------------------------------------------------------- fixMapScrolling
    @SuppressLint("ClickableViewAccessibility")
    private fun fixMapScrolling() {
        binding.imageviewTouch.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrollView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> {
                    true
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- fixMapScrolling


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onPause()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}