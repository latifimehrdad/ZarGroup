package com.zarholding.zar.api

import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.response.trip.RegisterStationResponseModel
import com.zarholding.zar.model.response.trip.TripResponseModel
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
    suspend fun requestGetAllTrips(
        @Header("Authorization") token : String
    ) : TripResponseModel



    @POST("${v1}/PersonnelsRegisteredStation/register-station")
    suspend fun requestRegisterStation(
        @Body registerStationModel: RequestRegisterStationModel,
        @Header("Authorization") token : String
    ) : RegisterStationResponseModel




}