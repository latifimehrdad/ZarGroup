package com.zarholding.zar.view.fragment

import android.graphics.Typeface
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.extensions.toSolarDate
import com.zar.core.tools.loadings.LoadingManager
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.request.TaxiAddFavPlaceRequest
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.model.response.taxi.TaxiFavPlaceModel
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.TimePicker
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.*
import com.zarholding.zar.view.extension.getAddress
import com.zarholding.zar.view.recycler.adapter.PassengerAdapter
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import com.zarholding.zar.viewmodel.AddressViewModel
import com.zarholding.zar.viewmodel.TaxiViewModel
import com.zarholding.zar.viewmodel.TokenViewModel
import dagger.hilt.android.AndroidEntryPoint
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import zar.R
import zar.databinding.FragmentTaxiBinding
import javax.inject.Inject
import java.time.LocalDateTime
import java.time.LocalTime


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class TaxiReservationFragment : Fragment(), RemoteErrorEmitter {

    @Inject
    lateinit var themeManagers: ThemeManager

    @Inject
    lateinit var userInfoDao: UserInfoDao

    @Inject
    lateinit var unAuthorizationManager: UnAuthorizationManager

    private val addressViewModel: AddressViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private val taxiViewModel: TaxiViewModel by viewModels()


    private val loadingManager = LoadingManager()
    private var _binding: FragmentTaxiBinding? = null
    private val binding get() = _binding!!
    private lateinit var osmManager: OsmManager
    private lateinit var adapter: PassengerAdapter
    private var timePickMode = TimePicker.PickerMode.RETURNING
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var favPlace: MutableList<TaxiFavPlaceModel>? = null
    private var originFavPlaceModel: TaxiFavPlaceModel? = null
    private var destinationFavPlaceModel: TaxiFavPlaceModel? = null
    private var favPlaceType = FavPlaceType.NONE

    //---------------------------------------------------------------------------------------------- FavClick
    enum class FavPlaceType {
        NONE,
        ORIGIN,
        DESTINATION
    }
    //---------------------------------------------------------------------------------------------- FavClick


    //---------------------------------------------------------------------------------------------- OnBackPressedCallback
    private val backClick = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (destinationMarker != null)
                removeDestinationMarker()
            else if (originMarker != null)
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
        initView()
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
        binding.textViewLoading.visibility = View.GONE
        loadingManager.stopLoadingView()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        binding.textViewLoading.visibility = View.GONE
        backClick.isEnabled = false
        loadingManager.stopLoadingView()
        unAuthorizationManager.handel(activity, type, message, binding.constraintLayoutParent)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        binding.imageViewMarker.setImageResource(R.drawable.ic_origin)
        setListener()
        setApplicatorNameToTextView()
        clickOnDepartureServiceTextView()
        backClickControl()
        setPassengersAdapter()
        requestGetTaxiFavPlace()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.textViewDeparture.setOnClickListener { clickOnDepartureServiceTextView() }
        binding.textViewReturning.setOnClickListener { clickOnReturningServicesTextView() }
        binding.buttonDepartureTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonReturnTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonDepartureDate.setOnClickListener { showDatePickerDialog(binding.buttonDepartureDate) }
        binding.buttonReturnDate.setOnClickListener { showDatePickerDialog(binding.buttonReturnDate) }
        binding.imageViewMarker.setOnClickListener { getCenterLocationOfMap() }
        binding.imageViewFavOrigin.setOnClickListener { clickImageviewFavOrigin() }
        binding.imageViewFavDestination.setOnClickListener { clickImageviewFavDestination() }
        binding.cardViewSearch.setOnClickListener { showDialogSearchAddress() }

        binding.powerSpinnerOrigin.setOnClickListener {
            originMarker?.let {
                ConfirmDialog(requireContext(),
                ConfirmDialog.ConfirmType.ADD,
                getString(R.string.originLocationIsSelect),
                object : ConfirmDialog.Click{
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
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- backClickControl
    private fun backClickControl() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backClick)
    }
    //---------------------------------------------------------------------------------------------- backClickControl


    //---------------------------------------------------------------------------------------------- requestGetTaxiFavPlace
    private fun requestGetTaxiFavPlace() {
        loadingManager.setViewLoading(
            binding.nestedScrollView,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow
        )
        taxiViewModel.requestGetTaxiFavPlace(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingView()
                response?.let {
                    if (it.hasError)
                        onError(EnumErrorType.UNKNOWN, it.message)
                    else {
                        it.data?.let { items ->
                            favPlace = items.toMutableList()
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
        val center = GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
        binding.textViewLoading.visibility = View.VISIBLE
        addressViewModel.getAddress(center).observe(viewLifecycleOwner) { response ->
            response?.let { info ->
                info.address?.let { address ->
                    if (originMarker == null) {
                        setAddressToTextview(address, binding.textViewOrigin)
                        addOriginMarker(GeoPoint(center.latitude, center.longitude))
                    } else if (destinationMarker == null) {
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
        timePickMode = TimePicker.PickerMode.DEPARTURE
    }
    //---------------------------------------------------------------------------------------------- clickOnDepartureServiceTextView


    //---------------------------------------------------------------------------------------------- clickOnReturningServicesTextView
    private fun clickOnReturningServicesTextView() {
        binding.textViewReturning.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewDeparture.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.constraintLayoutReturn.visibility = View.VISIBLE
        binding.textViewReturnTitle.visibility = View.VISIBLE
        timePickMode = TimePicker.PickerMode.RETURNING
    }
    //---------------------------------------------------------------------------------------------- clickOnReturningServicesTextView


    //---------------------------------------------------------------------------------------------- setApplicatorNameToTextView
    private fun setApplicatorNameToTextView() {
        CoroutineScope(IO).launch {
            val user = userInfoDao.getUserInfo()
            withContext(Main) {
                binding.textViewApplicator.text = getString(R.string.applicator, user?.fullName)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- setApplicatorNameToTextView


    //---------------------------------------------------------------------------------------------- showDatePickerDialog
    private fun showDatePickerDialog(button: MaterialButton) {
        val persianDate = LocalDateTime.now().toSolarDate()!!
        val typeface =
            Typeface.createFromAsset(requireContext().assets, "font/kalameh_medium.ttf")
        val picker = PersianDatePickerDialog(requireContext())
            .setPositiveButtonString(getString(R.string.choose))
            .setNegativeButton(getString(R.string.cancel))
            .setTodayButton(getString(R.string.todayDate))
            .setTodayButtonVisible(true)
            .setAllButtonsTextSize(14)
            .setTypeFace(typeface)
            .setMinYear(persianDate.getYear())
            .setMaxYear(persianDate.getYear() + 1)
            .setInitDate(persianDate.getYear(), persianDate.getMonth(), persianDate.getDay())
            .setBackgroundColor(
                resources.getColor(
                    R.color.dialogBackgroundColor,
                    requireContext().theme
                )
            )
            .setTitleColor(resources.getColor(R.color.textViewColor2, requireContext().theme))
            .setPickerBackgroundColor(
                resources.getColor(
                    R.color.dialogBackgroundColor,
                    requireContext().theme
                )
            )
            .setActionTextSize(20)
            .setActionTextColor(
                resources.getColor(
                    R.color.textViewColor2,
                    requireContext().theme
                )
            )
            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                    val select = getString(
                        R.string.setDate,
                        persianPickerDate.persianYear.toString(),
                        persianPickerDate.persianMonth.toString().padStart(2, '0'),
                        persianPickerDate.persianDay.toString().padStart(2, '0')
                    )
                    val today = persianDate.getSolarDate()
                    if (select < today)
                        onError(EnumErrorType.UNKNOWN, getString(R.string.selectedDateIsThePast))
                    else {
                        button.text = select
                        binding.buttonDepartureTime.text = null
                        binding.buttonReturnTime.text = null
                    }
                }

                override fun onDismissed() {}
            })

        picker.show()
    }
    //---------------------------------------------------------------------------------------------- showDatePickerDialog


    //---------------------------------------------------------------------------------------------- showTimePickerDialog
    private fun showTimePickerDialog() {
        if (binding.buttonDepartureDate.text.isNullOrEmpty()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheDepartureDate))
            return
        }

        if (timePickMode == TimePicker.PickerMode.RETURNING) {
            if (binding.buttonReturnDate.text.isNullOrEmpty()) {
                onError(EnumErrorType.UNKNOWN, getString(R.string.selectTheReturnDate))
                return
            }

            if (binding.buttonReturnDate.text.toString() < binding.buttonDepartureDate.text.toString()) {
                onError(EnumErrorType.UNKNOWN, getString(R.string.theReturnDateIsBeforeTheDepartureDate))
                return
            }
        }



        TimeDialog(requireContext(),
            timePickMode,
            click = object : TimeDialog.Click {
                override fun clickYes(timeDeparture: String, timeReturn: String) {
                    val time = LocalTime.now()
                    val currentTime = time.hour.toString().padStart(2, '0') +
                            ":" + time.minute.toString().padStart(2, '0')

                    if (timeDeparture < currentTime) {
                        onError(EnumErrorType.UNKNOWN, getString(R.string.selectedTimeIsThePast))
                        return
                    }
                    binding.buttonDepartureTime.text = timeDeparture
                    if (timePickMode == TimePicker.PickerMode.RETURNING) {
                        if (binding.buttonReturnDate.text.toString() > binding.buttonDepartureDate.text.toString())
                            binding.buttonReturnTime.text = timeReturn
                        else {
                            if (timeReturn < timeDeparture)
                                onError(EnumErrorType.UNKNOWN, getString(R.string.theReturnTimeIsBeforeTheDepartureTime))
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
        favPlace?.let {
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

                setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, newItem ->
                    originFavPlaceModel = it[newIndex]
                    binding.imageViewFavOrigin.setImageResource(R.drawable.ic_favorite)
                    addOriginMarker(GeoPoint(it[newIndex].lat, it[newIndex].long))
                    binding.powerSpinnerOrigin.setBackgroundResource(R.drawable.drawable_spinner_select)
                    binding.textViewOrigin.text = newItem.text
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
        favPlace?.let {
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
                setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, newItem ->
                    destinationFavPlaceModel = it[newIndex]
                    binding.imageViewFavDestination.setImageResource(R.drawable.ic_favorite)
                    addDestinationMarker(GeoPoint(GeoPoint(it[newIndex].lat, it[newIndex].long)))
                    binding.powerSpinnerDestination.setBackgroundResource(R.drawable.drawable_spinner_select)
                    binding.textViewDestination.text = newItem.text
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
        originMarker = osmManager.addMarker(
            icon,
            geoPoint,
            null
        )
        binding.imageViewMarker.setImageResource(R.drawable.ic_destination)
        osmManager.moveCameraZoomUp(geoPoint)
    }
    //---------------------------------------------------------------------------------------------- addOriginMarker


    //---------------------------------------------------------------------------------------------- removeOriginMarker
    private fun removeOriginMarker() {
        binding.textViewOrigin.isSelected = false
        osmManager.removeMarkerAndMove(originMarker!!)
        originMarker = null
        binding.imageViewFavOrigin.visibility = View.VISIBLE
        originFavPlaceModel = null
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
        destinationMarker = osmManager.addMarker(
            icon,
            geoPoint,
            null
        )
        binding.imageViewMarker.visibility = View.GONE
        val points = mutableListOf<GeoPoint>()
        points.add(originMarker!!.position)
        points.add(destinationMarker!!.position)
        val box = osmManager.getBoundingBoxFromPoints(points)
        binding.mapView.zoomToBoundingBox(box, true)
        binding.powerSpinnerDestination.isEnabled = false
    }
    //---------------------------------------------------------------------------------------------- addDestinationMarker


    //---------------------------------------------------------------------------------------------- removeDestinationMarker
    private fun removeDestinationMarker() {
        destinationFavPlaceModel = null
        binding.powerSpinnerDestination.isEnabled = true
        binding.imageViewFavDestination.visibility = View.VISIBLE
        binding.imageViewFavDestination.setImageResource(R.drawable.ic_favorite_outline)
        binding.textViewDestination.isSelected = false
        osmManager.removeMarkerAndMove(destinationMarker!!)
        destinationMarker = null
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
        CoroutineScope(IO).launch {
            val user = userInfoDao.getUserInfo()
            user?.let {
                users.add(it)
            }
        }

        val click = object : PassengerItemHolder.Click {
            override fun addClick() {
                showPersonnelDialog()
            }
        }
        adapter = PassengerAdapter(users, click)
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
        binding.recyclerViewPassengers.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setPassengersAdapter


    //---------------------------------------------------------------------------------------------- showPersonnelDialog
    private fun showPersonnelDialog() {
        val click = object : PersonnelDialog.Click {
            override fun select(item: UserInfoEntity) {
                val exist = adapter.getList()
                    .find { userInfoEntity -> userInfoEntity.userName == item.userName }
                if (exist == null)
                    adapter.addUser(item)
                else
                    onError(EnumErrorType.UNKNOWN, getString(R.string.duplicateInsert))
            }
        }
        PersonnelDialog(click).show(childFragmentManager, "personnel dialog")
    }
    //---------------------------------------------------------------------------------------------- showPersonnelDialog


    //---------------------------------------------------------------------------------------------- clickImageviewFavOrigin
    private fun clickImageviewFavOrigin() {
        originMarker?.let {
            favPlaceType = FavPlaceType.ORIGIN
            originFavPlaceModel?.let {
                if (destinationFavPlaceModel == null)
                    showRemoveFromFavPlaceDialog(it.locationName, it.id)
                else
                    onError(EnumErrorType.UNKNOWN, getString(R.string.destinationLocationSelected))
            } ?: run {
                showAddToFavPlaceDialog()
            }
        } ?: run {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseChooseLocation))
        }

    }
    //---------------------------------------------------------------------------------------------- clickImageviewFavOrigin


    //---------------------------------------------------------------------------------------------- clickImageviewFavDestination
    private fun clickImageviewFavDestination() {
        destinationMarker?.let {
            favPlaceType = FavPlaceType.DESTINATION
            destinationFavPlaceModel?.let {
                showRemoveFromFavPlaceDialog(it.locationName, it.id)
            } ?: run {
                showAddToFavPlaceDialog()
            }
        } ?: run {
            onError(EnumErrorType.UNKNOWN, getString(R.string.pleaseChooseLocation))
        }

    }
    //---------------------------------------------------------------------------------------------- clickImageviewFavDestination


    //---------------------------------------------------------------------------------------------- showAddToFavPlaceDialog
    private fun showAddToFavPlaceDialog() {
        context?.let {
            val click = object : FavPlaceDialog.Click {
                override fun clickSend(placeName: String) {
                    requestAddFavPlace(placeName)
                }
            }
            FavPlaceDialog(it, click).show()
        }
    }
    //---------------------------------------------------------------------------------------------- showAddToFavPlaceDialog


    //---------------------------------------------------------------------------------------------- requestAddFavPlace
    private fun requestAddFavPlace(placeName: String) {
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_repeat)
        var geoPoint: GeoPoint? = null
        when (favPlaceType) {
            FavPlaceType.ORIGIN -> {
                binding.imageViewFavOrigin.startAnimation(rotation)
                geoPoint = GeoPoint(originMarker?.position)
            }
            FavPlaceType.DESTINATION -> {
                binding.imageViewFavDestination.startAnimation(rotation)
                geoPoint = GeoPoint(destinationMarker?.position)
            }
            else -> {}
        }
        geoPoint?.let {
            val request = TaxiAddFavPlaceRequest(placeName, it.latitude, it.longitude)
            taxiViewModel.requestAddFavPlace(request, tokenViewModel.getBearerToken())
                .observe(viewLifecycleOwner) { response ->
                    binding.imageViewFavOrigin.clearAnimation()
                    binding.imageViewFavDestination.clearAnimation()
                    response?.let { data ->
                        onError(EnumErrorType.UNKNOWN, data.message)
                        if (!data.hasError) {
                            data.data?.let { element ->
                                if (favPlace == null)
                                    favPlace = mutableListOf()
                                favPlace!!.add(element)
                                initOriginSpinner()
                                initDestinationSpinner()
                            }
                            when (favPlaceType) {
                                FavPlaceType.ORIGIN -> {
                                    originFavPlaceModel = data.data
                                    binding
                                        .imageViewFavOrigin
                                        .setImageResource(R.drawable.ic_favorite)
                                }
                                FavPlaceType.DESTINATION -> {
                                    destinationFavPlaceModel = data.data
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
        context?.let {
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
    }
    //---------------------------------------------------------------------------------------------- showRemoveFromFavPlaceDialog


    //---------------------------------------------------------------------------------------------- requestRemoveFavPlace
    private fun requestRemoveFavPlace(id: Int) {
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_repeat)
        when (favPlaceType) {
            FavPlaceType.ORIGIN -> {
                binding.imageViewFavOrigin.startAnimation(rotation)
            }
            FavPlaceType.DESTINATION -> {
                binding.imageViewFavDestination.startAnimation(rotation)
            }
            else -> {}
        }
        taxiViewModel.requestDeleteFavPlace(id, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                binding.imageViewFavOrigin.clearAnimation()
                binding.imageViewFavDestination.clearAnimation()
                response?.let { it ->
                    onError(EnumErrorType.UNKNOWN, it.message)
                    if (!it.hasError) {
                        when (favPlaceType) {
                            FavPlaceType.ORIGIN -> removeOriginMarker()
                            FavPlaceType.DESTINATION -> removeDestinationMarker()
                            else -> {}
                        }
                        favPlace?.removeIf { it.id == id }
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


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onPause()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}