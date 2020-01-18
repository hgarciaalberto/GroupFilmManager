package com.ahgitdevelopment.groupfilmmanager.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.common.SingleLiveEvent
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepository by lazy { FirestoreRepository() }

    val createDbAction = SingleLiveEvent<Boolean>()
    val joinDbAction = SingleLiveEvent<Boolean>()

    fun createDb() {

        val databaseId: String
        val userId: String
        firestoreRepository.createDb().let { databaseId = it }
        firestoreRepository.createUser().let { userId = it }

        setPrefsDatabaseId(databaseId)
        setPrefsUserId(userId)

        createDbAction.value = databaseId.isNotBlank() && userId.isNotBlank()
    }

    fun joinDb() {
        joinDbAction.value = true
    }

    suspend fun existDatabaseId(databaseId: String): DocumentSnapshot? {
        return firestoreRepository.exitDatabaseId(databaseId)
    }

    fun setPrefsDatabaseId(node: String) = getApplication<BaseApplication>().prefs.setDatabaseId(node)
    fun getPrefsDatabaseId(): String = getApplication<BaseApplication>().prefs.getDatabaseId()

    fun setPrefsUserId(userId: String) = getApplication<BaseApplication>().prefs.setUserId(userId)
    fun getPrefsUserId(): String = getApplication<BaseApplication>().prefs.getUserId()


}
