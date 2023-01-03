package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        var notificationCount: Int = 0
    }


    //---------------------------------------------------------------------------------------------- deleteAllData
    fun deleteAllData() {
        userRepository.deleteUser()
        sharedPreferences
            .edit()
            .putString(CompanionValues.TOKEN, null)
            .putString(CompanionValues.userName, null)
            .putString(CompanionValues.passcode, null)
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



}