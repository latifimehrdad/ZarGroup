package com.zarholding.zar.api

import com.zarholding.zar.model.request.ArticleRequestModel
import com.zarholding.zar.model.request.RequestRegisterStationModel
import com.zarholding.zar.model.response.article.ArticleResponseModel
import com.zarholding.zar.model.response.trip.DeleteRegisteredStationResponseModel
import com.zarholding.zar.model.response.trip.RegisterStationResponseModel
import com.zarholding.zar.model.response.trip.TripResponseModel
import retrofit2.http.*

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


    @GET("${v1}/PersonnelsRegisteredStation/delete-register-station/{id}")
    suspend fun requestDeleteRegisteredStation(
        @Path("id") id : Int,
        @Header("Authorization") token : String
    ) : DeleteRegisteredStationResponseModel


    @POST("${v1}/Article/list-articles")
    suspend fun requestGetArticles(
        @Body articleRequestModel: ArticleRequestModel,
        @Header("Authorization") token : String
    ) : ArticleResponseModel

}