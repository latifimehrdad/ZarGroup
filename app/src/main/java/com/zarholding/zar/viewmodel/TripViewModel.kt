package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

@HiltViewModel
class TripViewModel @Inject constructor(private val repository: TripRepository) : ViewModel(){

    //---------------------------------------------------------------------------------------------- requestGetTrips
    fun requestGetTrips(token : String) = repository.requestGetTrips(token)
    //---------------------------------------------------------------------------------------------- requestGetTrips

}