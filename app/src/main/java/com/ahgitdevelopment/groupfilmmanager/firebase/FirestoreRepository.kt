package com.ahgitdevelopment.groupfilmmanager.firebase

import android.util.Log
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashSet

class FirestoreRepository(application: BaseApplication) {

    private val db by lazy { FirebaseFirestore.getInstance() }

    private val databaseId = application.prefs.getDatabaseId()
    private val userId = application.prefs.getUserId()
    private val userName = application.prefs.getUserName()

    /**
     *
     */
    fun createDb(): String {
        return db.collection(ROOT).document().id
    }

    /**
     *
     */
    fun createUser(): String {
        return db.collection(ROOT).document(databaseId).collection(USERS).document().id
    }

    /**
     *
     */
    suspend fun saveUserIntoDatabase() {
        val user = hashMapOf(
            USER_NAME to userName.trim().toUpperCase(Locale.getDefault())
        )

        db.collection(ROOT).document(databaseId).collection(USERS).document(userId).set(user).await()
    }

    /**
     * To know if a database has been created, at least one user has to exist (the creator)
     * due to movies could not be added.
     */
    suspend fun existDatabaseId(databaseId: String): QuerySnapshot? {
        return db.collection(ROOT).document(databaseId).collection(USERS).get().await()
    }

    /**
     *
     */
    suspend fun saveMovie(movie: Movie) {

        movie.id = db.collection(ROOT).document(databaseId).collection(MOVIES).document().get().await().id

        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movie.id).set(movie).await()
        addAllUsersIntoDatabase(movie.id)
    }

    private suspend fun addAllUsersIntoDatabase(movieId: String) {
        // Get users
        db.collection(ROOT).document(databaseId).collection(USERS).get().await().forEach {
            val user = User(id = it.id, name = it.getString(USER_NAME) ?: "", isWatched = false, isWanted = false)

            // Save users in the movie
            db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId)
                .collection(MOVIE_USERS_COLLECTION).document(user.id).set(user).await()
        }
    }

    /**
     *
     */
    suspend fun updateMoviesWithNewUser() {

        //Add user to all movies
        val dbRef = db.collection(ROOT).document(databaseId)
        val moviesRef = dbRef.collection(MOVIES)
        val userRef = dbRef.collection(USERS)

        val userName = userRef.document(userId).get().await().toObject(String::class.java)

        moviesRef.get().await().forEach {
            moviesRef.document(it.id).collection(MOVIE_USERS_COLLECTION).document(userId)
                .set(User(id = userId, name = userName ?: "", isWatched = false, isWanted = false))
        }
    }

    /**
     *
     */
    suspend fun getAllMovies(): QuerySnapshot {
        return db.collection(ROOT).document(databaseId).collection(MOVIES).orderBy(USER_NAME).get().await()
    }

    fun getAllMoviesRef() = db.collection(ROOT).document(databaseId).collection(MOVIES)


    /**
     * Get every movieId in which any user has click that want to watch that movie,
     * then retrieve all movies stored
     */
    suspend fun getAllFavouritesMovies(): QuerySnapshot? {

        val wantedMovieIds = HashSet<String>()

        // Loop throw the movies
        db.collection(ROOT).document(databaseId).collection(MOVIES).orderBy(USER_NAME).get().await()
            .forEach { movieSnapshot ->

                val movieId = movieSnapshot.id
                db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).let {

                    // Look for user who want to see the movie
                    it.collection(MOVIE_USERS_COLLECTION).get().await().forEach { userSnapshot ->
                        val user = userSnapshot.toObject(User::class.java)
                        if (user.isWanted) {
                            wantedMovieIds.add(movieId) //Store the movieId
                        }
                    }
                }
            }

        return if (wantedMovieIds.toList().isNotEmpty()) {
            db.collection(ROOT).document(databaseId).collection(MOVIES)
                .whereIn(MOVIE_ID, wantedMovieIds.toList().take(MAX_WHEREIN_ELEMENTS)).get().await()
        } else null
    }

    /**
     *
     */
    suspend fun getAllUsersInMovie(movieId: String): QuerySnapshot {
        return db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId)
            .collection(MOVIE_USERS_COLLECTION)
            .orderBy(USER_NAME).get().await()
    }

    /**
     *
     */
    suspend fun getDatabaseIdByName(movieName: String): String {
        var movieId = ""
        db.collection(ROOT).document(databaseId).collection(MOVIES).whereEqualTo(MOVIE_NAME, movieName).get()
            .await().forEach {
                val movie = it.toObject(Movie::class.java)
                if (movie.name == movieName) {
                    movieId = it.id
                }
            }
        return movieId
    }

    /**
     *
     */
    fun setWatchedMovie(movieId: String, user: User) {
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId)
            .collection(MOVIE_USERS_COLLECTION).document(user.id)
            .update(MOVIE_USERS_COLLECTION_WATCHED, user.isWatched).addOnCompleteListener {
                Log.i(TAG, "Set watched: ${it.isSuccessful}")
            }
    }

    /**
     *
     */
    fun setWantedMovie(movieId: String, user: User) {
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId)
            .collection(MOVIE_USERS_COLLECTION).document(user.id)
            .update(MOVIE_USERS_COLLECTION_WANTED, user.isWanted)
    }

    suspend fun removeMovie(movieId: String) {

        //Remove movieUsers collection first
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).collection(MOVIE_USERS_COLLECTION)
            .get().await().forEach {
                it.reference.delete()
            }

        //Then remove document
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).delete().await()
    }


    companion object {
        private const val TAG = "FirestoreRepository"

        // Database tables
        const val ROOT = "root"
        const val MOVIES = "movies"
        const val USERS = "users"

        // User fields
        const val USER_NAME = "name"

        // Movie fields
        const val MOVIE_ID = "id"
        const val MOVIE_NAME = "name"
        const val MOVIE_USERS_COLLECTION = "movieUsers"
        const val MOVIE_USERS_COLLECTION_NAME = "name"
        const val MOVIE_USERS_COLLECTION_WANTED = "wanted"
        const val MOVIE_USERS_COLLECTION_WATCHED = "watched"

        /**
         * Firebase limitation filtering collection elements
         */
        const val MAX_WHEREIN_ELEMENTS = 10
    }
}