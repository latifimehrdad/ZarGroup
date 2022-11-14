package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.recycler.adapter.AppAdapter
import zar.R
import zar.databinding.FragmentHomeBinding
import zar.databinding.FragmentLoginBinding

/**
 * Created by m-latifi on 11/13/2022.
 */

class HomeFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initApps()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- initApps
    private fun initApps() {
        val apps: MutableList<AppModel> = mutableListOf()
        apps.add(AppModel(R.drawable.icon_trip, getString(R.string.tripAndMap), 1))
        apps.add(AppModel(R.drawable.icon_personnel, getString(R.string.personnelList), 1))
        apps.add(AppModel(R.drawable.icon_food_reservation, getString(R.string.foodReservation), 0))
        apps.add(AppModel(R.drawable.icon_trip, getString(R.string.tripAndMap), 0))
        setAppsAdapter(apps)
    }
    //---------------------------------------------------------------------------------------------- initApps



    //---------------------------------------------------------------------------------------------- setAppsAdapter
    private fun setAppsAdapter(apps: MutableList<AppModel>) {
        val adapter = AppAdapter(apps)

        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        linearLayoutManager.reverseLayout = true

        binding.recyclerViewApps.layoutManager = linearLayoutManager
        binding.recyclerViewApps.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setAppsAdapter



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}