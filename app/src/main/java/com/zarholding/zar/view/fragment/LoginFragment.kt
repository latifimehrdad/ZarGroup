package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zar.core.tools.BiometricTools
import com.zarholding.zar.view.extension.hideKeyboard
import com.zarholding.zar.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentLoginBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class LoginFragment : Fragment(){

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
        if (loginViewModel.getBiometricEnable())
            binding.buttonFingerLogin.visibility = View.VISIBLE
        else
            binding.buttonFingerLogin.visibility = View.GONE
    }
    //---------------------------------------------------------------------------------------------- initView



    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 10 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
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
    private fun login(fromFingerPrint : Boolean) {
        if (binding.buttonLogin.isLoading)
            return

        if (fromFingerPrint)
            loginViewModel.setUserNamePasswordFromSharePreferences()

        if (checkEmpty()) {
            startLoading()
            loginViewModel.requestLogin()
            observeLoginLiveDate()
            observeErrorLiveDate()
        }
    }
    //---------------------------------------------------------------------------------------------- login


    //---------------------------------------------------------------------------------------------- checkEmpty
    private fun checkEmpty() : Boolean {
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


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeLoginLiveDate() {
        loginViewModel.loginLiveDate.removeObservers(viewLifecycleOwner)
        loginViewModel.loginLiveDate.observe(viewLifecycleOwner) {
            it?.let {
                if (activity != null)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate



    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        loginViewModel.errorLiveDate.removeObservers(viewLifecycleOwner)
        loginViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            binding.buttonLogin.stopLoading()
            showMessage(it.message)
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate




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
        loginViewModel.loginLiveDate.removeObservers(viewLifecycleOwner)
        loginViewModel.errorLiveDate.removeObservers(viewLifecycleOwner)
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}