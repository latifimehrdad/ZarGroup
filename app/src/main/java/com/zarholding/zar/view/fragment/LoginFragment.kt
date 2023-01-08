package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zar.core.tools.BiometricTools
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.utility.extension.hideKeyboard
import com.zarholding.zar.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zar.R
import zar.databinding.FragmentLoginBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var biometricTools: BiometricTools

    private val loginViewModel: LoginViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = loginViewModel
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
        setListener()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        activity?.let { (it as MainActivity).deleteAllData() }
        if (loginViewModel.isBiometricEnable())
            binding.buttonFingerLogin.visibility = View.VISIBLE
        else
            binding.buttonFingerLogin.visibility = View.GONE
        observeLoginLiveDate()
        observeErrorLiveDate()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- observeErrorLiveDate
    private fun observeErrorLiveDate() {
        loginViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            binding.buttonLogin.stopLoading()
            showMessage(it.message)
        }
    }
    //---------------------------------------------------------------------------------------------- observeErrorLiveDate


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeLoginLiveDate() {
        loginViewModel.loginLiveDate.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- showMessage


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding
            .buttonLogin
            .setOnClickListener { login(false) }

        binding
            .textInputEditTextUserName
            .setOnClickListener { binding.textInputLayoutUserName.error = null }

        binding
            .textInputEditTextPasscode
            .setOnClickListener { binding.textInputLayoutPasscode.error = null }

        binding
            .buttonFingerLogin
            .setOnClickListener { showBiometricDialog() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- showBiometricDialog
    private fun showBiometricDialog() {
        if (activity == null)
            return
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(
            requireActivity(),
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage(getString(R.string.onAuthenticationError))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fingerPrintClick()
                }
            })
        biometricTools.checkDeviceHasBiometric(biometricPrompt)
    }
    //---------------------------------------------------------------------------------------------- showBiometricDialog


    //---------------------------------------------------------------------------------------------- fingerPrintClick
    private fun fingerPrintClick() {
        login(true)
    }
    //---------------------------------------------------------------------------------------------- fingerPrintClick


    //---------------------------------------------------------------------------------------------- login
    private fun login(fromFingerPrint: Boolean) {
        if (binding.buttonLogin.isLoading)
            return
        CoroutineScope(Main).launch {
            if (fromFingerPrint)
                loginViewModel.setUserNamePasswordFromSharePreferences()
            delay(500)
            if (checkEmpty()) {
                startLoading()
                loginViewModel.requestLogin()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- login


    //---------------------------------------------------------------------------------------------- checkEmpty
    private fun checkEmpty(): Boolean {
        if (loginViewModel.userName.isNullOrEmpty()) {
            binding.textInputLayoutUserName.error = getString(R.string.userNameIsEmpty)
            return false
        }
        if (loginViewModel.password.isNullOrEmpty()) {
            binding.textInputLayoutPasscode.error = getString(R.string.passcodeIsEmpty)
            return false
        }
        return true
    }
    //---------------------------------------------------------------------------------------------- checkEmpty


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        hideKeyboard()
        binding.textInputLayoutUserName.error = null
        binding.textInputLayoutPasscode.error = null
        binding.textInputEditTextUserName.isEnabled = false
        binding.textInputEditTextPasscode.isEnabled = false
        binding.buttonLogin.startLoading(getString(R.string.bePatient))
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.textInputEditTextUserName.isEnabled = true
        binding.textInputEditTextPasscode.isEnabled = true
        binding.buttonLogin.stopLoading()
    }
    //---------------------------------------------------------------------------------------------- stopLoading


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}