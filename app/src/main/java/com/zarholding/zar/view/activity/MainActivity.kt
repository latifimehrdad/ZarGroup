package com.zarholding.zar.view.activity

import android.Manifest
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.res.Configuration
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zar.core.tools.loadings.LoadingManager
import com.zar.core.tools.manager.DialogManager
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.background.ZarBackgroundBroadcast
import com.zarholding.zar.background.ZarNotificationService
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.RoleManager
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
import javax.inject.Inject


/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    @Inject
    lateinit var userInfoDao: UserInfoDao
    @Inject
    lateinit var roleManager: RoleManager
    @Inject
    lateinit var themeManagers: ThemeManager
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var loadingManager: LoadingManager

    lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    private var broadcastReceiver: BroadcastReceiver? = null

    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        setAppTheme(themeManagers.applicationTheme())
        registerReceiver()
        setUserInfo()
        setListener()
        checkLocationPermission()
        createNotificationChannel()
/*        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(CompanionValues.channelId)*/
    }
    //---------------------------------------------------------------------------------------------- initView


    //______________________________________________________________________________________________ setAppTheme
    private fun setAppTheme(theme: Int) {
        when (theme) {
            Configuration.UI_MODE_NIGHT_YES ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Configuration.UI_MODE_NIGHT_NO ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    //______________________________________________________________________________________________ setAppTheme



    //---------------------------------------------------------------------------------------------- setNotificationCount
    fun setNotificationCount(count : Int) {
        Log.i("meri", "main = $count")
        binding.textViewNotificationCount.text = count.toString()
    }
    //---------------------------------------------------------------------------------------------- setNotificationCount



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment?.navController
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label != null)
                showAndHideBottomMenu(destination.label.toString())
        }


//        binding.imageViewNotification.setOnClickListener { showNotificationDialog() }


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
        val dialog = DialogManager().createDialogHeightWrapContent(
            this, R.layout.dialog_notification, Gravity.TOP, position
        )
        initNotification(dialog)
        dialog.show()
    }
    //---------------------------------------------------------------------------------------------- showNotificationDialog



    //---------------------------------------------------------------------------------------------- showAndHideBottomMenu
    private fun showAndHideBottomMenu(fragmentLabel: String) {
        when (fragmentLabel) {
            "SplashFragment",
            "LoginFragment" -> {
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
    //---------------------------------------------------------------------------------------------- showAndHideBottomMenu



    //---------------------------------------------------------------------------------------------- gotoFirstFragment
    fun gotoFirstFragment() {
        CoroutineScope(IO).launch {
            stopService(Intent(this@MainActivity, ZarNotificationService::class.java))
            sharedPreferences
                .edit()
                .putString(CompanionValues.TOKEN, null)
                .putString(CompanionValues.userName, null)
                .putString(CompanionValues.passcode, null)
                .apply()
            userInfoDao.deleteAllRole()
            delay(500)
            withContext(Main) {
                gotoFragment(R.id.action_goto_SplashFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFirstFragment


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
        CoroutineScope(IO).launch {
            delay(500)
            val user = userInfoDao.getUserInfo()
            withContext(Main) {
                binding.textViewProfileName.text = user?.fullName
                binding.textViewPersonalCode.text = resources
                    .getString(R.string.personalCode, user?.personnelNumber.toString())
                if (roleManager.getAdminRole())
                    binding.imageViewAdmin.visibility = View.VISIBLE
                else
                    binding.imageViewAdmin.visibility = View.GONE
            }
        }
    }
    //---------------------------------------------------------------------------------------------- setUserInfo


    //---------------------------------------------------------------------------------------------- createNotificationChannel
    private fun createNotificationChannel() {
        val vibrate: LongArray = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
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
        channel.setSound(alarmSound, audioAttributes)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    //---------------------------------------------------------------------------------------------- createNotificationChannel


    //---------------------------------------------------------------------------------------------- registerReceiver
    private fun registerReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                Log.e("meri", "registerReceiver")
                MainViewModel.notificationCount += 1
                setNotificationCount(MainViewModel.notificationCount)
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("com.zarholding.zar.background"))
    }
    //---------------------------------------------------------------------------------------------- registerReceiver


    private fun initNotification(dialog: Dialog) {

        dialog.setCancelable(true)
        val tabLayout = dialog.findViewById<TabLayout>(R.id.tabLayout)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewNotification)
        val imageViewClose = dialog.findViewById<ImageView>(R.id.imageViewClose)

        loadingManager.setRecyclerLoading(
            recyclerView,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )

        imageViewClose.setOnClickListener {
            dialog.dismiss()
        }
/*
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

        tabLayout?.getTabAt(0)?.apply {
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
        ).attach()*/

    }

    //---------------------------------------------------------------------------------------------- onDestroy
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
    //---------------------------------------------------------------------------------------------- onDestroy

}