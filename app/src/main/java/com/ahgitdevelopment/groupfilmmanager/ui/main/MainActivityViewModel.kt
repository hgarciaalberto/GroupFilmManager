package com.ahgitdevelopment.groupfilmmanager.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.common.SingleLiveEvent
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepository by lazy { FirestoreRepository(application as BaseApplication) }

    val createDbAction = SingleLiveEvent<Boolean>()
    val joinDbAction = SingleLiveEvent<Boolean>()

    fun createDb() {

        viewModelScope.launch {
            firestoreRepository.createDb().let { databaseId ->
                setPrefsDatabaseId(databaseId)
                setPrefsUserId(getUserId())
            }

            createDbAction.value = true
        }
    }

    fun getUserId(): String {
        return if (getPrefsUserId().isBlank())
            firestoreRepository.createUser()
        else
            getPrefsUserId()
    }

    fun saveUserIntoDatabase() {
        viewModelScope.launch {
            firestoreRepository.saveUserIntoDatabase()
        }
    }

    fun joinDb() {
        joinDbAction.value = true
    }

    suspend fun existDatabaseId(databaseId: String): QuerySnapshot? {
        return firestoreRepository.existDatabaseId(databaseId)
    }

    suspend fun updateMoviesWithNewUser() {
        firestoreRepository.updateMoviesWithNewUser()
    }


    fun setPrefsDatabaseId(node: String) = getApplication<BaseApplication>().prefs.setDatabaseId(node)
    fun getPrefsDatabaseId(): String = getApplication<BaseApplication>().prefs.getDatabaseId()

    fun setPrefsUserId(userId: String) = getApplication<BaseApplication>().prefs.setUserId(userId)
    fun getPrefsUserId(): String = getApplication<BaseApplication>().prefs.getUserId()

    fun setPrefsUserName(userName: String) = getApplication<BaseApplication>().prefs.setUserName(userName)
    fun getPrefsUserName(): String = getApplication<BaseApplication>().prefs.getUserName()


}
