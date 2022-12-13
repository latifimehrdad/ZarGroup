package com.zarholding.zar.repository

import androidx.lifecycle.LiveData
import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.response.address.AddressResponseModel
import org.osmdroid.api.IGeoPoint
import javax.inject.Inject

class AddressRepository @Inject constructor(private val api: ApiSuperApp) {

    @Inject
    lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- getAddress
    fun getAddress(geoPoint: IGeoPoint) : LiveData<AddressResponseModel?> {
        val url =
            "http://nominatim.openstreetmap.org/reverse?format=json&lat=" +
                    geoPoint.latitude.toString() +
                    "&lon=" +
                    geoPoint.longitude.toString() +
                    "&zoom=22&addressdetails=5"
        return apiCall(emitter) {api.getAddress(url)}
    }
    //---------------------------------------------------------------------------------------------- getAddress

}