package com.roshi.runningtrackerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        setUpTimBer()

    }

    private fun setUpTimBer() {
        Timber.plant(Timber.DebugTree())
    }
}