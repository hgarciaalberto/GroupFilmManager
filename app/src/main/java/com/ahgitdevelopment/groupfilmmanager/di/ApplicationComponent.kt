package com.ahgitdevelopment.groupfilmmanager.di

import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import dagger.Component

@Component(
    modules = [
        SharedPreferencesModule::class
    ]
)
interface ApplicationComponent {

    fun inject(application: BaseApplication)
    fun inject(firestoreRepository: FirestoreRepository)

}