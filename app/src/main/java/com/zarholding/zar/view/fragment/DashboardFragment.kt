package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.recycler.adapter.DashboardAppAdapter
import com.zarholding.zar.view.recycler.holder.DashboardItemHolder
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentDashboardBinding

@AndroidEntryPoint
class DashboardFragment : Fragment(){

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
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



    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {

        val apps: MutableList<AppModel> = mutableListOf()

        apps.add(
            AppModel(
                R.drawable.ic_bus_transport,
                getString(R.string.service),
                R.id.action_DashboardFragment_to_AdminBusFragment
            )
        )

        apps.add(
            AppModel(
                R.drawable.ic_cab,
                getString(R.string.agency),
                R.id.action_DashboardFragment_to_AdminTaxiFragment
            )
        )

        apps.add(AppModel(R.drawable.icon_food_reservation, getString(R.string.foodReservation), 0))

        val click = object : DashboardItemHolder.Click {
            override fun appClick(action: Int) {
                if (action != 0)
                    findNavController().navigate(action)
            }
        }
        val adapter = DashboardAppAdapter(apps, click)
        val gridLayoutManager = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )

        binding.recyclerViewApps.layoutManager = gridLayoutManager
        binding.recyclerViewApps.layoutDirection = View.LAYOUT_DIRECTION_RTL
        binding.recyclerViewApps.adapter = adapter

    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}