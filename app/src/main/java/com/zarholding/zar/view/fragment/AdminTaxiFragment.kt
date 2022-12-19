package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentAdminTaxiBinding
import javax.inject.Inject

@AndroidEntryPoint
class AdminTaxiFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentAdminTaxiBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var unAuthorizationManager: UnAuthorizationManager

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentAdminTaxiBinding.inflate(inflater, container, false)
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
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, context?.theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, context?.theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, context?.theme))
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        unAuthorizationManager.handel(activity, type, message, binding.constraintLayoutParent)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization

}