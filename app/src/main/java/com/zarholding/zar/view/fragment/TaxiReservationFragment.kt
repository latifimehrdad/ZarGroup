package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.view.WentTimePicker
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.TimeDialog
import com.zarholding.zar.view.recycler.adapter.DashboardAppAdapter
import com.zarholding.zar.view.recycler.adapter.PassengerAdapter
import com.zarholding.zar.view.recycler.holder.DashboardItemHolder
import com.zarholding.zar.view.recycler.holder.PassengerItemHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zar.R
import zar.databinding.FragmentTaxiBinding
import javax.inject.Inject


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class TaxiReservationFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentTaxiBinding? = null
    private val binding get() = _binding!!

    private lateinit var osmManager: OsmManager
    private lateinit var adapter : PassengerAdapter

    @Inject
    lateinit var themeManagers: ThemeManager

    @Inject
    lateinit var userInfoDao: UserInfoDao

    var timePickMode = WentTimePicker.PickerMode.WENT_FORTH


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
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
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
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {

    }
    //---------------------------------------------------------------------------------------------- unAuthorization



    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        initApplicatorTextView()
        initOriginSpinner()
        initDestinationSpinner()
        binding.textViewWent.setOnClickListener { selectWentService() }
        binding.textViewWentAndForth.setOnClickListener { selectWentForthServices() }
        binding.buttonWentTime.setOnClickListener { showTimePickerDialog()}
        binding.buttonForthTime.setOnClickListener { showTimePickerDialog() }
        selectWentService()
        backClickControl()
        setPassengersAdapter()
    }
    //---------------------------------------------------------------------------------------------- initView



    //---------------------------------------------------------------------------------------------- backClickControl
    private fun backClickControl() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })
    }
    //---------------------------------------------------------------------------------------------- backClickControl



    //---------------------------------------------------------------------------------------------- selectWentService
    private fun selectWentService() {
        binding.textViewWent.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewWentAndForth.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.textViewForthTimeTitle.visibility = View.INVISIBLE
        binding.buttonForthTime.visibility = View.INVISIBLE
        timePickMode = WentTimePicker.PickerMode.WENT
    }
    //---------------------------------------------------------------------------------------------- selectWentService


    //---------------------------------------------------------------------------------------------- selectWentForthServices
    private fun selectWentForthServices() {
        binding.textViewWentAndForth.setBackgroundResource(R.drawable.drawable_trip_select_button)
        binding.textViewWent.setBackgroundResource(R.drawable.drawable_trip_unselect_button)
        binding.textViewForthTimeTitle.visibility = View.VISIBLE
        binding.buttonForthTime.visibility = View.VISIBLE
        timePickMode = WentTimePicker.PickerMode.WENT_FORTH
    }
    //---------------------------------------------------------------------------------------------- selectWentForthServices


    //---------------------------------------------------------------------------------------------- initApplicatorTextView
    private fun initApplicatorTextView() {
        CoroutineScope(IO).launch {
            val user = userInfoDao.getUserInfo()
            withContext(Main) {
                binding.textViewApplicator.text = getString(R.string.applicator, user?.fullName)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initApplicatorTextView


    //---------------------------------------------------------------------------------------------- showTimePickerDialog
    private fun showTimePickerDialog(){
/*        TimeMultiDialog2(
            timePickMode,
            object : TimeMultiDialog2.Click {
                override fun clickYes(timeWent: String, timeForth: String) {
                    binding.buttonWentTime.text = timeWent
                    if (timePickMode == WentTimePicker.PickerMode.WENT_FORTH)
                        binding.buttonForthTime.text = timeForth
                }
            }).show(childFragmentManager, "Alert Dialog")*/


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
        }

        binding.powerSpinnerOrigin
            .setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
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
        }

        binding.powerSpinnerDestination
            .setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
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
    //---------------------------------------------------------------------------------------------- initDestinationSpinner



    //---------------------------------------------------------------------------------------------- setPassengersAdapter
    private fun setPassengersAdapter() {
        val apps: MutableList<AppModel> = mutableListOf()



        apps.add(AppModel(R.drawable.icon_personnel, "مهرداد لطیفی 1 محمد لطیفی", 0))
        apps.add(AppModel(R.drawable.icon_personnel, "علی سلیمانی", 0))
        apps.add(AppModel(R.drawable.icon_personnel, "حامد آزاد", 0))
        apps.add(AppModel(R.drawable.icon_personnel, "امیر رادمان نژاد", 0))
        apps.add(AppModel(R.drawable.icon_personnel, "محمد امین قنبری", 0))
        apps.add(AppModel(R.drawable.icon_personnel, "سعید اصغری", 0))


        val click = object : PassengerItemHolder.Click {
            override fun appClick(action: Int) {
                apps.add(AppModel(R.drawable.icon_personnel, "مهسا داننده", 0))
                adapter.notifyDataSetChanged()

            }
        }
        adapter = PassengerAdapter(apps, click)
        val gridLayoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false
        )
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if (position == apps.size-1)
                    2
                else
                    1
            }
        }
        binding.recyclerViewPassengers.layoutManager = gridLayoutManager
        binding.recyclerViewPassengers.layoutDirection = View.LAYOUT_DIRECTION_RTL
        binding.recyclerViewPassengers.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setPassengersAdapter



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onPause()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}