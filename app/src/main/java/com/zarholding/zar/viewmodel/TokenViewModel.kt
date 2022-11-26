package com.zarholding.zar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.zarholding.zar.utility.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/22/2022.
 */

@HiltViewModel
class TokenViewModel @Inject constructor(private val sp: SharedPreferences ) : ViewModel() {

    //---------------------------------------------------------------------------------------------- getBearerToken
    fun getBearerToken() = "Bearer ${sp.getString(CompanionValues.TOKEN, "")}"
    //---------------------------------------------------------------------------------------------- getBearerToken

    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = sp.getString(CompanionValues.TOKEN, "")
    //---------------------------------------------------------------------------------------------- getToken

}