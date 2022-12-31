package com.zarholding.zar.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumApiError
import com.zarholding.zar.background.ZarNotificationService
import com.zarholding.zar.view.activity.MainActivity
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

    private val splashViewModel : SplashViewModel by viewModels()

    private lateinit var job: Job

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
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- checkUserIsLogged
    private fun checkUserIsLogged() {
        if (binding.buttonReTry.isLoading)
            return

        val token = splashViewModel.getToken()
        token?.let {
            gotoFragmentHome()
        } ?: gotoFragmentLogin()
    }
    //---------------------------------------------------------------------------------------------- checkUserIsLogged


    //---------------------------------------------------------------------------------------------- gotoFragmentLogin
    private fun gotoFragmentLogin() {
        job = CoroutineScope(IO).launch {
            delay(5000)
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
        job = CoroutineScope(IO).launch {
            withContext(Main) {
                requestGetData()
            }
        }
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
            when(it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()
                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate



    //---------------------------------------------------------------------------------------------- observeSuccessLiveDataLiveData
    private fun observeSuccessLiveDataLiveData() {
        splashViewModel.successLiveData.observe(viewLifecycleOwner){
            (activity as MainActivity).setUserInfo()
            startServiceNotificationBackground()
            if (it)
                findNavController()
                    .navigate(R.id.action_splashFragment_to_HomeFragment)
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
        job.cancel()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}