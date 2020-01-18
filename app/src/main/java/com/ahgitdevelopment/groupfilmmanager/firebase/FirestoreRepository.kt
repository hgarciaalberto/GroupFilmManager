package com.ahgitdevelopment.groupfilmmanager.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var databaseId: String
    private lateinit var userId: String


    fun createDb(): String {
        databaseId = db.collection(ROOT).document().id
        return databaseId
    }

    fun createUser(): String {
        userId = db.collection(ROOT).document(databaseId).collection(USER).document().id
        return userId
    }

    suspend fun exitDatabaseId(databaseId: String): DocumentSnapshot? {
        return db.collection(ROOT).document(databaseId).get().await()
    }

    companion object {
        const val ROOT = "root"
        const val FILM = "film"
        const val USER = "user"

    }
}