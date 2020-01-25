package com.ahgitdevelopment.groupfilmmanager.base

import android.app.Application
import com.ahgitdevelopment.groupfilmmanager.SharedPreferencesManager
import com.ahgitdevelopment.groupfilmmanager.di.ApplicationComponent
import com.ahgitdevelopment.groupfilmmanager.di.DaggerApplicationComponent
import com.ahgitdevelopment.groupfilmmanager.di.SharedPreferencesModule
import javax.inject.Inject


class BaseApplication : Application() {

    private val TAG = "BaseApplication"

    // Instance of the AppComponent that will be used by all the Activities in the project
    private val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .sharedPreferencesModule(SharedPreferencesModule(applicationContext))
            .build()
    }

    @Inject
    lateinit var prefs: SharedPreferencesManager

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
    }
}

