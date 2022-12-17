package com.zarholding.zar.api

import com.zarholding.zar.model.request.*
import com.zarholding.zar.model.response.LoginResponseModel
import com.zarholding.zar.model.response.address.AddressResponseModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.model.response.article.ArticleResponseModel
import com.zarholding.zar.model.response.taxi.TaxiAddFavePlaceResponse
import com.zarholding.zar.model.response.taxi.TaxiFavPlaceResponse
import com.zarholding.zar.model.response.taxi.TaxiRemoveFavePlaceResponse
import com.zarholding.zar.model.response.taxi.TaxiRequestResponse
import com.zarholding.zar.model.response.trip.*
import com.zarholding.zar.model.response.user.UserInfoResponseModel
import retrofit2.http.*


/**
 * Created by m-latifi on 11/26/2022.
 */

interface ApiSuperApp {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
    }

    @POST("${v1}/LogIn/login-users")
    suspend fun requestLogin(@Body login : LoginRequestModel) : LoginResponseModel


    @GET("${v1}/LogIn/login-userInfo")
    suspend fun requestUserInfo(
        @Header("Authorization") token : String
    ) : UserInfoResponseModel


    @GET("$v1/PersonnelsRegisteredStation/list-registered-trip")
    suspend fun requestGetAllTrips(
        @Header("Authorization") token : String
    ) : TripResponseModel



    @POST("$v1/PersonnelsRegisteredStation/register-station")
    suspend fun requestRegisterStation(
        @Body registerStationModel: RequestRegisterStationModel,
        @Header("Authorization") token : String
    ) : RegisterStationResponseModel


    @GET("$v1/PersonnelsRegisteredStation/delete-register-station/{id}")
    suspend fun requestDeleteRegisteredStation(
        @Path("id") id : Int,
        @Header("Authorization") token : String
    ) : DeleteRegisteredStationResponseModel


    @POST("$v1/Article/list-articles")
    suspend fun requestGetArticles(
        @Body articleRequestModel: ArticleRequestModel,
        @Header("Authorization") token : String
    ) : ArticleResponseModel


    @GET("$v1/personnelsRegisteredStation/list-request-registered-trip")
    suspend fun requestGetTripRequestRegister(
        @Header("Authorization") token : String
    ) : TripRequestRegisterResponseModel


    @POST("$v1/personnelsRegisteredStation/response-request-registered-trip")
    suspend fun requestConfirmAndRejectTripRequestRegister(
        @Body request : List<TripRequestRegisterStatusModel>,
        @Header("Authorization") token : String
    ) : TripRequestRegisterStatusResponseModel


    @GET("$v1/CarRequest/get-favoritelocations")
    suspend fun requestGetTaxiFavPlace(
        @Header("Authorization") token: String
    ) : TaxiFavPlaceResponse


    @POST("$v1/CarRequest/Add-favoritelocations")
    suspend fun requestAddFavPlace(
        @Body request : TaxiAddFavPlaceRequest,
        @Header("Authorization") token: String
    ) : TaxiAddFavePlaceResponse


    @GET("$v1/CarRequest/delete-favoritelocations/{id}")
    suspend fun requestDeleteFavPlace(
        @Path("id") id : Int,
        @Header("Authorization") token : String
    ) : TaxiRemoveFavePlaceResponse


    @POST("$v1/CarRequest/Add-carrequest")
    suspend fun requestTaxi(
        @Body request : TaxiRequestModel,
        @Header("Authorization") token : String
    ) : TaxiRequestResponse


    @GET
    suspend fun requestGetAddress(
        @Url url : String
    ) : AddressResponseModel


    @GET
    suspend fun requestGetSuggestionAddress(
        @Url url: String
    ): List<AddressSuggestionModel>

}