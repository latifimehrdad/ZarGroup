package com.zarholding.zar.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.DialogManager
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import zar.databinding.FragmentSplashBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/8/2022.
 */


@AndroidEntryPoint
class SplashFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var job: Job

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        checkUserIsLogged()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- checkUserIsLogged
    private fun checkUserIsLogged() {
        val token = sharedPreferences.getString(CompanionValues.sharedPreferencesToken, null)
        token?.let {
            gotoFragmentHome()
        } ?: gotoFragmentLogin()
    }
    //---------------------------------------------------------------------------------------------- checkUserIsLogged


    //---------------------------------------------------------------------------------------------- gotoFragmentLogin
    private fun gotoFragmentLogin() {
        job = CoroutineScope(IO).launch {
            delay(5000)
            withContext(Main){
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentLogin


    //---------------------------------------------------------------------------------------------- gotoFragmentHome
    private fun gotoFragmentHome() {
        job = CoroutineScope(IO).launch {
            delay(5000)
            withContext(Main){
                findNavController().navigate(R.id.action_splashFragment_to_HomeFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentHome


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}