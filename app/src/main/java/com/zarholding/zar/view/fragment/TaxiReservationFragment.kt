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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.zar.core.enums.EnumApiError
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
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.*
import com.zarholding.zar.utility.extension.getAddress
import com.zarholding.zar.utility.extension.hideKeyboard
import com.zarholding.zar.utility.extension.setApplicatorNameToTextView
import com.zarholding.zar.view.recycler.adapter.PassengerAdapter
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import com.zarholding.zar.viewmodel.TaxiReservationViewModel
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
class TaxiReservationFragment : Fragment() {

    @Inject
    lateinit var themeManagers: ThemeManager

    @Inject
    lateinit var loadingManager: LoadingManager

    private var _binding: FragmentTaxiBinding? = null
    private val binding get() = _binding!!

    private val taxiReservationViewModel: TaxiReservationViewModel by viewModels()

    private lateinit var osmManager: OsmManager
    private lateinit var passengersAdapter: PassengerAdapter

    //---------------------------------------------------------------------------------------------- OnBackPressedCallback
    private val backClick = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (taxiReservationViewModel.getDestinationMarker() != null)
                removeDestinationMarker()
            else if (taxiReservationViewModel.getOriginMarker() != null)
                removeOriginMarker()
            else {
                if (binding.powerSpinnerOrigin.isShowing)
                    binding.powerSpinnerOrigin.dismiss()
                else if (binding.powerSpinnerDestination.isShowing)
                    binding.powerSpinnerDestination.dismiss()
                else if (binding.powerSpinnerCompany.isShowing)
                    binding.powerSpinnerCompany.dismiss()
                else {
                    this.isEnabled = false
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- OnBackPressedCallback


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaxiBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, context?.theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, context?.theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, context?.theme))
        snack.show()
        binding.textViewLoading.visibility = View.GONE
        loadingManager.stopLoadingView()
    }
    //---------------------------------------------------------------------------------------------- showMessage


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backClick)
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        binding.textViewChooseLocation.text = getString(R.string.pleaseSelectPlaceOfOrigin)
        setListener()
        setApplicatorNameToTextView()
        clickOnDepartureServiceTextView()
        setPassengersAdapter()
        getSpinnersData()
        observeErrorLiveDate()
        observeFavePlaceLiveData()
        observeAddressLiveData()
        observeAddFavPlaceLiveData()
        observeRemoveFavPlaceLiveData()
        observeSendRequestLiveData()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        taxiReservationViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            binding.buttonSendRequest.stopLoading()
            backClick.isEnabled = false
            loadingManager.stopLoadingView()
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()

                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate


    //---------------------------------------------------------------------------------------------- observeFavePlaceLiveData
    private fun observeFavePlaceLiveData() {
        taxiReservationViewModel.setFavPlaceLiveData.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
            initOriginSpinner()
            initDestinationSpinner()
            initCompanySpinner()
        }
    }
    //---------------------------------------------------------------------------------------------- observeFavePlaceLiveData


    //---------------------------------------------------------------------------------------------- observeAddressLiveData
    private fun observeAddressLiveData() {
        taxiReservationViewModel.addressLiveData.observe(viewLifecycleOwner) {
            val center =
                GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
            if (taxiReservationViewModel.getOriginMarker() == null) {
                setAddressToTextview(it, binding.textViewOrigin)
                addOriginMarker(GeoPoint(center.latitude, center.longitude))
            } else if (taxiReservationViewModel.getDestinationMarker() == null) {
                setAddressToTextview(it, binding.textViewDestination)
                addDestinationMarker(GeoPoint(center.latitude, center.longitude))
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeAddressLiveData


    //---------------------------------------------------------------------------------------------- observeAddFavPlaceLiveData
    private fun observeAddFavPlaceLiveData() {
        taxiReservationViewModel.addFavPlaceLiveData.observe(viewLifecycleOwner) {
            binding.imageViewFavOrigin.clearAnimation()
            binding.imageViewFavDestination.clearAnimation()
            when (taxiReservationViewModel.getFavPlaceType()) {
                FavPlaceType.ORIGIN -> {
                    binding
                        .imageViewFavOrigin
                        .setImageResource(R.drawable.ic_favorite)
                }
                FavPlaceType.DESTINATION -> {
                    binding
                        .imageViewFavDestination
                        .setImageResource(R.drawable.ic_favorite)
                }
                else -> {}
            }
            initOriginSpinner()
            initDestinationSpinner()
        }
    }
    //---------------------------------------------------------------------------------------------- observeAddFavPlaceLiveData


    //---------------------------------------------------------------------------------------------- observeRemoveFavPlaceLiveData
    private fun observeRemoveFavPlaceLiveData() {
        taxiReservationViewModel.removeFavPlaceLiveData.observe(viewLifecycleOwner) {
            binding.imageViewFavOrigin.clearAnimation()
            binding.imageViewFavDestination.clearAnimation()
            when (taxiReservationViewModel.getFavPlaceType()) {
                FavPlaceType.ORIGIN -> removeOriginMarker()
                FavPlaceType.DESTINATION -> removeDestinationMarker()
                else -> {}
            }
            initOriginSpinner()
            initDestinationSpinner()
        }
    }
    //---------------------------------------------------------------------------------------------- observeRemoveFavPlaceLiveData


    //---------------------------------------------------------------------------------------------- observeSendRequestLiveData
    private fun observeSendRequestLiveData() {
        taxiReservationViewModel.sendRequestLiveData.observe(viewLifecycleOwner) {
            showMessage(it)
            backClick.isEnabled = false
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
    //---------------------------------------------------------------------------------------------- observeSendRequestLiveData


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        fixMapScrolling()
        binding.materialButtonMyRequest.setOnClickListener { gotoMyRequestHistory() }
        binding.textViewDeparture.setOnClickListener { clickOnDepartureServiceTextView() }
        binding.textViewReturning.setOnClickListener { clickOnReturningServicesTextView() }
        binding.buttonDepartureTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonReturnTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonDepartureDate.setOnClickListener { showDatePickerDialog() }
        binding.buttonReturnDate.setOnClickListener { showDatePickerDialog() }
        binding.imageViewMarker.setOnClickListener { getCenterLocationOfMap() }
        binding.textViewChooseLocation.setOnClickListener { getCenterLocationOfMap() }
        binding.imageViewFavOrigin.setOnClickListener { clickImageviewFavOrigin() }
        binding.imageViewFavDestination.setOnClickListener { clickImageviewFavDestination() }
        binding.textViewSearch.setOnClickListener { showDialogSearchAddress() }
        binding.powerSpinnerOrigin.setOnClickListener { powerSpinnerOriginClick() }
        binding.powerSpinnerDestination.setOnClickListener { powerSpinnerDestinationClick() }
        binding.powerSpinnerCompany.setOnClickListener { powerSpinnerCompanyClick() }
        binding.buttonSendRequest.setOnClickListener { requestTaxi() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- gotoMyRequestHistory
    private fun gotoMyRequestHistory() {
        val bundle = Bundle()
        bundle.putBoolean(CompanionValues.myRequest, true)
        findNavController()
            .navigate(R.id.action_TaxiReservationFragment_to_AdminTaxiFragment, bundle)
    }
    //---------------------------------------------------------------------------------------------- gotoMyRequestHistory


    //---------------------------------------------------------------------------------------------- powerSpinnerOriginClick
    private fun powerSpinnerOriginClick() {
        if (context == null)
            return
        taxiReservationViewModel.getOriginMarker()?.let {
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
        taxiReservationViewModel.getDestinationMarker()?.let {
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


    //---------------------------------------------------------------------------------------------- powerSpinnerCompanyClick
    private fun powerSpinnerCompanyClick() {
        if (context == null)
            return
        taxiReservationViewModel.companySelected?.let {
            ConfirmDialog(requireContext(),
                ConfirmDialog.ConfirmType.DELETE,
                getString(R.string.companyIsSelect),
                object : ConfirmDialog.Click {
                    override fun clickYes() {
                        binding.textViewCompany.text = getString(R.string.pleaseSelectCompany)
                        taxiReservationViewModel.companySelected = null
                        binding.powerSpinnerCompany.clearSelectedItem()
                        binding.powerSpinnerCompany.showArrow = true
                        binding.powerSpinnerCompany.setBackgroundResource(R.drawable.drawable_spinner)
                    }
                }).show()
        } ?: run {
            if (binding.powerSpinnerCompany.isShowing)
                binding.powerSpinnerCompany.dismiss()
            else
                binding.powerSpinnerCompany.show()
        }
    }
    //---------------------------------------------------------------------------------------------- powerSpinnerCompanyClick


    //---------------------------------------------------------------------------------------------- getSpinnersData
    private fun getSpinnersData() {
        loadingManager.setViewLoading(
            binding.constraintLayoutFooter,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow
        )
        taxiReservationViewModel.getSpinnersData()
    }
    //---------------------------------------------------------------------------------------------- getSpinnersData


    //---------------------------------------------------------------------------------------------- getCenterLocationOfMap
    private fun getCenterLocationOfMap() {
        binding.powerSpinnerOrigin.dismiss()
        binding.powerSpinnerDestination.dismiss()
        val center =
            GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
        binding.textViewLoading.visibility = View.VISIBLE
        taxiReservationViewModel.getAddress(center)
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
        taxiReservationViewModel.setTimePickMode(ZarTimePicker.PickerMode.DEPARTURE)
    }
    //---------------------------------------------------------------------------------------------- clickOnDepartureServiceTextView


    //---------------------------------------------------------------------------------------------- clickOnReturningServicesTextView
    private fun clickOnReturningServicesTextView() {
        binding.textViewReturning.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewDeparture.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.constraintLayoutReturn.visibility = View.VISIBLE
        binding.textViewReturnTitle.visibility = View.VISIBLE
        taxiReservationViewModel.setTimePickMode(ZarTimePicker.PickerMode.RETURNING)
    }
    //---------------------------------------------------------------------------------------------- clickOnReturningServicesTextView


    //---------------------------------------------------------------------------------------------- setApplicatorNameToTextView
    private fun setApplicatorNameToTextView() {
        val fullName = taxiReservationViewModel.getUser()?.fullName
        binding.textViewApplicator.setApplicatorNameToTextView(fullName)
    }
    //---------------------------------------------------------------------------------------------- setApplicatorNameToTextView


    //---------------------------------------------------------------------------------------------- showDatePickerDialog
    private fun showDatePickerDialog() {
        if (context == null)
            return
        val datePickerDialog = DatePickerDialog(requireContext())
        when (taxiReservationViewModel.getTimePickMode()) {
            ZarTimePicker.PickerMode.DEPARTURE ->
                datePickerDialog.selectionMode = DateRangeCalendarView.SelectionMode.Single
            ZarTimePicker.PickerMode.RETURNING ->
                datePickerDialog.selectionMode = DateRangeCalendarView.SelectionMode.Range
            else -> {}
        }
        datePickerDialog.isDisableDaysAgo = taxiReservationViewModel.isDisableDaysAgo()
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
                binding.buttonDepartureDate.text = startDate.persianShortDate
                binding.buttonDepartureTime.text = null
                binding.buttonReturnTime.text = null
            }
        datePickerDialog.onRangeDateSelectedListener =
            DatePickerDialog.OnRangeDateSelectedListener { startDate, endDate ->
                binding.buttonDepartureDate.text = startDate.persianShortDate
                binding.buttonReturnDate.text = endDate.persianShortDate
                binding.buttonDepartureTime.text = null
                binding.buttonReturnTime.text = null
            }

        datePickerDialog.showDialog()

    }
    //---------------------------------------------------------------------------------------------- showDatePickerDialog


    //---------------------------------------------------------------------------------------------- showTimePickerDialog
    private fun showTimePickerDialog() {

        if (context == null)
            return

        if (binding.buttonDepartureDate.text.isNullOrEmpty()) {
            showMessage(getString(R.string.selectTheDepartureDate))
            return
        }

        if (taxiReservationViewModel.getTimePickMode() == ZarTimePicker.PickerMode.RETURNING) {
            if (binding.buttonReturnDate.text.isNullOrEmpty()) {
                showMessage(getString(R.string.selectTheReturnDate))
                return
            }

            if (binding.buttonReturnDate.text.toString() < binding.buttonDepartureDate.text.toString()
            ) {
                showMessage(getString(R.string.theReturnDateIsBeforeTheDepartureDate))
                return
            }
        }

        TimeDialog(requireContext(),
            taxiReservationViewModel.getTimePickMode(),
            click = object : TimeDialog.Click {
                override fun clickYes(timeDeparture: String, timeReturn: String) {
                    if (taxiReservationViewModel.isDisableDaysAgo()) {
                        val persianDate = LocalDateTime.now().toSolarDate()!!
                        val today = persianDate.getSolarDate()
                        if (binding.buttonDepartureDate.text.toString() == today) {
                            val time = LocalTime.now()
                            val currentTime = time.hour.toString().padStart(2, '0') +
                                    ":" + time.minute.toString().padStart(2, '0')

                            if (timeDeparture < currentTime) {
                                showMessage(getString(R.string.selectedTimeIsThePast))
                                return
                            }
                        }
                    }
                    binding.buttonDepartureTime.text = timeDeparture
                    if (taxiReservationViewModel.getTimePickMode() == ZarTimePicker.PickerMode.RETURNING) {
                        if (binding.buttonReturnDate.text.toString() > binding.buttonDepartureDate.text.toString())
                            binding.buttonReturnTime.text = timeReturn
                        else {
                            if (timeReturn < timeDeparture)
                                showMessage(getString(R.string.theReturnTimeIsBeforeTheDepartureTime))
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
        taxiReservationViewModel.getFavPlaceList()?.let {
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
                    taxiReservationViewModel.setOriginFavPlaceModel(it[newIndex])
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
        taxiReservationViewModel.getFavPlaceList()?.let {
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
                    taxiReservationViewModel.setDestinationFavPlaceModel(it[newIndex])
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


    //---------------------------------------------------------------------------------------------- initCompanySpinner
    private fun initCompanySpinner() {
        taxiReservationViewModel.getCompaniesList()?.let {
            val items = it.map { company ->
                IconSpinnerItem(company.text)
            }

            binding.powerSpinnerCompany.apply {
                setSpinnerAdapter(IconSpinnerAdapter(this))
                setItems(items)
                getSpinnerRecyclerView().layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                lifecycleOwner = viewLifecycleOwner

                setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, _ ->
                    taxiReservationViewModel.companySelected = it[newIndex]
                    binding.powerSpinnerCompany.setBackgroundResource(R.drawable.drawable_spinner_select)
                    binding.textViewCompany.text = it[newIndex].text
                }
            }
        }

    }
    //---------------------------------------------------------------------------------------------- initCompanySpinner


    //---------------------------------------------------------------------------------------------- addOriginMarker
    private fun addOriginMarker(geoPoint: GeoPoint) {
        binding.textViewLoading.visibility = View.GONE
        val icon = osmManager.createMarkerIconDrawable(
            Size(143, 85),
            R.drawable.icon_marker_origin
        )
        taxiReservationViewModel.setOriginMarker(
            osmManager.addMarker(
                icon,
                geoPoint,
                null
            )
        )
        binding.textViewChooseLocation.text = getString(R.string.pleaseSelectPlaceOfDestination)
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        osmManager.moveCameraZoomUp(geoPoint)
    }
    //---------------------------------------------------------------------------------------------- addOriginMarker


    //---------------------------------------------------------------------------------------------- removeOriginMarker
    private fun removeOriginMarker() {
        binding.textViewOrigin.isSelected = false
        osmManager.removeMarkerAndMove(taxiReservationViewModel.getOriginMarker()!!)
        taxiReservationViewModel.setOriginMarker(null)
        binding.imageViewFavOrigin.visibility = View.VISIBLE
        taxiReservationViewModel.setOriginFavPlaceModel(null)
        binding.imageViewFavOrigin.setImageResource(R.drawable.ic_favorite_outline)
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        binding.textViewChooseLocation.text = getString(R.string.pleaseSelectPlaceOfOrigin)
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
            Size(143, 85),
            R.drawable.icon_marker_destination
        )
        taxiReservationViewModel.setDestinationMarker(
            osmManager.addMarker(
                icon,
                geoPoint,
                null
            )
        )
        binding.imageViewMarker.visibility = View.GONE
        binding.textViewChooseLocation.visibility = View.GONE
        binding.textViewSearch.visibility = View.GONE
        val points = mutableListOf<GeoPoint>()
        points.add(taxiReservationViewModel.getOriginMarker()!!.position)
        points.add(taxiReservationViewModel.getDestinationMarker()!!.position)
        val box = osmManager.getBoundingBoxFromPoints(points)
        binding.mapView.zoomToBoundingBox(box, true)
    }
    //---------------------------------------------------------------------------------------------- addDestinationMarker


    //---------------------------------------------------------------------------------------------- removeDestinationMarker
    private fun removeDestinationMarker() {
        taxiReservationViewModel.setDestinationFavPlaceModel(null)
        binding.imageViewFavDestination.visibility = View.VISIBLE
        binding.imageViewFavDestination.setImageResource(R.drawable.ic_favorite_outline)
        binding.textViewDestination.isSelected = false
        osmManager.removeMarkerAndMove(taxiReservationViewModel.getDestinationMarker()!!)
        taxiReservationViewModel.setDestinationMarker(null)
        binding.imageViewMarker.visibility = View.VISIBLE
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        binding.textViewChooseLocation.text = getString(R.string.pleaseSelectPlaceOfDestination)
        binding.textViewChooseLocation.visibility = View.VISIBLE
        binding.textViewSearch.visibility = View.VISIBLE
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
        val user = taxiReservationViewModel.getUser()
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
                    showMessage(getString(R.string.duplicateInsert))
            }
        }
        PersonnelDialog(click).show(childFragmentManager, "personnel dialog")
    }
    //---------------------------------------------------------------------------------------------- showPersonnelDialog


    //---------------------------------------------------------------------------------------------- clickImageviewFavOrigin
    private fun clickImageviewFavOrigin() {
        taxiReservationViewModel.getOriginMarker()?.let {
            taxiReservationViewModel.setFavPlaceType(FavPlaceType.ORIGIN)
            taxiReservationViewModel.getOriginFacPlaceModel()?.let {
                if (taxiReservationViewModel.getDestinationFavPlaceModel() == null)
                    showRemoveFromFavPlaceDialog(it.locationName, it.id)
                else
                    showMessage(getString(R.string.destinationLocationSelected))
            } ?: run {
                showAddToFavPlaceDialog(binding.textViewOrigin.text.toString())
            }
        } ?: run {
            showMessage(getString(R.string.pleaseChooseLocation))
        }
    }
    //---------------------------------------------------------------------------------------------- clickImageviewFavOrigin


    //---------------------------------------------------------------------------------------------- clickImageviewFavDestination
    private fun clickImageviewFavDestination() {
        taxiReservationViewModel.getDestinationMarker()?.let {
            taxiReservationViewModel.setFavPlaceType(FavPlaceType.DESTINATION)
            taxiReservationViewModel.getDestinationFavPlaceModel()?.let {
                showRemoveFromFavPlaceDialog(it.locationName, it.id)
            } ?: run {
                showAddToFavPlaceDialog(binding.textViewDestination.text.toString())
            }
        } ?: run {
            showMessage(getString(R.string.pleaseChooseLocation))
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
        when (taxiReservationViewModel.getFavPlaceType()) {
            FavPlaceType.ORIGIN -> {
                binding.imageViewFavOrigin.startAnimation(rotation)
                geoPoint = GeoPoint(taxiReservationViewModel.getOriginMarker()?.position)
            }
            FavPlaceType.DESTINATION -> {
                binding.imageViewFavDestination.startAnimation(rotation)
                geoPoint = GeoPoint(taxiReservationViewModel.getDestinationMarker()?.position)
            }
            else -> {}
        }
        geoPoint?.let {
            val request = TaxiAddFavPlaceRequest(placeName, placeAddress, it.latitude, it.longitude)
            taxiReservationViewModel.requestAddFavPlace(request)
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
        when (taxiReservationViewModel.getFavPlaceType()) {
            FavPlaceType.ORIGIN -> {
                binding.imageViewFavOrigin.startAnimation(rotation)
            }
            FavPlaceType.DESTINATION -> {
                binding.imageViewFavDestination.startAnimation(rotation)
            }
            else -> {}
        }
        taxiReservationViewModel.requestDeleteFavPlace(id)
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
        if (checkValueBeforeRequest() && !binding.buttonSendRequest.isLoading) {
            val passenger = passengersAdapter.getList().map { userInfoEntity -> userInfoEntity.id }
            val user = taxiReservationViewModel.getUser()
            val request = TaxiRequestModel(
                taxiReservationViewModel.getEnumTaxiRequest(),
                binding.buttonDepartureDate.text.toString(),
                binding.buttonDepartureTime.text.toString(),
                binding.buttonReturnDate.text.toString(),
                binding.buttonReturnTime.text.toString(),
                taxiReservationViewModel.getOriginMarker()!!.position.latitude,
                taxiReservationViewModel.getOriginMarker()!!.position.longitude,
                binding.textViewOrigin.text.toString(),
                taxiReservationViewModel.getDestinationMarker()!!.position.latitude,
                taxiReservationViewModel.getDestinationMarker()!!.position.longitude,
                binding.textViewDestination.text.toString(),
                passenger,
                binding.editTextReason.text.toString(),
                taxiReservationViewModel.companySelected?.value,
                user?.personnelJobKeyCode
            )
            binding.buttonSendRequest.startLoading(getString(R.string.bePatient))
            taxiReservationViewModel.requestTaxi(request)
        }
    }
    //---------------------------------------------------------------------------------------------- requestTaxi


    //---------------------------------------------------------------------------------------------- checkValueBeforeRequest
    private fun checkValueBeforeRequest(): Boolean {

        if (context == null)
            return false

        if (taxiReservationViewModel.getOriginMarker() == null) {
            showMessage(getString(R.string.pleaseChooseOriginLocation))
            return false
        }

        if (taxiReservationViewModel.getDestinationMarker() == null) {
            showMessage(getString(R.string.pleaseChooseDestinationLocation))
            return false
        }

        if (binding.buttonDepartureDate.text.toString().isEmpty()) {
            showMessage(getString(R.string.selectTheDepartureDate))
            return false
        }

        if (binding.buttonDepartureTime.text.toString().isEmpty()) {
            showMessage(getString(R.string.selectTheDepartureTime))
            return false
        }

        if (taxiReservationViewModel.getTimePickMode() == ZarTimePicker.PickerMode.RETURNING) {

            if (binding.buttonReturnDate.text.toString().isEmpty()) {
                showMessage(getString(R.string.selectTheReturnDate))
                return false
            }

            if (binding.buttonReturnTime.text.toString().isEmpty()) {
                showMessage(getString(R.string.selectTheReturnTime))
                return false
            }

        }

        if (passengersAdapter.getList().isEmpty()) {
            showMessage(getString(R.string.pleaseSelectPassenger))
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