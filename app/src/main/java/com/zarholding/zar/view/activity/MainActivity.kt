package com.zarholding.zar.view.activity

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.manager.DialogManager
import com.zarholding.zar.model.other.notification.Category
import com.zarholding.zar.model.other.notification.Item
import com.zarholding.zar.view.recycler.CategoriesAdapter
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.ActivityMainBinding
import javax.inject.Inject


/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RemoteErrorEmitter {

    lateinit var binding : ActivityMainBinding

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
        setNavListener()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- setNavListener
    private fun setNavListener() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navHostFragment?.let {
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

    }
    //---------------------------------------------------------------------------------------------- setNavListener


    //---------------------------------------------------------------------------------------------- showAndHideBottomMenu
    private fun showAndHideBottomMenu(fragmentLabel: String) {
        when (fragmentLabel) {
            "SplashFragment",
            "LoginFragment" ->
            {
                binding.constraintLayoutFooterMenu.visibility = View.GONE
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




    private fun initNotification(dialog: Dialog) {

        val tabLayout = dialog.findViewById<TabLayout>(R.id.tabLayout)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)

        val categories = mutableListOf(
            Category(
                "امروز",
                Item("item 1"),
                Item("item 2"),
                Item("item 3"),
                Item("item 4"),
                Item("item 5")
            ),
            Category(
                "دیروز",
                Item("item 2-1"),
                Item("item 2-2"),
                Item("item 2-3"),
                Item("item 2-4"),
                Item("item 2-5"),
                Item("item 2-6"),
                Item("item 2-6")
            ),
            Category(
                "همه",
                Item("item 3-1"),
                Item("item 3-2"),
                Item("item 3-3")
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

        recyclerView.adapter = CategoriesAdapter(this, categories)

        TabbedListMediator(
            recyclerView,
            tabLayout,
            categories.indices.toList(),
            true
        ).attach()

    }

}