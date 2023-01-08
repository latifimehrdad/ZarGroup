package com.zarholding.zar.view.fragment.taxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.zarholding.zar.model.enum.EnumAdminTaxiType
import com.zarholding.zar.model.enum.EnumPersonnelType
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.adapter.ViewPagerAdapter
import com.zarholding.zar.viewmodel.AdminTaxiViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentAdminTaxiBinding

@AndroidEntryPoint
class AdminTaxiFragment : Fragment() {

    private var _binding: FragmentAdminTaxiBinding? = null
    private val binding get() = _binding!!

    private val adminTaxiViewModel: AdminTaxiViewModel by viewModels()
    private var isMyRequest = false


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminTaxiBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        isMyRequest = arguments?.getBoolean(CompanionValues.myRequest, false) ?: false
        setFragmentToViewPager()
        observeLiveData()
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
    }
    //---------------------------------------------------------------------------------------------- showMessage


    //---------------------------------------------------------------------------------------------- setFragmentToViewPager
    private fun setFragmentToViewPager() {
        if (isMyRequest)
            setFragmentMyRequestHistory()
        else
            setFragmentRequest()
    }
    //---------------------------------------------------------------------------------------------- setFragmentToViewPager


    //---------------------------------------------------------------------------------------------- setFragmentRequest
    private fun setFragmentRequest() {
        when(adminTaxiViewModel.getUserType()) {
            EnumPersonnelType.Administrative -> setAdministrativeFragment()
            else -> setNotAdministrativeFragment()
        }
    }
    //---------------------------------------------------------------------------------------------- setFragmentRequest


    //---------------------------------------------------------------------------------------------- setFragmentMyRequestHistory
    private fun setFragmentMyRequestHistory() {
        val adapter = ViewPagerAdapter(this@AdminTaxiFragment)
        val myRequestBundle = Bundle()
        myRequestBundle.putString(CompanionValues.adminTaxiType, EnumAdminTaxiType.MY.name)
        val myRequestFragment = AdminTaxiListFragment().apply {
            arguments = myRequestBundle
        }
        adapter.addFragment(myRequestFragment, getString(R.string.myRequests))
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }
    //---------------------------------------------------------------------------------------------- setFragmentMyRequestHistory


    //---------------------------------------------------------------------------------------------- setAdministrativeFragment
    private fun setAdministrativeFragment() {
        val adapter = ViewPagerAdapter(this@AdminTaxiFragment)
        val requestBundle = Bundle()
        val historyBundle = Bundle()
        requestBundle.putString(CompanionValues.adminTaxiType, EnumAdminTaxiType.REQUEST.name)
        historyBundle.putString(CompanionValues.adminTaxiType, EnumAdminTaxiType.HISTORY.name)
        val requestFragment = AdminTaxiListFragment().apply {
            arguments = requestBundle
        }
        val historyFragment = AdminTaxiListFragment().apply {
            arguments = historyBundle
        }
        adapter.addFragment(requestFragment, getString(R.string.requests))
        adapter.addFragment(historyFragment, getString(R.string.historyOfRequests))
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }
    //---------------------------------------------------------------------------------------------- setAdministrativeFragment


    //---------------------------------------------------------------------------------------------- setNotAdministrativeFragment
    private fun setNotAdministrativeFragment() {
        val adapter = ViewPagerAdapter(this@AdminTaxiFragment)
        val requestBundle = Bundle()
        requestBundle.putString(CompanionValues.adminTaxiType, EnumAdminTaxiType.REQUEST.name)
        var myRequestFragment: AdminTaxiListFragment? = null
        if (adminTaxiViewModel.getUserType() != EnumPersonnelType.Driver) {
            val myRequestBundle = Bundle()
            myRequestBundle.putString(CompanionValues.adminTaxiType, EnumAdminTaxiType.MY.name)
            myRequestFragment = AdminTaxiListFragment().apply {
                arguments = myRequestBundle
            }
        }
        val requestFragment = AdminTaxiListFragment().apply {
            arguments = requestBundle
        }
        adapter.addFragment(requestFragment, getString(R.string.requests))
        myRequestFragment?.let {
            adapter.addFragment(myRequestFragment, getString(R.string.myRequests))
        }
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }
    //---------------------------------------------------------------------------------------------- setNotAdministrativeFragment


    //---------------------------------------------------------------------------------------------- observeLiveData
    private fun observeLiveData() {

        AdminTaxiViewModel.requestTaxiLiveDate.observe(viewLifecycleOwner) {
            binding.tabLayout.getTabAt(0)?.apply {
                orCreateBadge
                badge?.isVisible = true
                badge?.number = it
            }
        }

        AdminTaxiViewModel.myTaxiLiveDate.observe(viewLifecycleOwner) {
            binding.tabLayout.getTabAt(1)?.apply {
                orCreateBadge
                badge?.isVisible = true
                badge?.number = it
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLiveData

}