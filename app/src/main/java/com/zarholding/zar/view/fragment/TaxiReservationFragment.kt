package com.zarholding.zar.view.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.Size
import android.view.*
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
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.WentTimePicker
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.PersonnelDialog
import com.zarholding.zar.view.dialog.TimeDialog
import com.zarholding.zar.view.recycler.adapter.PassengerAdapter
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import com.zarholding.zar.viewmodel.AddressViewModel
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


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class TaxiReservationFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentTaxiBinding? = null
    private val binding get() = _binding!!

    private lateinit var osmManager: OsmManager
    private lateinit var adapter: PassengerAdapter

    private val addressViewModel: AddressViewModel by viewModels()

    @Inject
    lateinit var themeManagers: ThemeManager

    @Inject
    lateinit var userInfoDao: UserInfoDao

    @Inject
    lateinit var unAuthorizationManager: UnAuthorizationManager

    var timePickMode = WentTimePicker.PickerMode.WENT_FORTH

    var originMarker: Marker? = null
    var destinationMarker: Marker? = null


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
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        binding.textViewLoading.visibility = View.GONE
        backClick.isEnabled = false
        unAuthorizationManager.handel(activity, type, message, binding.constraintLayoutParent)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        binding.imageViewMarker.setImageResource(R.drawable.icon_start_marker)
        setListener()
        setApplicatorNameToTextView()
        initOriginSpinner()
        initDestinationSpinner()
        clickOnWentServiceTextView()
        backClickControl()
        setPassengersAdapter()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.textViewWent.setOnClickListener { clickOnWentServiceTextView() }
        binding.textViewWentAndForth.setOnClickListener { clickOnWentAndForthServicesTextView() }
        binding.buttonWentTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonForthTime.setOnClickListener { showTimePickerDialog() }
        binding.buttonDate.setOnClickListener { showDatePickerDialog() }
        binding.imageViewMarker.setOnClickListener { getCenterLocationOfMap() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- backClickControl
    private fun backClickControl() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backClick)
    }
    //---------------------------------------------------------------------------------------------- backClickControl


    //---------------------------------------------------------------------------------------------- getCenterLocationOfMap
    private fun getCenterLocationOfMap() {
        val center = binding.mapView.mapCenter
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
        var addressText = ""

        var county = address.county
        county = county?.replace("شهرستان", "")
        county = county?.replace("شهر", "")
        county = county?.trimStart()
        county = county?.trimEnd()
        if (county == null)
            county = ""

        var city = address.city
        city = city?.replace("شهرستان", "")
        city = city?.replace("شهر", "")
        city = city?.trimStart()
        city = city?.trimEnd()
        if (city == null)
            city = ""

        var town = address.town
        town = town?.replace("شهرستان", "")
        town = town?.replace("شهر", "")
        town = town?.trimStart()
        town = town?.trimEnd()
        if (town == null)
            town = ""

        addressText = if (city in county)
            county
        else if (county in city)
            city
        else
            "$county , $city"

        if (town !in addressText)
            addressText += " , $town"


        address.neighbourhood?.let {
            addressText += " , $it"
        }

        address.road?.let {
            addressText += " , $it"
        }

        textView.isSelected = true
        textView.text = addressText
        textView.setTextColor(resources.getColor(R.color.primaryColor, context?.theme))
    }
    //---------------------------------------------------------------------------------------------- setAddressToTextview


    //---------------------------------------------------------------------------------------------- clickOnWentServiceTextView
    private fun clickOnWentServiceTextView() {
        binding.textViewWent.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewWentAndForth.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.textViewForthTimeTitle.visibility = View.INVISIBLE
        binding.buttonForthTime.visibility = View.INVISIBLE
        timePickMode = WentTimePicker.PickerMode.WENT
    }
    //---------------------------------------------------------------------------------------------- clickOnWentServiceTextView


    //---------------------------------------------------------------------------------------------- clickOnWentAndForthServicesTextView
    private fun clickOnWentAndForthServicesTextView() {
        binding.textViewWentAndForth.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewWent.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.textViewForthTimeTitle.visibility = View.VISIBLE
        binding.buttonForthTime.visibility = View.VISIBLE
        timePickMode = WentTimePicker.PickerMode.WENT_FORTH
    }
    //---------------------------------------------------------------------------------------------- clickOnWentAndForthServicesTextView


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
    private fun showDatePickerDialog() {
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
            .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
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
                    binding.buttonDate.text = getString(
                        R.string.setDate,
                        persianPickerDate.persianYear.toString(),
                        persianPickerDate.persianMonth.toString(),
                        persianPickerDate.persianDay.toString()
                    )
                }

                override fun onDismissed() {}
            })

        picker.show()
    }
    //---------------------------------------------------------------------------------------------- showDatePickerDialog


    //---------------------------------------------------------------------------------------------- showTimePickerDialog
    private fun showTimePickerDialog() {
        TimeDialog(requireContext(),
            timePickMode,
            click = object : TimeDialog.Click {
                override fun clickYes(timeWent: String, timeForth: String) {
                    binding.buttonWentTime.text = timeWent
                    if (timePickMode == WentTimePicker.PickerMode.WENT_FORTH)
                        binding.buttonForthTime.text = timeForth
                }
            }).show()
    }
    //---------------------------------------------------------------------------------------------- showTimePickerDialog


    //---------------------------------------------------------------------------------------------- initOriginSpinner
    private fun initOriginSpinner() {
        binding.powerSpinnerOrigin.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem("کارخانه زر ماکارون"),
                    IconSpinnerItem("کارخانه زر نام"),
                    IconSpinnerItem("کارخانه زر کام")
                )
            )
            getSpinnerRecyclerView().layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            lifecycleOwner = viewLifecycleOwner

            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
                addOriginMarker(GeoPoint(35.90576170621037, 50.819428313684675))
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
    //---------------------------------------------------------------------------------------------- initOriginSpinner


    //---------------------------------------------------------------------------------------------- initDestinationSpinner
    private fun initDestinationSpinner() {
        binding.powerSpinnerDestination.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem("کارخانه زر ماکارون"),
                    IconSpinnerItem("کارخانه زر نام"),
                    IconSpinnerItem("کارخانه زر کام")
                )
            )
            getSpinnerRecyclerView().layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            lifecycleOwner = viewLifecycleOwner
            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
                addDestinationMarker(GeoPoint(35.82659993663358, 50.995642063037785))
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
    //---------------------------------------------------------------------------------------------- initDestinationSpinner


    //---------------------------------------------------------------------------------------------- addOriginMarker
    private fun addOriginMarker(geoPoint: GeoPoint) {
        binding.textViewLoading.visibility = View.GONE
        val icon = osmManager.createMarkerIconDrawable(
            Size(100, 97),
            R.drawable.icon_start_marker
        )
        originMarker = osmManager.addMarker(
            icon,
            geoPoint,
            null
        )
        binding.imageViewMarker.setImageResource(R.drawable.icon_end_marker)
        osmManager.moveCameraZoomUp(geoPoint)
    }
    //---------------------------------------------------------------------------------------------- addOriginMarker


    //---------------------------------------------------------------------------------------------- removeOriginMarker
    private fun removeOriginMarker() {
        binding.textViewOrigin.isSelected = false
        osmManager.removeMarkerAndMove(originMarker!!)
        originMarker = null
        binding.imageViewMarker.setImageResource(R.drawable.icon_start_marker)
        binding.powerSpinnerOrigin.clearSelectedItem()
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
            Size(100, 97),
            R.drawable.icon_end_marker
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
    }
    //---------------------------------------------------------------------------------------------- addDestinationMarker


    //---------------------------------------------------------------------------------------------- removeDestinationMarker
    private fun removeDestinationMarker() {
        binding.textViewDestination.isSelected = false
        osmManager.removeMarkerAndMove(destinationMarker!!)
        destinationMarker = null
        binding.imageViewMarker.visibility = View.VISIBLE
        binding.imageViewMarker.setImageResource(R.drawable.icon_end_marker)
        binding.powerSpinnerDestination.clearSelectedItem()
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


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onPause()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}