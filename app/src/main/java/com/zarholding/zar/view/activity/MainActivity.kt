package com.zarholding.zar.view.activity

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.DialogManager
import com.zarholding.zar.model.other.notification.NotificationCategoryModel
import com.zarholding.zar.model.other.notification.NotificationModel
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.view.recycler.adapter.notification.NotificationCategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.ActivityMainBinding
import java.time.LocalDateTime


/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RemoteErrorEmitter {

    lateinit var binding : ActivityMainBinding
    var navController : NavController? = null

    //---------------------------------------------------------------------------------------------- companion object
    companion object {
        lateinit var remoteErrorEmitter: RemoteErrorEmitter
    }
    //---------------------------------------------------------------------------------------------- companion object


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        setListener()
        checkLocationPermission()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navHostFragment?.let {
            navController = it.navController
            it.navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.label != null)
                    showAndHideBottomMenu(destination.label.toString())
            }
        }



        binding.imageViewNotification.setOnClickListener {
            val position = binding.imageViewNotification.top +
                    binding.imageViewNotification.measuredHeight
            val dialog = DialogManager().createDialogHeightWrapContent(
                this, R.layout.dialog_notification, Gravity.TOP, position)
            initNotification(dialog)
            dialog.show()
        }


        binding.imageViewProfile.setOnClickListener {
            gotoFragment(R.id.action_goto_ProfileFragment)
        }

        binding.imageViewSetting.setOnClickListener {
            gotoFragment(R.id.action_goto_SettingFragment)
        }

        binding.imageViewHome.setOnClickListener {
            gotoFragment(R.id.action_goto_HomeFragment)
        }

    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- showAndHideBottomMenu
    private fun showAndHideBottomMenu(fragmentLabel: String) {
        when (fragmentLabel) {
            "SplashFragment",
            "LoginFragment" ->
            {
                binding.constraintLayoutFooterMenu.visibility = View.GONE
                binding.constraintLayoutProfile.visibility = View.GONE

            }
            "ProfileFragment" ->
            {
                binding.constraintLayoutProfile.visibility = View.GONE
            }
            else ->
            {
                binding.constraintLayoutFooterMenu.visibility = View.VISIBLE
                binding.constraintLayoutProfile.visibility = View.VISIBLE
            }
        }
    }
    //---------------------------------------------------------------------------------------------- showAndHideBottomMenu




    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        remoteErrorEmitter.onError(errorType, message)
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        remoteErrorEmitter.unAuthorization(type, message)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- gotoFragment
    private fun gotoFragment(fragment : Int) {
        navController?.navigate(fragment, null)
    }
    //---------------------------------------------------------------------------------------------- gotoFragment


    //---------------------------------------------------------------------------------------------- checkLocationPermission
    private fun checkLocationPermission() {

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,

            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                }

            })
            .check()
    }
    //---------------------------------------------------------------------------------------------- checkLocationPermission



    //---------------------------------------------------------------------------------------------- setUserInfo
    fun setUserInfo(userInfoEntity: UserInfoEntity) {
        binding.textViewProfileName.text = userInfoEntity.fullName
        binding.textViewPersonalCode.text = resources
            .getString(R.string.personalCode,userInfoEntity.personnelNumber)
    }
    //---------------------------------------------------------------------------------------------- setUserInfo


    private fun initNotification(dialog: Dialog) {

        dialog.setCancelable(true)
        val tabLayout = dialog.findViewById<TabLayout>(R.id.tabLayout)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewNotification)
        val imageViewClose = dialog.findViewById<ImageView>(R.id.imageViewClose)

        imageViewClose.setOnClickListener {
            dialog.dismiss()
        }

        val categories = mutableListOf(
            NotificationCategoryModel(
                "امروز",
                LocalDateTime.now(),
                NotificationModel("", "", "", "", LocalDateTime.now(), false),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), false),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
            ),
            NotificationCategoryModel(
                "دیروز",
                LocalDateTime.now().minusDays(1),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), false),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
            ),
            NotificationCategoryModel(
                "همه",
                null,
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
                NotificationModel("", "", "", "", LocalDateTime.now(), true),
            )
        )

        for (category in categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category.name))
        }

        tabLayout?.getTabAt(0)?.apply{
            orCreateBadge
            badge?.isVisible = true
            badge?.number = 3
        }

        recyclerView.adapter = NotificationCategoryAdapter(categories)

        TabbedListMediator(
            recyclerView,
            tabLayout,
            categories.indices.toList(),
            true
        ).attach()

    }

}