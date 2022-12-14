package com.zarholding.zar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val repo: AddressRepository) : ViewModel() {

    //---------------------------------------------------------------------------------------------- getAddress
    fun getAddress(geoPoint: GeoPoint) = repo.requestGetAddress(geoPoint)
    //---------------------------------------------------------------------------------------------- getAddress


    //---------------------------------------------------------------------------------------------- requestGetSuggestionAddress
    fun requestGetSuggestionAddress(
        address: String,
        suggestion: List<AddressSuggestionModel>?
    ): LiveData<List<AddressSuggestionModel>?> =
        repo.requestGetSuggestionAddress(address, suggestion)
    //---------------------------------------------------------------------------------------------- requestGetSuggestionAddress
}