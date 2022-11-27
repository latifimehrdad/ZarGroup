package com.zarholding.zar.api

import com.zarholding.zar.model.response.trip.RegisterStationModel
import com.zarholding.zar.model.response.trip.RegisterStationResponseModel
import com.zarholding.zar.model.response.trip.TripResponseModel
import com.zarholding.zar.model.response.user.UserInfoResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by m-latifi on 11/26/2022.
 */

interface ApiSuperApp {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
    }


    @GET("${v1}/PersonnelsRegisteredStation/list-registered-trip")
    suspend fun requestGetTrips(
        @Header("Authorization") token : String
    ) : TripResponseModel


    @POST("${v1}/PersonnelsRegisteredStation/register-station")
    suspend fun requestRegisterStation(
        @Body registerStationModel: RegisterStationModel,
        @Header("Authorization") token : String
    ) : RegisterStationResponseModel


}