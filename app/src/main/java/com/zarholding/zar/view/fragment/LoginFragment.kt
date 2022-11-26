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
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.extension.hideKeyboard
import com.zarholding.zar.viewmodel.LoginViewModel
import com.zarholding.zar.viewmodel.LoginViewModel_Factory
import com.zarholding.zar.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentLoginBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class LoginFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val loginViewModel: LoginViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = loginViewModel
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setListener()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding
            .buttonLogin
            .setOnClickListener { checkEmptyValueForLogin() }

        binding
            .textInputEditTextUserName
            .setOnClickListener { binding.textInputLayoutUserName.error = null }

        binding
            .textInputEditTextPasscode
            .setOnClickListener { binding.textInputLayoutPasscode.error = null }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- checkEmptyValueForLogin
    private fun checkEmptyValueForLogin() {

        if (loginViewModel.loadingLiveDate.value == true)
            return

        if (loginViewModel.userName.isNullOrEmpty()) {
            binding.textInputLayoutUserName.error = getString(R.string.userNameIsEmpty)
            return
        }
        if (loginViewModel.passcode.isNullOrEmpty()) {
            binding.textInputLayoutPasscode.error = getString(R.string.passcodeIsEmpty)
            return
        }
        requestLogin()
    }
    //---------------------------------------------------------------------------------------------- checkEmptyValueForLogin


    //---------------------------------------------------------------------------------------------- requestLogin
    private fun requestLogin() {
        startLoading()
        val model = LoginRequestModel(loginViewModel.userName!!, loginViewModel.passcode!!)
        loginViewModel.requestLogin(model).observe(viewLifecycleOwner) { response ->
            stopLoading()
            response?.let {
                if (it.hasError) {
                    onError(EnumErrorType.UNKNOWN, it.message)
                } else {
                    sharedPreferences
                        .edit()
                        .putString(CompanionValues.TOKEN, it.data)
                        .apply()
                    if (activity != null)
                        requireActivity().onBackPressed()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- requestLogin


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        hideKeyboard()
        binding.textInputLayoutUserName.error = null
        binding.textInputLayoutPasscode.error = null
        binding.textInputEditTextUserName.isEnabled = false
        binding.textInputEditTextPasscode.isEnabled = false
        loginViewModel.loadingLiveDate.value = true
    }
    //---------------------------------------------------------------------------------------------- startLoading



    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.textInputEditTextUserName.isEnabled = true
        binding.textInputEditTextPasscode.isEnabled = true
        loginViewModel.loadingLiveDate.value = false
    }
    //---------------------------------------------------------------------------------------------- stopLoading



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}