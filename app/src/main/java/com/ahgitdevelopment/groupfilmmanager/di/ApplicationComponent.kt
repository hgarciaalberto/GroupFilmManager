package com.ahgitdevelopment.groupfilmmanager.di

import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import dagger.Component

@Component(
    modules = [
        SharedPreferencesModule::class
    ]
)
interface ApplicationComponent {

    fun inject(application: BaseApplication)

}