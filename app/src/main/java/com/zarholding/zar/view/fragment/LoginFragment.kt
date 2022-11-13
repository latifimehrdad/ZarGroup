package com.zarholding.zar.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.databinding.FragmentLoginBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class LoginFragment : Fragment(), RemoteErrorEmitter {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val loginViewModel : LoginViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView



    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
    }
    //---------------------------------------------------------------------------------------------- onViewCreated



    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //---------------------------------------------------------------------------------------------- onError



    //---------------------------------------------------------------------------------------------- requestLogin
    private fun requestLogin() {
        val model = LoginRequestModel("5000602", "5000602")
        loginViewModel.requestLogin(model).observe(viewLifecycleOwner) { response ->
            response?.let {
                if (it.hasError) {
                    onError(EnumErrorType.UNKNOWN, it.message)
                } else {
                    sharedPreferences
                        .edit()
                        .putString(CompanionValues.sharedPreferencesToken, it.data)
                        .apply()
                    if (activity != null)
                        requireActivity().onBackPressed()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestLogin



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}