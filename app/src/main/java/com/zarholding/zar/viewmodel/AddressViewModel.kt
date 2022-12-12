package com.zarholding.zar.viewmodel

import androidx.lifecycle.ViewModel
import com.zarholding.zar.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val repo : AddressRepository) : ViewModel() {

    //---------------------------------------------------------------------------------------------- getAddress
    fun getAddress(geoPoint: IGeoPoint) = repo.getAddress(geoPoint)
    //---------------------------------------------------------------------------------------------- getAddress
}