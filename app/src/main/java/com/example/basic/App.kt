package com.example.basic

import android.app.Application
import com.example.basic.utils.PreferenceManager

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(applicationContext)
    }
}