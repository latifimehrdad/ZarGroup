package com.zarholding.zar.hilt

import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiBPMS
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.view.activity.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by m-latifi on 11/8/2022.
 */

@Module
@InstallIn(SingletonComponent::class)
class Providers {

    //---------------------------------------------------------------------------------------------- provideBPMSUrl
    @Provides
    @Singleton
    @Named("Normal")
    fun provideBPMSUrl(): String {
        return "http://192.168.50.153:8081"
    }
    //---------------------------------------------------------------------------------------------- provideBPMSUrl




    //---------------------------------------------------------------------------------------------- provideSuperAppUrl
    @Provides
    @Singleton
    @Named("SuperApp")
    fun provideSuperAppUrl(): String {
        return "http://192.168.50.153:8081"
    }
    //---------------------------------------------------------------------------------------------- provideSuperAppUrl



    //---------------------------------------------------------------------------------------------- provideRemoteErrorEmitter
    @Provides
    @Singleton
    fun provideRemoteErrorEmitter() : RemoteErrorEmitter {
        return MainActivity()
    }
    //---------------------------------------------------------------------------------------------- provideRemoteErrorEmitter



    //---------------------------------------------------------------------------------------------- provideApiBPMS
    @Provides
    @Singleton
    fun provideApiBPMS(@Named("Normal") retrofit: Retrofit): ApiBPMS {
        return retrofit.create(ApiBPMS::class.java)
    }
    //---------------------------------------------------------------------------------------------- provideApiBPMS



    //---------------------------------------------------------------------------------------------- provideApiSuperApp
    @Provides
    @Singleton
    fun provideApiSuperApp(@Named("SuperApp") retrofit: Retrofit): ApiSuperApp {
        return retrofit.create(ApiSuperApp::class.java)
    }
    //---------------------------------------------------------------------------------------------- provideApiSuperApp

}