package com.zarholding.zar.hilt

import com.google.gson.Gson
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiBPMS
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.utility.UnAuthorizationManager
import com.zarholding.zar.view.activity.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by m-latifi on 11/8/2022.
 */

@Module
@InstallIn(SingletonComponent::class)
class Providers {

    companion object {
        const val url = "http://5.160.125.98:5081"
    }

    //---------------------------------------------------------------------------------------------- provideBPMSUrl
    @Provides
    @Singleton
    @Named("Normal")
    fun provideBPMSUrl() = "http://192.168.50.153:8081"
    //---------------------------------------------------------------------------------------------- provideBPMSUrl



    //---------------------------------------------------------------------------------------------- provideRemoteErrorEmitter
    @Provides
    @Singleton
    fun provideRemoteErrorEmitter() : RemoteErrorEmitter = MainActivity()
    //---------------------------------------------------------------------------------------------- provideRemoteErrorEmitter



    //---------------------------------------------------------------------------------------------- provideApiBPMS
    @Provides
    @Singleton
    fun provideApiBPMS(@Named("Normal") retrofit: Retrofit) : ApiBPMS =
        retrofit.create(ApiBPMS::class.java)
    //---------------------------------------------------------------------------------------------- provideApiBPMS



    //---------------------------------------------------------------------------------------------- provideRetrofit
    @Provides
    @Singleton
    @Named("SuperApp")
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
    //---------------------------------------------------------------------------------------------- provideRetrofit



    //---------------------------------------------------------------------------------------------- provideApiSuperApp
    @Provides
    @Singleton
    fun provideApiSuperApp(@Named("SuperApp") retrofit: Retrofit) : ApiSuperApp =
        retrofit.create(ApiSuperApp::class.java)
    //---------------------------------------------------------------------------------------------- provideApiSuperApp


    //---------------------------------------------------------------------------------------------- provideUnAuthorization
    @Provides
    @Singleton
    fun provideUnAuthorization() = UnAuthorizationManager()
    //---------------------------------------------------------------------------------------------- provideUnAuthorization

}