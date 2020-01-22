package com.ahgitdevelopment.groupfilmmanager.firebase

import android.util.Log
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashSet

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    //TODO: Remove al parameters that come from SharedPrefs like databaseId


    /**
     *
     */
    fun createDb(): String = db.collection(ROOT).document().id

    /**
     *
     */
    fun createUser(databaseId: String) = db.collection(ROOT).document(databaseId).collection(USERS).document().id

    /**
     *
     */
    suspend fun saveUserIntoDatabase(databaseId: String, userId: String, userName: String) {
        val user = hashMapOf(
            USER_NAME to userName.trim().toUpperCase(Locale.getDefault())
        )

        db.collection(ROOT).document(databaseId).collection(USERS).document(userId).set(user).await()
    }

    /**
     * To know if a database has been created, at least one user has to exist (the creator)
     * due to movies could not be added.
     */
    suspend fun existDatabaseId(databaseId: String): QuerySnapshot? =
        db.collection(ROOT).document(databaseId).collection(USERS).get().await()

    /**
     *
     */
    suspend fun saveMovie(databaseId: String, movie: Movie) {

        movie.id = db.collection(ROOT).document(databaseId).collection(MOVIES).document().get().await().id

        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movie.id).set(movie).await()
        addAllUsersIntoDatabase(databaseId, movie.id)
    }

    private suspend fun addAllUsersIntoDatabase(databaseId: String, movieId: String) {
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
    suspend fun updateMoviesWithNewUser(databaseId: String, userId: String) {

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
    suspend fun getAllMovies(databaseId: String): QuerySnapshot =
        db.collection(ROOT).document(databaseId).collection(MOVIES).orderBy(USER_NAME).get().await()

    /**
     *
     */
    suspend fun getAllUsersInMovie(databaseId: String, movieId: String): QuerySnapshot =
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).collection(MOVIE_USERS_COLLECTION)
            .orderBy(USER_NAME).get().await()

    /**
     * Get every movieId in which any user has click that want to watch that movie,
     * then retrieve all movies stored
     */
    suspend fun getAllFavouritesMovies(databaseId: String): QuerySnapshot? {

        val wantedMovieIds = HashSet<String>()

        // Loop throw the movies
        db.collection(ROOT).document(databaseId).collection(MOVIES).get().await().forEach { movieSnapshot ->

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

        return if (wantedMovieIds.toList().size > 0) {
            db.collection(ROOT).document(databaseId).collection(MOVIES).whereIn(MOVIE_ID, wantedMovieIds.toList()).get()
                .await()
        } else null
    }

    /**
     *
     */
    suspend fun getDatabaseIdByName(databaseId: String, movieName: String): String {
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
    fun setWatchedMovie(databaseId: String, movieId: String, user: User) {
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId)
            .collection(MOVIE_USERS_COLLECTION).document(user.id)
            .update(MOVIE_USERS_COLLECTION_WATCHED, user.isWatched).addOnCompleteListener {
                Log.i(TAG, "Set watched: ${it.isSuccessful}")
            }
    }

    /**
     *
     */
    fun setWantedMovie(databaseId: String, movieId: String, user: User) {
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId)
            .collection(MOVIE_USERS_COLLECTION).document(user.id)
            .update(MOVIE_USERS_COLLECTION_WANTED, user.isWanted)
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
        const val MOVIE_USERS_COLLECTION_NAME = "wanted"
        const val MOVIE_USERS_COLLECTION_WANTED = "wanted"
        const val MOVIE_USERS_COLLECTION_WATCHED = "watched"
    }
}