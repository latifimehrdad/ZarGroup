package com.zarholding.zar.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.ConfirmDialog
import com.zarholding.zar.view.dialog.EditPlaqueDialog
import com.zarholding.zar.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentProfileBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class ProfileFragment : Fragment(){

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val profileViewModel : ProfileViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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



    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.layoutLogout.root.setOnClickListener { logOut() }
        binding.layoutMessage.root.setOnClickListener { disableFeature() }
        binding.layoutPersonalInformation.setOnClickListener { showAndHideInfo() }
        binding.layoutTrainings.root.setOnClickListener { disableFeature() }
        binding.layoutPlaque.root.setOnClickListener { showEditCarPlaque() }
    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- disableFeature
    private fun disableFeature() {
        showMessage(getString(R.string.disableFeature))
    }
    //---------------------------------------------------------------------------------------------- disableFeature



    //---------------------------------------------------------------------------------------------- logOut
    private fun logOut() {

        val click = object : ConfirmDialog.Click {
            override fun clickYes() {
                (activity as MainActivity).gotoFirstFragment()
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
        binding.item = profileViewModel.getUserInfo()
        binding.token = profileViewModel.getBearerToken()
    }
    //---------------------------------------------------------------------------------------------- setUserInfo



    //---------------------------------------------------------------------------------------------- showAndHideInfo
    private fun showAndHideInfo() {
        if (binding.expandableInfo.isExpanded)
            hideMoreInfo()
        else
            showMoreInfo()
    }
    //---------------------------------------------------------------------------------------------- showAndHideInfo



    //---------------------------------------------------------------------------------------------- showMoreInfo
    private fun showMoreInfo() {
        binding.expandableInfo.expand()
        val rotate = RotateAnimation(
            0f,
            -90f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 350
        rotate.interpolator = LinearInterpolator()
        rotate.fillAfter = true
        binding.imageViewMore.startAnimation(rotate)
    }
    //---------------------------------------------------------------------------------------------- showMoreInfo


    //---------------------------------------------------------------------------------------------- hideMoreInfo
    private fun hideMoreInfo() {
        binding.expandableInfo.collapse()
        val rotate = RotateAnimation(
            -90f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 400
        rotate.interpolator = LinearInterpolator()
        rotate.fillAfter = true
        binding.imageViewMore.startAnimation(rotate)
    }
    //---------------------------------------------------------------------------------------------- hideMoreInfo



    //---------------------------------------------------------------------------------------------- showEditCarPlaque
    private fun showEditCarPlaque() {
        val click = object : EditPlaqueDialog.Click {
            override fun editPlaque() {

                setUserInfo()
            }
        }
        EditPlaqueDialog(click).show(childFragmentManager, "edit plaque")
    }
    //---------------------------------------------------------------------------------------------- showEditCarPlaque



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}