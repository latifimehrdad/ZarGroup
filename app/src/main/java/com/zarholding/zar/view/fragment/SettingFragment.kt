package com.zarholding.zar.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.BiometricTools
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.ThemeManagers
import com.zarholding.zar.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zar.R
import zar.databinding.FragmentSettingBinding
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class SettingFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var themeManagers: ThemeManagers

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var biometricTools: BiometricTools

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
        setOnListener()

    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        CoroutineScope(Main).launch {
            delay(300)
            when (themeManagers.applicationTheme()) {
                Configuration.UI_MODE_NIGHT_YES -> binding.layoutActiveDarkMode.switchActive.isChecked =
                    true
                Configuration.UI_MODE_NIGHT_NO -> binding.layoutActiveDarkMode.switchActive.isChecked =
                    false
            }

            binding
                .layoutActiveFingerPrint
                .switchActive
                .isChecked = sharedPreferences.getBoolean(CompanionValues.biometric, false)
        }
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- setOnListener
    private fun setOnListener() {
        binding.layoutActiveDarkMode.switchActive.setOnClickListener { changeAppTheme() }
        binding.layoutActiveFingerPrint.switchActive.setOnClickListener { showBiometricDialog() }
    }
    //---------------------------------------------------------------------------------------------- setOnListener


    //---------------------------------------------------------------------------------------------- changeAppTheme
    private fun changeAppTheme() {
        if (binding.layoutActiveDarkMode.switchActive.isChecked)
            themeManagers.changeApplicationTheme(Configuration.UI_MODE_NIGHT_YES)
        else
            themeManagers.changeApplicationTheme(Configuration.UI_MODE_NIGHT_NO)
    }
    //---------------------------------------------------------------------------------------------- changeAppTheme


/*
    //---------------------------------------------------------------------------------------------- checkDeviceHasBiometric
    private fun checkDeviceHasBiometric() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            binding.layoutActiveFingerPrint.switchActive.isEnabled = false
            return
        }

        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricDialog()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.layoutActiveFingerPrint.switchActive.isChecked = false
                binding.layoutActiveFingerPrint.switchActive.isEnabled = false
                onError(
                    EnumErrorType.UNKNOWN,
                    getString(R.string.biometricNoHardware)
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.layoutActiveFingerPrint.switchActive.isChecked = false
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                startActivity(enrollIntent)

            }
            else -> {

            }
        }
    }
    //---------------------------------------------------------------------------------------------- checkDeviceHasBiometric
*/


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
                    binding
                        .layoutActiveFingerPrint
                        .switchActive
                        .isChecked = sharedPreferences.getBoolean(CompanionValues.biometric, false)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    binding
                        .layoutActiveFingerPrint
                        .switchActive
                        .isChecked = sharedPreferences.getBoolean(CompanionValues.biometric, false)
                }


                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val biometric = !sharedPreferences.getBoolean(CompanionValues.biometric, false)
                    binding
                        .layoutActiveFingerPrint
                        .switchActive
                        .isChecked = biometric
                    sharedPreferences.edit()
                        .putBoolean(CompanionValues.biometric, biometric)
                        .apply()
                    onError(EnumErrorType.UNKNOWN, getString(R.string.actionIsDone))
                }
            })
        biometricTools.checkDeviceHasBiometric(biometricPrompt)
    }
    //---------------------------------------------------------------------------------------------- showBiometricDialog


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}