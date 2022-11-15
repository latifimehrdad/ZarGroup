package com.zarholding.zar.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import dagger.hilt.android.AndroidEntryPoint
import zar.R


/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RemoteErrorEmitter {

    lateinit var constraintLayoutFooterMenu: ConstraintLayout

    //---------------------------------------------------------------------------------------------- companion object
    companion object {
        lateinit var remoteErrorEmitter: RemoteErrorEmitter
    }
    //---------------------------------------------------------------------------------------------- companion object


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        constraintLayoutFooterMenu = findViewById(R.id.constraintLayoutFooterMenu)
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
    }
    //---------------------------------------------------------------------------------------------- setNavListener


    //---------------------------------------------------------------------------------------------- showAndHideBottomMenu
    private fun showAndHideBottomMenu(fragmentLabel: String) {
        when (fragmentLabel) {
            "SplashFragment",
            "LoginFragment" -> constraintLayoutFooterMenu.visibility = View.GONE
            else -> constraintLayoutFooterMenu.visibility = View.VISIBLE
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

}