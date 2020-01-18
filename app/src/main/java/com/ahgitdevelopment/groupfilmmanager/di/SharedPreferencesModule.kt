package com.ahgitdevelopment.groupfilmmanager.di

import android.content.Context
import com.ahgitdevelopment.groupfilmmanager.SharedPreferencesManager
import dagger.Module
import dagger.Provides

@Module
class SharedPreferencesModule(private val context: Context) {

    @Provides
    fun getSharedPrefs(): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }
}
