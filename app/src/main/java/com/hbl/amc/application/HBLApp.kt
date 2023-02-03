package com.hbl.amc.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hbl.amc.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HBLApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initiateKoin()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initiateKoin() {
        startKoin {
            androidLogger()
            androidContext(this@HBLApp)
            modules(
                provideDependency()
            )
        }
    }

    open fun provideDependency() = appComponent
}