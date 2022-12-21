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
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.BiometricTools
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.view.activity.MainActivity
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
class LoginFragment : Fragment(), RemoteErrorEmitter {

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
        initView()
        setListener()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 10 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
        stopLoading()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        if (loginViewModel.getBiometricEnable())
            binding.buttonFingerLogin.visibility = View.VISIBLE
        else
            binding.buttonFingerLogin.visibility = View.GONE
    }
    //---------------------------------------------------------------------------------------------- initView


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
                    onError(EnumErrorType.UNKNOWN, getString(R.string.onAuthenticationError))
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



    //---------------------------------------------------------------------------------------------- observeUseNameEmptyLiveData
    private fun observeUseNameEmptyLiveData() {
        loginViewModel.useNameEmptyLiveData.observe(viewLifecycleOwner) {
            if (it)
                binding.textInputLayoutUserName.error = getString(R.string.userNameIsEmpty)
            else
                binding.textInputLayoutUserName.error = null
            loginViewModel.useNameEmptyLiveData.removeObservers(viewLifecycleOwner)
        }
    }
    //---------------------------------------------------------------------------------------------- observeUseNameEmptyLiveData


    //---------------------------------------------------------------------------------------------- observePasswordEmptyLiveData
    private fun observePasswordEmptyLiveData() {
        loginViewModel.passwordEmptyLiveData.observe(viewLifecycleOwner) {
            if (it)
                binding.textInputLayoutPasscode.error = getString(R.string.passcodeIsEmpty)
            else
                binding.textInputLayoutPasscode.error = null
            loginViewModel.passwordEmptyLiveData.removeObservers(viewLifecycleOwner)
        }
    }
    //---------------------------------------------------------------------------------------------- observePasswordEmptyLiveData


    //---------------------------------------------------------------------------------------------- observeLoadingLiveDate
    private fun observeLoadingLiveDate() {
        loginViewModel.loadingLiveDate.observe(viewLifecycleOwner) {
            if (it)
                startLoading()
            else
                stopLoading()
            loginViewModel.loadingLiveDate.removeObservers(viewLifecycleOwner)
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoadingLiveDate



    //---------------------------------------------------------------------------------------------- login
    private fun login(fromFingerPrint : Boolean) {
        if (loginViewModel.loadingLiveDate.value == true)
            return
        loginViewModel.login(fromFingerPrint)
        observeUseNameEmptyLiveData()
        observePasswordEmptyLiveData()
        observeLoadingLiveDate()
        observeResponseOfLoginRequestLiveDate()
    }
    //---------------------------------------------------------------------------------------------- login


    //---------------------------------------------------------------------------------------------- observeResponseOfLoginRequestLiveDate
    private fun observeResponseOfLoginRequestLiveDate() {
        loginViewModel.responseOfLoginRequestLiveDate?.observe(viewLifecycleOwner) {
            loginViewModel.loadingLiveDate.value = false
            loginViewModel.responseOfLoginRequestLiveDate?.removeObservers(viewLifecycleOwner)
            it?.let {
                if (it.hasError) {
                    onError(EnumErrorType.UNKNOWN, it.message)
                } else {
                    loginViewModel.saveUserNameAndPassword(it.data)
                    if (activity != null)
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeResponseOfLoginRequestLiveDate


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        hideKeyboard()
        binding.textInputLayoutUserName.error = null
        binding.textInputLayoutPasscode.error = null
        binding.textInputEditTextUserName.isEnabled = false
        binding.textInputEditTextPasscode.isEnabled = false
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.textInputEditTextUserName.isEnabled = true
        binding.textInputEditTextPasscode.isEnabled = true
    }
    //---------------------------------------------------------------------------------------------- stopLoading


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}