package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.request.TripRequestRegisterStatusModel
import com.zarholding.zar.model.response.trip.TripRequestRegisterModel
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.RejectReasonDialog
import com.zarholding.zar.view.recycler.adapter.AdminBusAdapter
import com.zarholding.zar.view.recycler.holder.AdminBusItemHolder
import com.zarholding.zar.viewmodel.TokenViewModel
import com.zarholding.zar.viewmodel.TripViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.filterList
import zar.R
import zar.databinding.FragmentAdminBusBinding

@AndroidEntryPoint
class AdminBusFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentAdminBusBinding? = null
    private val binding get() = _binding!!

    private val tripViewModel: TripViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private val loadingManager = LoadingManager()
    lateinit var adapter: AdminBusAdapter


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentAdminBusBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setOnListener()
        requestGetTripRequestRegister()
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


    //---------------------------------------------------------------------------------------------- setOnListener
    private fun setOnListener() {

        binding.linearLayoutConfirm.setOnClickListener {
            requestConfirmAndRejectTripRequestRegister(EnumTripStatus.Done, null)
        }

        binding.linearLayoutReject.setOnClickListener {
            showDialogReasonOfReject()
        }

    }
    //---------------------------------------------------------------------------------------------- setOnListener


    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister
    private fun requestGetTripRequestRegister() {
        startLoading()
        tripViewModel.requestGetTripRequestRegister(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingRecycler()
                response?.let {
                    if (it.hasError)
                        onError(EnumErrorType.UNKNOWN, it.message)
                    else
                        it.data?.let { trips ->
                            setRequestAdapter(trips)
                        }
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetTripRequestRegister


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        loadingManager.setRecyclerLoading(
            binding.recyclerViewRequest,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- setRequestAdapter
    private fun setRequestAdapter(items: List<TripRequestRegisterModel>) {
        val choose = object : AdminBusItemHolder.Choose {
            override fun choose() {
                countChooseItem()
            }
        }

        adapter = AdminBusAdapter(items, choose)
        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.recyclerViewRequest.layoutManager = layoutManager
        binding.recyclerViewRequest.adapter = adapter
        countChooseItem()
    }
    //---------------------------------------------------------------------------------------------- setRequestAdapter


    //---------------------------------------------------------------------------------------------- countChooseItem
    private fun countChooseItem() {
        val choosed = adapter.getItems().filterList { choose }
        binding.textViewConfirmCount.text = choosed.size.toString()
        binding.textViewRejectCount.text = choosed.size.toString()
    }
    //---------------------------------------------------------------------------------------------- countChooseItem


    //---------------------------------------------------------------------------------------------- showDialogReasonOfReject
    private fun showDialogReasonOfReject() {
        val click = object : RejectReasonDialog.Click {
            override fun clickSend(reason: String) {
                requestConfirmAndRejectTripRequestRegister(EnumTripStatus.Reject, reason)
            }

        }
        RejectReasonDialog(requireContext(),click).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogReasonOfReject


    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister
    private fun requestConfirmAndRejectTripRequestRegister(status: EnumTripStatus, reason: String?) {
        val chosen = adapter.getItems().filterList { choose }
        if (chosen.isEmpty()) {
            onError(EnumErrorType.UNKNOWN, getString(R.string.chooseItemsIsEmpty))
            return
        }
        val request = mutableListOf<TripRequestRegisterStatusModel>()
        for (item in chosen)
            request.add(
                TripRequestRegisterStatusModel(
                    item.id,
                    status,
                    reason
                )
            )

        startLoading()
        tripViewModel.requestConfirmAndRejectTripRequestRegister(
            request,
            tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingRecycler()
                response?.let {
                    if (it.hasError)
                        onError(EnumErrorType.UNKNOWN, it.message)
                    else
                        requestGetTripRequestRegister()
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestConfirmAndRejectTripRequestRegister


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}