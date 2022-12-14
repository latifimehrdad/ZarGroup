package com.zarholding.zar.view.fragment.taxi

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.enum.EnumAdminTaxiType
import com.zarholding.zar.model.enum.EnumPersonnelType
import com.zarholding.zar.model.enum.EnumTaxiRequestStatus
import com.zarholding.zar.model.request.TaxiChangeStatusRequest
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.EndlessScrollListener
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.ConfirmDialog
import com.zarholding.zar.view.dialog.DriverDialog
import com.zarholding.zar.view.dialog.RejectReasonDialog
import com.zarholding.zar.view.recycler.adapter.MyTaxiAdapter
import com.zarholding.zar.view.recycler.adapter.AdminTaxiRequestAdapter
import com.zarholding.zar.view.recycler.adapter.DriverTaxiRequestAdapter
import com.zarholding.zar.view.recycler.holder.AdminTaxiRequestHolder
import com.zarholding.zar.view.recycler.holder.DriverTaxiRequestHolder
import com.zarholding.zar.viewmodel.AdminTaxiListViewModel
import com.zarholding.zar.viewmodel.AdminTaxiViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentAdminTaxiListBinding
import javax.inject.Inject

@AndroidEntryPoint
class AdminTaxiListFragment : Fragment() {

    @Inject
    lateinit var loadingManager: LoadingManager

    private var _binding: FragmentAdminTaxiListBinding? = null
    private val binding get() = _binding!!

    private val adminTaxiListViewModel: AdminTaxiListViewModel by viewModels()
    private var myTaxiRequestAdapter: MyTaxiAdapter? = null
    private var adminTaxiRequestAdapter: AdminTaxiRequestAdapter? = null
    private var driverTaxiRequestAdapter: DriverTaxiRequestAdapter? = null
    private var endlessScrollListener: EndlessScrollListener? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminTaxiListBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        arguments?.let {
            val type = it.getString(CompanionValues.adminTaxiType, null)
            type?.let {
                adminTaxiListViewModel.setEnumAdminTaxiType(type)
                observeErrorLiveDate()
                observeTaxiRequestListLiveData()
                startLoading()
                adminTaxiListViewModel.getTaxiList()
            } ?: run {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        } ?: run {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
        binding.textViewLoading.visibility = View.GONE
    }
    //---------------------------------------------------------------------------------------------- showMessage


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        adminTaxiListViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()
                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate


    //---------------------------------------------------------------------------------------------- observeTaxiRequestListLiveData
    private fun observeTaxiRequestListLiveData() {
        adminTaxiListViewModel.taxiRequestListLiveData.observe(viewLifecycleOwner) {
            binding.textViewLoading.visibility = View.GONE
            setAdapter(it)
        }
    }
    //---------------------------------------------------------------------------------------------- observeTaxiRequestListLiveData


    //---------------------------------------------------------------------------------------------- setAdapter
    private fun setAdapter(items: List<AdminTaxiRequestModel>) {
        when (adminTaxiListViewModel.getEnumAdminTaxiType()) {
            EnumAdminTaxiType.MY,
            EnumAdminTaxiType.HISTORY
            -> setMyTaxiRequestAdapter(items)
            EnumAdminTaxiType.REQUEST -> {
                when (adminTaxiListViewModel.getUserType()) {
                    EnumPersonnelType.Driver -> setDriverTaxiRequestAdapter(items)
                    else -> setTaxiRequestAdapter(items)
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- setAdapter


    //---------------------------------------------------------------------------------------------- setMyTaxiRequestAdapter
    private fun setMyTaxiRequestAdapter(items: List<AdminTaxiRequestModel>) {
        if (context == null)
            return
        myTaxiRequestAdapter?.let { adapter ->
            val count = AdminTaxiViewModel.myTaxiLiveDate.value ?: run { 0 }
            AdminTaxiViewModel.myTaxiLiveDate.value = count + items.size
            adapter.addRequest(items)
            endlessScrollListener?.let {
                it.setLoading(false)
                if (items.isEmpty())
                    binding.recyclerView.removeOnScrollListener(it)
            }
        } ?: run {
            AdminTaxiViewModel.myTaxiLiveDate.value = items.size
            myTaxiRequestAdapter = MyTaxiAdapter(items.toMutableList())
            val manager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            endlessScrollListener = getEndlessScrollListener(manager)
            binding.recyclerView.layoutManager = manager
            binding.recyclerView.adapter = myTaxiRequestAdapter
            binding.recyclerView.addOnScrollListener(endlessScrollListener!!)
        }
    }
    //---------------------------------------------------------------------------------------------- setMyTaxiRequestAdapter


    //---------------------------------------------------------------------------------------------- setTaxiRequestAdapter
    private fun setTaxiRequestAdapter(items: List<AdminTaxiRequestModel>) {
        if (context == null)
            return
        adminTaxiRequestAdapter?.let { adapter ->
            val count = AdminTaxiViewModel.requestTaxiLiveDate.value ?: run { 0 }
            AdminTaxiViewModel.requestTaxiLiveDate.value = count + items.size
            adapter.addRequest(items)
            endlessScrollListener?.let {
                it.setLoading(false)
                if (items.isEmpty())
                    binding.recyclerView.removeOnScrollListener(it)
            }
        } ?: run {
            val click = object : AdminTaxiRequestHolder.Click {
                override fun accept(item: AdminTaxiRequestModel) {
                    when (adminTaxiListViewModel.getUserType()) {
                        EnumPersonnelType.Administrative -> showDialogChooseDriver(item)
                        else -> showDialogConfirm(item)
                    }
                }

                override fun reject(item: AdminTaxiRequestModel) {
                    showDialogReasonOfReject(item)
                }

            }
            AdminTaxiViewModel.requestTaxiLiveDate.value = items.size
            adminTaxiRequestAdapter = AdminTaxiRequestAdapter(items.toMutableList(), click)
            val manager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            endlessScrollListener = getEndlessScrollListener(manager)
            binding.recyclerView.layoutManager = manager
            binding.recyclerView.adapter = adminTaxiRequestAdapter
            binding.recyclerView.addOnScrollListener(endlessScrollListener!!)
        }
    }
    //---------------------------------------------------------------------------------------------- setTaxiRequestAdapter


    //---------------------------------------------------------------------------------------------- setDriverTaxiRequestAdapter
    private fun setDriverTaxiRequestAdapter(items: List<AdminTaxiRequestModel>) {
        if (context == null)
            return
        driverTaxiRequestAdapter?.let { adapter ->
            val count = AdminTaxiViewModel.requestTaxiLiveDate.value ?: run { 0 }
            AdminTaxiViewModel.requestTaxiLiveDate.value = count + items.size
            adapter.addRequest(items)
            endlessScrollListener?.let {
                it.setLoading(false)
                if (items.isEmpty())
                    binding.recyclerView.removeOnScrollListener(it)
            }
        } ?: run {
            val click = object : DriverTaxiRequestHolder.Click {
                override fun showOnMap(
                    latOrigin: Double,
                    lngOrigin: Double,
                    latDestination: Double,
                    lngDestination: Double
                ) {
                    val bundle = Bundle()
                    bundle.putDouble(CompanionValues.latOrigin, latOrigin)
                    bundle.putDouble(CompanionValues.lngOrigin, lngOrigin)
                    bundle.putDouble(CompanionValues.latDestination, latDestination)
                    bundle.putDouble(CompanionValues.lngDestination, lngDestination)
                    findNavController()
                        .navigate(R.id.action_AdminTaxiFragment_to_MapFragment, bundle)
                }

                override fun changeTripStatus(position: Int) {
                    requestLocationPermission(position)
                }
            }
            AdminTaxiViewModel.requestTaxiLiveDate.value = items.size
            driverTaxiRequestAdapter = DriverTaxiRequestAdapter(items.toMutableList(), click)
            val manager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            endlessScrollListener = getEndlessScrollListener(manager)
            binding.recyclerView.layoutManager = manager
            binding.recyclerView.adapter = driverTaxiRequestAdapter
            binding.recyclerView.addOnScrollListener(endlessScrollListener!!)
        }
    }
    //---------------------------------------------------------------------------------------------- setDriverTaxiRequestAdapter


    //---------------------------------------------------------------------------------------------- requestLocationPermission
    private fun requestLocationPermission(position: Int) {
        if (context == null)
            return
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    getCurrentLocation(position)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }
            })
            .check()


    }
    //---------------------------------------------------------------------------------------------- requestLocationPermission


    //---------------------------------------------------------------------------------------------- getCurrentLocation
    private fun getCurrentLocation(position: Int) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }).addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(requireContext(), "Cannot get location.", Toast.LENGTH_SHORT)
                        .show()
                else {
                    val lat = location.latitude
                    val lon = location.longitude
                    requestDriverChangeTripStatus(position, lat, lon)
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- getCurrentLocation


    //---------------------------------------------------------------------------------------------- requestDriverChangeTripStatus
    private fun requestDriverChangeTripStatus(position: Int, lat: Double, lon: Double) {
        driverTaxiRequestAdapter?.let {
            startLoading()
            adminTaxiListViewModel.requestDriverChangeTripStatus(
                it.getList()[position], lat, lon
            )
        }
    }
    //---------------------------------------------------------------------------------------------- requestDriverChangeTripStatus


    //______________________________________________________________________________________________ getEndlessScrollListener
    private fun getEndlessScrollListener(manager: LinearLayoutManager): EndlessScrollListener {
        val endlessScrollListener = object : EndlessScrollListener(manager) {
            override fun loadMoreItems() {
                binding.textViewLoading.visibility = View.VISIBLE
                adminTaxiListViewModel.getTaxiList()
            }
        }
        endlessScrollListener.setLoading(false)
        return endlessScrollListener
    }
    //______________________________________________________________________________________________ getEndlessScrollListener


    //---------------------------------------------------------------------------------------------- showDialogReasonOfReject
    private fun showDialogReasonOfReject(item: AdminTaxiRequestModel) {
        val click = object : RejectReasonDialog.Click {
            override fun clickSend(reason: String) {
                val request = TaxiChangeStatusRequest(
                    item.id,
                    EnumTaxiRequestStatus.Reject,
                    reason,
                    item.personnelJobKeyCode,
                    item.fromCompany
                )
                adminTaxiRequestAdapter = null
                endlessScrollListener = null
                requestChangeStatusOfTaxiRequests(request)
            }
        }
        RejectReasonDialog(requireContext(), click).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogReasonOfReject


    //---------------------------------------------------------------------------------------------- showDialogChooseDriver
    private fun showDialogChooseDriver(item: AdminTaxiRequestModel) {
        val click = object : DriverDialog.Click {
            override fun clickYes(message: String) {
                adminTaxiRequestAdapter = null
                endlessScrollListener = null
                adminTaxiListViewModel.setPageNumberToZero()
                showMessage(message)
                adminTaxiListViewModel.getTaxiList()
            }
        }
        DriverDialog(click, item).show(childFragmentManager, "driver")
    }
    //---------------------------------------------------------------------------------------------- showDialogChooseDriver


    //---------------------------------------------------------------------------------------------- showDialogConfirm
    private fun showDialogConfirm(item: AdminTaxiRequestModel) {
        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                adminTaxiListViewModel.getUser()?.let {
                    val request = TaxiChangeStatusRequest(
                        item.id,
                        EnumTaxiRequestStatus.Confirmed,
                        null,
                        it.personnelJobKeyCode,
                        item.fromCompany
                    )
                    adminTaxiRequestAdapter = null
                    endlessScrollListener = null
                    requestChangeStatusOfTaxiRequests(request)
                } ?: run {
                    (activity as MainActivity?)?.gotoFirstFragment()
                }
            }
        }
        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.ADD,
            getString(R.string.confirmForTaxiRequestRegister, item.requesterName),
            click
        ).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogConfirm


    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests
    private fun requestChangeStatusOfTaxiRequests(request: TaxiChangeStatusRequest) {
        startLoading()
        adminTaxiListViewModel.requestChangeStatusOfTaxiRequests(request)
    }
    //---------------------------------------------------------------------------------------------- requestChangeStatusOfTaxiRequests


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        loadingManager.setRecyclerLoading(
            binding.recyclerView,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        adminTaxiListViewModel.setPageNumberToZero()
        AdminTaxiViewModel.requestTaxiLiveDate.value = 0
        AdminTaxiViewModel.myTaxiLiveDate.value = 0
        adminTaxiRequestAdapter = null
        endlessScrollListener = null
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}