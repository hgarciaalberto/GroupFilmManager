package com.ahgitdevelopment.groupfilmmanager.firebase

import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class FirestoreRepository {

//    private val databaseId by lazy { prefs.getDatabaseId() }
//    private val userId by lazy { prefs.getUserId() }

    private val db = FirebaseFirestore.getInstance()


    fun createDb(): String = db.collection(ROOT).document().id

    fun createUser(databaseId: String) =
        db.collection(ROOT).document(databaseId).collection(USERS).document().id

    /**Map
     * To know if a database has been created, at least one user has to exist (the creator)
     * due to movies could not be added.
     */
    suspend fun exitDatabaseId(databaseId: String): QuerySnapshot? {
        return db.collection(ROOT).document(databaseId).collection(USERS).get().await()
    }

    suspend fun saveMovie(databaseId: String, movie: Movie) {
        // Get users

        val users =
            db.collection(ROOT).document(databaseId).collection(USERS).get().await().toObjects(User::class.java)
        movie.users.addAll(users)

        db.collection(ROOT).document(databaseId).collection(MOVIES).add(movie).await()

    }

    fun saveUserIntoDatabase(databaseId: String, userId: String, userName: String) =
        db.collection(ROOT).document(databaseId).collection(USERS).document(userId).set(User(userName))

    suspend fun updateMoviesWithNewUser(databaseId: String, userId: String) {

        //Add user to all movies
        val dbRef = db.collection(ROOT).document(databaseId)
        val moviesRef = dbRef.collection(MOVIES)
        val userRef = dbRef.collection(USERS)

        val user = userRef.document(userId).get().await().toObject(User::class.java)

        moviesRef.get().await().forEach {
            moviesRef.document(it.id).update(MOVIE_USERS_LIST, FieldValue.arrayUnion(user))
        }
    }

    suspend fun getAllMovies(databaseId: String): QuerySnapshot =
        db.collection(ROOT).document(databaseId).collection(MOVIES).orderBy(USER_NAME).get().await()


    companion object {
        private const val TAG = "FirestoreRepository"

        // Database tables
        const val ROOT = "root"
        const val MOVIES = "movies"
        const val USERS = "users"

        // User fields
        const val USER_NAME = "name"

        // Movie fields
        const val MOVIE_USERS_LIST = "users"
    }
}