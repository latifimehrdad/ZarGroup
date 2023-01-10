package com.zarholding.zar.view.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Created by m-latifi on 11/8/2022.
 */

@HiltAndroidApp
class App : Application(), Configuration.Provider{

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
}