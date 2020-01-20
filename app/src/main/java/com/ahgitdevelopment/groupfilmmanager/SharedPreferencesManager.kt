package com.ahgitdevelopment.groupfilmmanager

import android.content.Context
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(context: Context) {

    private val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    fun setDatabaseId(databaseId: String) = prefs.edit().putString(DATABASE_ID, databaseId).apply()
    fun getDatabaseId(): String = prefs.getString(DATABASE_ID, "") ?: ""

    fun setUserId(userId: String) = prefs.edit().putString(USER_ID, userId).apply()
    fun getUserId(): String = prefs.getString(USER_ID, "") ?: ""

    fun setUserName(userName: String) = prefs.edit().putString(USER_NAME, userName).apply()
    fun getUserName(): String = prefs.getString(USER_NAME, "") ?: ""

    fun clear() = prefs.edit().clear().apply()

    companion object {
        const val DATABASE_ID: String = "database_id"
        const val USER_ID: String = "user_id"
        const val USER_NAME: String = "user_name"
    }
}