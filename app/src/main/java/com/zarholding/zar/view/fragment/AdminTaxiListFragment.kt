package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.enum.EnumAdminTaxiType
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
import com.zarholding.zar.view.recycler.holder.AdminTaxiRequestHolder
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

    private var myTaxiRequestAdapter : MyTaxiAdapter? = null
    private var adminTaxiRequestAdapter : AdminTaxiRequestAdapter? = null

    var endlessScrollListener: EndlessScrollListener? = null


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
            EnumAdminTaxiType.MY -> setMyTaxiRequestAdapter(items)
            EnumAdminTaxiType.REQUEST -> setTaxiRequestAdapter(items)
            EnumAdminTaxiType.HISTORY -> {}
        }
    }
    //---------------------------------------------------------------------------------------------- setAdapter


    //---------------------------------------------------------------------------------------------- setMyTaxiRequestAdapter
    private fun setMyTaxiRequestAdapter(items: List<AdminTaxiRequestModel>) {
        if (context == null)
            return
        AdminTaxiViewModel.myTaxiLiveDate.value = items.size
        myTaxiRequestAdapter?.let { adapter ->
            adapter.addRequest(items)
            endlessScrollListener?.let {
                it.setLoading(false)
                if (items.isEmpty())
                    binding.recyclerView.removeOnScrollListener(it)
            }
        } ?: run {
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
        AdminTaxiViewModel.requestTaxiLiveDate.value = items.size
        adminTaxiRequestAdapter?.let { adapter ->
            adapter.addRequest(items)
            endlessScrollListener?.let {
                it.setLoading(false)
                if (items.isEmpty())
                    binding.recyclerView.removeOnScrollListener(it)
            }
        } ?: run {
            val click = object : AdminTaxiRequestHolder.Click {
                override fun accept(item: AdminTaxiRequestModel) {
                    if (adminTaxiListViewModel.isAdministrativeUser())
                        showDialogChooseDriver(item)
                    else
                        showDialogConfirm(item)
                }

                override fun reject(item: AdminTaxiRequestModel) {
                    showDialogReasonOfReject(item)
                }

            }
            adminTaxiRequestAdapter = AdminTaxiRequestAdapter(items.toMutableList(),
                click, adminTaxiListViewModel.isDriver())
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
                requestChangeStatusOfTaxiRequests(request)
            }
        }
        RejectReasonDialog(requireContext(), click).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogReasonOfReject



    //---------------------------------------------------------------------------------------------- showDialogChooseDriver
    private fun showDialogChooseDriver(item: AdminTaxiRequestModel) {
        val click = object : DriverDialog.Click {
            override fun clickYes(message : String) {
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
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}