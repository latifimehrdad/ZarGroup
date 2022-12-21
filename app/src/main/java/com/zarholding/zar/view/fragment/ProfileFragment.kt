package com.zarholding.zar.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import zar.databinding.FragmentProfileBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class ProfileFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userInfoDao: UserInfoDao

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var unAuthorizationManager: UnAuthorizationManager

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setListener()
        setUserInfo()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {

    }
    //---------------------------------------------------------------------------------------------- onError



    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        unAuthorizationManager.handel(activity,type,message,binding.constraintLayoutParent)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.layoutLogout.root.setOnClickListener { logOut() }
    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- logOut
    private fun logOut() {

        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                userInfoDao.deleteAllRole()
                sharedPreferences.edit().putString(CompanionValues.TOKEN, null).apply()
                CoroutineScope(IO).launch {
                    delay(500)
                    withContext(Main) {
                        findNavController().navigate(R.id.action_goto_SplashFragment)
                    }
                }
            }

        }
        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.DELETE,
            "آیا برای خروج مطمئن هستید؟",
            click).show()

    }
    //---------------------------------------------------------------------------------------------- logOut



    //---------------------------------------------------------------------------------------------- setUserInfo
    private fun setUserInfo() {
        CoroutineScope(IO).launch {
            val user = userInfoDao.getUserInfo()
            withContext(Main) {
                binding.textViewProfileName.text = user?.fullName
                binding.textViewPersonalCode.text = resources
                    .getString(R.string.personalCode, user?.personnelNumber.toString())
                binding.textViewDegree.text = "از سرور نمیاد"
            }
        }
    }
    //---------------------------------------------------------------------------------------------- setUserInfo




    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}