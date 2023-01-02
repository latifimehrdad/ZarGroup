package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
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
import org.osmdroid.util.GeoPoint
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
    private var driverTaxiRequestAdapter : DriverTaxiRequestAdapter? = null

    private var endlessScrollListener: EndlessScrollListener? = null


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
            adminTaxiListViewModel
                .setEnumAdminTaxiType(it.getString(CompanionValues.adminTaxiType, null))
            observeErrorLiveDate()
            observeTaxiRequestListLiveData()
            startLoading()
            getTaxiList()
        } ?: run {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
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
        loadingManager.stopLoadingRecycler()
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


    //---------------------------------------------------------------------------------------------- getTaxiList
    private fun getTaxiList() {
        adminTaxiListViewModel.getEnumAdminTaxiType()?.let {
            adminTaxiListViewModel.getTaxiList()
        }
    }
    //---------------------------------------------------------------------------------------------- getTaxiList


    //---------------------------------------------------------------------------------------------- setAdapter
    private fun setAdapter(items: List<AdminTaxiRequestModel>) {
        when (adminTaxiListViewModel.getEnumAdminTaxiType()!!) {
            EnumAdminTaxiType.MY,
            EnumAdminTaxiType.HISTORY
            -> setMyTaxiRequestAdapter(items)
            EnumAdminTaxiType.REQUEST -> {
                when(adminTaxiListViewModel.getUserType()) {
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
                    bundle.putDouble("latOrigin", latOrigin)
                    bundle.putDouble("lngOrigin", lngOrigin)
                    bundle.putDouble("latDestination", latDestination)
                    bundle.putDouble("lngDestination", lngDestination)

                    findNavController()
                        .navigate(R.id.action_AdminTaxiFragment_to_MapFragment, bundle)
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



    //______________________________________________________________________________________________ getEndlessScrollListener
    private fun getEndlessScrollListener(manager: LinearLayoutManager): EndlessScrollListener {
        val endlessScrollListener = object : EndlessScrollListener(manager) {
            override fun loadMoreItems() {
                binding.textViewLoading.visibility = View.VISIBLE
                getTaxiList()
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
                getTaxiList()
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