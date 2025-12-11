package org.example.project

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        ToastManager.init(this)
    }
}
