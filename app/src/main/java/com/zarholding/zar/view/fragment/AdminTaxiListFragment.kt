package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.enum.EnumAdminTaxiType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.recycler.adapter.MyTaxiAdapter
import com.zarholding.zar.view.recycler.adapter.TaxiAdapter
import com.zarholding.zar.view.recycler.holder.TaxiHolder
import com.zarholding.zar.viewmodel.AdminTaxiListViewModel
import com.zarholding.zar.viewmodel.AdminTaxiViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentAdminTaxiListBinding
import javax.inject.Inject

@AndroidEntryPoint
class AdminTaxiListFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentAdminTaxiListBinding? = null
    private val binding get() = _binding!!

    private val adminTaxiListViewModel : AdminTaxiListViewModel by viewModels()

    @Inject
    lateinit var unAuthorizationManager: UnAuthorizationManager

    @Inject
    lateinit var loadingManager : LoadingManager


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
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
            getTaxiList()
        } ?: run {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
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
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        unAuthorizationManager.handel(activity, type, message, binding.constraintLayoutParent)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization



    //---------------------------------------------------------------------------------------------- getTaxiList
    private fun getTaxiList() {
        adminTaxiListViewModel.getEnumAdminTaxiType()?.let {
            startLoading()
            adminTaxiListViewModel.getTaxiList().observe(viewLifecycleOwner) {
                loadingManager.stopLoadingRecycler()
                it?.let { response ->
                    if (response.hasError)
                        onError(EnumErrorType.UNKNOWN, response.message)
                    else response.data?.let { items ->
                            setAdapter(items)
                    }
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- getTaxiList



    //---------------------------------------------------------------------------------------------- setAdapter
    private fun setAdapter(items : List<AdminTaxiRequestModel>){
        when(adminTaxiListViewModel.getEnumAdminTaxiType()!!) {
            EnumAdminTaxiType.REQUEST -> setTaxiRequestAdapter(items)
            EnumAdminTaxiType.HISTORY -> setMyTaxiRequestAdapter(items)
        }
    }
    //---------------------------------------------------------------------------------------------- setAdapter



    //---------------------------------------------------------------------------------------------- setMyTaxiRequestAdapter
    private fun setMyTaxiRequestAdapter(items : List<AdminTaxiRequestModel>) {
        if (context == null)
            return

        AdminTaxiViewModel.myTaxiLiveDate.value = items.size
        val adapter = MyTaxiAdapter(items)
        val manager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setMyTaxiRequestAdapter



    //---------------------------------------------------------------------------------------------- setTaxiRequestAdapter
    private fun setTaxiRequestAdapter(items : List<AdminTaxiRequestModel>) {
        if (context == null)
            return
        val click = object : TaxiHolder.Click {
            override fun accept(item: AdminTaxiRequestModel) {
            }

            override fun reject(item: AdminTaxiRequestModel) {
            }

        }
        AdminTaxiViewModel.requestTaxiLiveDate.value = items.size
        val adapter = TaxiAdapter(items, click)
        val manager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setTaxiRequestAdapter



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


}