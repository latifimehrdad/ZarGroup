package com.zarholding.zar.view.activity

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zarholding.zar.background.ZarNotificationService
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.dialog.NotificationDialog
import com.zarholding.zar.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zar.R
import zar.databinding.ActivityMainBinding


/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    private val mainViewModel: MainViewModel by viewModels()

    private var broadcastReceiver: BroadcastReceiver? = null

    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.token = ""
        initView()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        mainViewModel.setLastNotificationIdToZero()
        setAppTheme()
        registerReceiver()
        setUserInfo()
        setListener()
        checkLocationPermission()
        createNotificationChannel()
    }
    //---------------------------------------------------------------------------------------------- initView


    //______________________________________________________________________________________________ setAppTheme
    private fun setAppTheme() {
        when (mainViewModel.applicationTheme()) {
            Configuration.UI_MODE_NIGHT_YES ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Configuration.UI_MODE_NIGHT_NO ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    //______________________________________________________________________________________________ setAppTheme


    //---------------------------------------------------------------------------------------------- setNotificationCount
    fun setNotificationCount(count: Int) {
        binding.textViewNotificationCount.text = count.toString()
        if (count < 1)
            binding.cardViewNotification.visibility = View.GONE
        else
            binding.cardViewNotification.visibility = View.VISIBLE
    }
    //---------------------------------------------------------------------------------------------- setNotificationCount


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment?.navController
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label != null)
                showAndHideBottomNavigationMenu(destination.label.toString())
        }


        binding.imageViewNotification.setOnClickListener { showNotificationDialog() }

        binding.cardViewNotification.setOnClickListener { showNotificationDialog() }

        binding.imageViewProfile.setOnClickListener {
            gotoFragment(R.id.action_goto_ProfileFragment)
        }

        binding.imageViewSetting.setOnClickListener {
            gotoFragment(R.id.action_goto_SettingFragment)
        }

        binding.imageViewHome.setOnClickListener {
            gotoFragment(R.id.action_goto_HomeFragment)
        }

        binding.imageViewAdmin.setOnClickListener {
            gotoFragment(R.id.action_goto_DashboardFragment)
        }

    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- showNotificationDialog
    private fun showNotificationDialog() {
        val position = binding.imageViewNotification.top +
                binding.imageViewNotification.measuredHeight
        NotificationDialog(position).show(supportFragmentManager, "notification dialog")
    }
    //---------------------------------------------------------------------------------------------- showNotificationDialog


    //---------------------------------------------------------------------------------------------- showAndHideBottomNavigationMenu
    private fun showAndHideBottomNavigationMenu(fragmentLabel: String) {
        when (fragmentLabel) {
            "SplashFragment",
            "LoginFragment",
            "MapFragment" -> {
                binding.constraintLayoutFooterMenu.visibility = View.GONE
                binding.constraintLayoutProfile.visibility = View.GONE

            }
            "ProfileFragment" -> {
                binding.constraintLayoutProfile.visibility = View.GONE
            }
            else -> {
                binding.constraintLayoutFooterMenu.visibility = View.VISIBLE
                binding.constraintLayoutProfile.visibility = View.VISIBLE
            }
        }
    }
    //---------------------------------------------------------------------------------------------- showAndHideBottomNavigationMenu


    //---------------------------------------------------------------------------------------------- gotoFirstFragment
    fun gotoFirstFragment() {
        deleteAllData()
        CoroutineScope(IO).launch {
            delay(500)
            withContext(Main) {
                gotoFragment(R.id.action_goto_SplashFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFirstFragment


    //---------------------------------------------------------------------------------------------- deleteAllData
    fun deleteAllData() {
        stopService(Intent(this@MainActivity, ZarNotificationService::class.java))
        mainViewModel.deleteAllData()
    }
    //---------------------------------------------------------------------------------------------- deleteAllData


    //---------------------------------------------------------------------------------------------- gotoFragment
    private fun gotoFragment(fragment: Int) {
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
    fun setUserInfo() {
        CoroutineScope(Main).launch {
            delay(500)
            val user = mainViewModel.getUserInfo()
            binding.item = user
            binding.token = mainViewModel.getBearerToken()
            if (mainViewModel.getAdminRole())
                binding.imageViewAdmin.visibility = View.VISIBLE
            else
                binding.imageViewAdmin.visibility = View.GONE
            binding.notifyChange()
        }
    }
    //---------------------------------------------------------------------------------------------- setUserInfo


    //---------------------------------------------------------------------------------------------- createNotificationChannel
    private fun createNotificationChannel() {
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CompanionValues.channelId,
            CompanionValues.channelName,
            importance
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        channel.vibrationPattern = vibrate
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    //---------------------------------------------------------------------------------------------- createNotificationChannel


    //---------------------------------------------------------------------------------------------- registerReceiver
    private fun registerReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val item = intent.getStringExtra(CompanionValues.notificationLast)
                if (item.isNullOrEmpty())
                    MainViewModel.notificationCount -= 1
                else MainViewModel.notificationCount += 1
                setNotificationCount(MainViewModel.notificationCount)
            }
        }
        registerReceiver(
            broadcastReceiver,
            IntentFilter("com.zarholding.zar.receive.message")
        )
    }
    //---------------------------------------------------------------------------------------------- registerReceiver


    //---------------------------------------------------------------------------------------------- showMessage
    fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, theme))
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- onDestroy
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
    //---------------------------------------------------------------------------------------------- onDestroy

}