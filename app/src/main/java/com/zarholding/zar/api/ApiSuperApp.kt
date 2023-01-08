package com.zarholding.zar.api

import com.zarholding.zar.model.enum.EnumDriverType
import com.zarholding.zar.model.notification.NotificationResponse
import com.zarholding.zar.model.request.*
import com.zarholding.zar.model.response.LoginResponseModel
import com.zarholding.zar.model.response.address.AddressResponseModel
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.model.response.article.ArticleResponseModel
import com.zarholding.zar.model.response.company.CompanyResponse
import com.zarholding.zar.model.response.driver.DriverResponse
import com.zarholding.zar.model.response.notification.NotificationUnreadResponse
import com.zarholding.zar.model.response.taxi.*
import com.zarholding.zar.model.response.trip.*
import com.zarholding.zar.model.response.user.UserInfoResponseModel
import com.zarholding.zar.model.response.user.UserPermissionResponseModel
import com.zarholding.zar.model.response.user.UserResponseModel
import retrofit2.Response
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
    suspend fun requestLogin(@Body login : LoginRequestModel) : Response<LoginResponseModel>


    @GET("${v1}/LogIn/login-userInfo")
    suspend fun requestUserInfo(
        @Header("Authorization") token : String
    ) : Response<UserInfoResponseModel>


    @POST("${v1}/LogIn/login-userInfo")
    suspend fun requestUserInfo(
        @Body request : UserInfoRequest,
        @Header("Authorization") token : String
    ) : Response<UserInfoResponseModel>


    @GET("${v1}/LogIn/get-persmissions")
    suspend fun requestUserPermission(
        @Header("Authorization") token : String
    ) : Response<UserPermissionResponseModel>


    @POST("${v1}/LogIn/get-filter-users")
    suspend fun requestGetUser(
        @Body request : FilterUserRequestModel,
        @Header("Authorization") token : String
    ) : Response<UserResponseModel>


    @GET("$v1/PersonnelsRegisteredStation/list-registered-trip")
    suspend fun requestGetAllTrips(
        @Header("Authorization") token : String
    ) : Response<TripResponseModel>



    @POST("$v1/PersonnelsRegisteredStation/register-station")
    suspend fun requestRegisterStation(
        @Body registerStationModel: RequestRegisterStationModel,
        @Header("Authorization") token : String
    ) : Response<RegisterStationResponseModel>


    @GET("$v1/PersonnelsRegisteredStation/delete-register-station/{id}")
    suspend fun requestDeleteRegisteredStation(
        @Path("id") id : Int,
        @Header("Authorization") token : String
    ) : Response<DeleteRegisteredStationResponseModel>


    @POST("$v1/Article/list-articles")
    suspend fun requestGetArticles(
        @Body articleRequestModel: ArticleRequestModel,
        @Header("Authorization") token : String
    ) : Response<ArticleResponseModel>


    @GET("$v1/personnelsRegisteredStation/list-request-registered-trip")
    suspend fun requestGetTripRequestRegister(
        @Header("Authorization") token : String
    ) : Response<TripRequestRegisterResponseModel>


    @POST("$v1/personnelsRegisteredStation/response-request-registered-trip")
    suspend fun requestConfirmAndRejectTripRequestRegister(
        @Body request : List<TripRequestRegisterStatusModel>,
        @Header("Authorization") token : String
    ) : Response<TripRequestRegisterStatusResponseModel>


    @GET("$v1/CarRequest/get-favoritelocations")
    suspend fun requestGetTaxiFavPlace(
        @Header("Authorization") token: String
    ) : Response<TaxiFavPlaceResponse>


    @POST("$v1/CarRequest/Add-favoritelocations")
    suspend fun requestAddFavPlace(
        @Body request : TaxiAddFavPlaceRequest,
        @Header("Authorization") token: String
    ) : Response<TaxiAddFavePlaceResponse>


    @GET("$v1/CarRequest/delete-favoritelocations/{id}")
    suspend fun requestDeleteFavPlace(
        @Path("id") id : Int,
        @Header("Authorization") token : String
    ) : Response<TaxiRemoveFavePlaceResponse>


    @POST("$v1/CarRequest/Add-carrequest")
    suspend fun requestTaxi(
        @Body request : TaxiRequestModel,
        @Header("Authorization") token : String
    ) : Response<TaxiRequestResponse>


    @POST("$v1/CarRequest/get-mycarrequestlist")
    suspend fun requestMyTaxiRequestList(
        @Body request : AdminTaxiListRequest,
        @Header("Authorization") token : String
    ) : Response<AdminTaxiRequestResponse>


    @POST("$v1/CarRequest/get-CarRequestlist")
    suspend fun requestTaxiList(
        @Body request : AdminTaxiListRequest,
        @Header("Authorization") token : String
    ) : Response<AdminTaxiRequestResponse>


    @POST("$v1/CarRequest/responeCarRequest")
    suspend fun requestChangeStatusOfTaxiRequests(
        @Body request : TaxiChangeStatusRequest,
        @Header("Authorization") token : String
    ) : Response<TaxiRemoveFavePlaceResponse>


    @POST("$v1/CarRequest/assign-Driver-ToRrquest")
    suspend fun requestAssignDriverToRequest(
        @Body request : AssignDriverRequest,
        @Header("Authorization") token : String
    ) : Response<TaxiRemoveFavePlaceResponse>


    @GET("$v1/DropDown/get-companies")
    suspend fun requestGetCompanies(
        @Header("Authorization") token : String
    ) : Response<CompanyResponse>


    @GET("$v1/CarRequest/get-Drivers")
    suspend fun requestGetDriver(
        @Query("driverType") type : EnumDriverType,
        @Query("companyCode") companyCode : String?,
        @Header("Authorization") token : String
    ) : Response<DriverResponse>


    @POST("$v1/Notification/unread-count-by-user")
    suspend fun requestGetNotificationUnreadCount(
        @Body request : NotificationUnreadCountRequestModel,
        @Header("Authorization") token : String
    ) : Response<NotificationUnreadResponse>


    @GET("$v1/Notification/get-messages")
    suspend fun requestGetNotification(
        @Header("Authorization") token : String
    ) : Response<NotificationResponse>

    @POST("$v1/Notification/set-read")
    suspend fun requestReadNotification(
        @Body ids : List<Int>,
        @Header("Authorization") token : String
    ) : Response<TaxiRemoveFavePlaceResponse>

    @POST("$v1/CarRequest/driverResponeCarRequest")
    suspend fun requestDriverChangeTripStatus(
        @Body request : DriverChangeTripStatus,
        @Header("Authorization") token : String
    ) : Response<TaxiRemoveFavePlaceResponse>


    @POST("$v1/User/Edit-Myinfo")
    suspend fun requestChangeCarPlaque(
        @Body request : CarPlaqueEditRequest,
        @Header("Authorization") token : String
    ) : Response<TaxiRemoveFavePlaceResponse>


    @GET
    suspend fun requestGetAddress(
        @Url url : String
    ) : Response<AddressResponseModel>


    @GET
    suspend fun requestGetSuggestionAddress(
        @Url url: String
    ): Response<List<AddressSuggestionModel>>

}