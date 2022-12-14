package com.zarholding.zar.repository

import androidx.lifecycle.LiveData
import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.response.address.AddressResponseModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class AddressRepository @Inject constructor(private val api: ApiSuperApp) {

    @Inject
    lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestGetAddress
    fun requestGetAddress(geoPoint: GeoPoint) : LiveData<AddressResponseModel?> {
        val url =
            "http://nominatim.openstreetmap.org/reverse?format=json&lat=" +
                    geoPoint.latitude.toString() +
                    "&lon=" +
                    geoPoint.longitude.toString() +
                    "&zoom=22&addressdetails=5"
        return apiCall(emitter) {api.requestGetAddress(url)}
    }
    //---------------------------------------------------------------------------------------------- requestGetAddress



    //---------------------------------------------------------------------------------------------- requestGetSuggestionAddress
    fun requestGetSuggestionAddress(
        address: String,
        suggestion : List<AddressSuggestionModel>?) : LiveData<List<AddressSuggestionModel>?> {
        val split = address.split(" ")
        var search = ""
        for (item in split)
            search += "$item+"
        var url = "https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&limit=50&q=$search"
        suggestion?.let {
            url += "&exclude_place_ids="
            for (item in suggestion)
                url += "${item.place_id},"
        }

        return apiCall(emitter){api.requestGetSuggestionAddress(url)}
    }
    //---------------------------------------------------------------------------------------------- requestGetSuggestionAddress

}