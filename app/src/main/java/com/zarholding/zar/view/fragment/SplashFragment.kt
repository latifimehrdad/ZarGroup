package com.zarholding.zar.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zar.core.enums.EnumApiError
import com.zarholding.zar.background.ZarNotificationService
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.MainViewModel
import com.zarholding.zar.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import zar.databinding.FragmentSplashBinding

/**
 * Created by m-latifi on 11/8/2022.
 */


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val splashViewModel: SplashViewModel by viewModels()

    private var job: Job? = null

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.buttonReTry.setOnClickListener { checkUserIsLogged() }
        checkUserIsLogged()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- showMessage


    //---------------------------------------------------------------------------------------------- checkUserIsLogged
    private fun checkUserIsLogged() {
        if (binding.buttonReTry.isLoading)
            return
        if (splashViewModel.userIsEntered())
            gotoFragmentHome()
        else
            gotoFragmentLogin()
    }
    //---------------------------------------------------------------------------------------------- checkUserIsLogged


    //---------------------------------------------------------------------------------------------- gotoFragmentLogin
    private fun gotoFragmentLogin() {
        job = CoroutineScope(IO).launch {
            delay(3000)
            withContext(Main) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentLogin


    //---------------------------------------------------------------------------------------------- gotoFragmentHome
    private fun gotoFragmentHome() {
        observeErrorLiveDate()
        observeSuccessLiveDataLiveData()
        requestGetData()
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentHome


    //---------------------------------------------------------------------------------------------- startServiceNotificationBackground
    private fun startServiceNotificationBackground() {
        requireContext().startService(Intent(requireContext(), ZarNotificationService::class.java))
    }
    //---------------------------------------------------------------------------------------------- startServiceNotificationBackground


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        splashViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            stopLoading()
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()
                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate


    //---------------------------------------------------------------------------------------------- observeSuccessLiveDataLiveData
    private fun observeSuccessLiveDataLiveData() {
        splashViewModel.successLiveData.observe(viewLifecycleOwner) {
            (activity as MainActivity).setUserInfo()
            startServiceNotificationBackground()
            MainViewModel.notificationCount = it
            (activity as MainActivity).setNotificationCount(it)
            findNavController().navigate(R.id.action_splashFragment_to_HomeFragment)
        }
    }
    //---------------------------------------------------------------------------------------------- observeSuccessLiveDataLiveData


    //---------------------------------------------------------------------------------------------- requestGetData
    private fun requestGetData() {
        startLoading()
        splashViewModel.requestGetData()
    }
    //---------------------------------------------------------------------------------------------- requestGetData


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        binding.buttonReTry.visibility = View.GONE
        binding.buttonReTry.startLoading("")
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.buttonReTry.stopLoading()
        binding.buttonReTry.visibility = View.VISIBLE
    }
    //---------------------------------------------------------------------------------------------- stopLoading


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}