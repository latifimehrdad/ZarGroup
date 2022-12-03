package com.zarholding.zar.view.fragment

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.utility.ThemeManagers
import com.zarholding.zar.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zar.R
import zar.databinding.FragmentSettingBinding
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

    }
    //---------------------------------------------------------------------------------------------- onError



    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        CoroutineScope(Main).launch {
            delay(300)
            when (themeManagers.applicationTheme()) {
                Configuration.UI_MODE_NIGHT_YES -> binding.layoutActiveDarkMode.switchActive.isChecked = true
                Configuration.UI_MODE_NIGHT_NO -> binding.layoutActiveDarkMode.switchActive.isChecked = false
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initView



    //---------------------------------------------------------------------------------------------- setOnListener
    private fun setOnListener() {
        binding.layoutActiveDarkMode.switchActive.setOnClickListener { changeAppTheme() }
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



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}