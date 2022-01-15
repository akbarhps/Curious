package com.charuniverse.curious

import android.app.Application
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CuriousApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
    }
}