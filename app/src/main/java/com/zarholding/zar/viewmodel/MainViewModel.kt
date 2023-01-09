package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.repository.TokenRepository
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.utility.RoleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository,
    private val roleManager: RoleManager,
    private val themeManager: ThemeManager,
    private val tokenRepository: TokenRepository
) : ZarViewModel() {

    companion object {
        var notificationCount: Int = 0
    }


    //---------------------------------------------------------------------------------------------- deleteAllData
    fun deleteAllData() {
        userRepository.deleteUser()
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, null)
            .putInt(CompanionValues.notificationLast, 0)
            .apply()
    }
    //---------------------------------------------------------------------------------------------- deleteAllData


    //---------------------------------------------------------------------------------------------- setLastNotificationIdToZero
    fun setLastNotificationIdToZero() {
        sharedPreferences
            .edit()
            .putInt(CompanionValues.notificationLast, 0)
            .apply()
    }
    //---------------------------------------------------------------------------------------------- setLastNotificationIdToZero



    //---------------------------------------------------------------------------------------------- getUserInfo
    fun getUserInfo() = userRepository.getUser()
    //---------------------------------------------------------------------------------------------- getUserInfo


    //---------------------------------------------------------------------------------------------- getAdminRole
    fun getAdminRole() = roleManager.getAdminRole()
    //---------------------------------------------------------------------------------------------- getAdminRole


    //---------------------------------------------------------------------------------------------- applicationTheme
    fun applicationTheme() = themeManager.applicationTheme()
    //---------------------------------------------------------------------------------------------- applicationTheme


    //---------------------------------------------------------------------------------------------- getBearerToken
    fun getBearerToken() = tokenRepository.getBearerToken()
    //---------------------------------------------------------------------------------------------- getBearerToken

}