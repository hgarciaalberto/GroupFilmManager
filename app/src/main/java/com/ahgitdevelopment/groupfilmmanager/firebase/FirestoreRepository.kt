package com.ahgitdevelopment.groupfilmmanager.firebase

import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createDb(): String = db.collection(ROOT).document().id

    fun createUser(databaseId: String) = db.collection(ROOT).document(databaseId).collection(USERS).document().id

    /**
     * To know if a database has been created, at least one user has to exist (the creator)
     * due to movies could not be added.
     */
    suspend fun existDatabaseId(databaseId: String): QuerySnapshot? =
        db.collection(ROOT).document(databaseId).collection(USERS).get().await()


    suspend fun saveMovie(databaseId: String, movie: Movie) {

        // Get users
        val users =
            db.collection(ROOT).document(databaseId).collection(USERS).get().await().toObjects(User::class.java)

        movie.users.addAll(users)

        db.collection(ROOT).document(databaseId).collection(MOVIES).add(movie).await()
    }

    fun saveUserIntoDatabase(databaseId: String, userId: String, userName: String) =
        db.collection(ROOT).document(databaseId).collection(USERS).document(userId).update(USER_NAME, userName)

    suspend fun updateMoviesWithNewUser(databaseId: String, userId: String) {

        //Add user to all movies
        val dbRef = db.collection(ROOT).document(databaseId)
        val moviesRef = dbRef.collection(MOVIES)
        val userRef = dbRef.collection(USERS)

        val userName = userRef.document(userId).get().await().toObject(String::class.java)

        moviesRef.get().await().forEach {
            moviesRef.document(it.id).update(MOVIE_USERS_LIST, FieldValue.arrayUnion(
                User(userName ?: "", isWatched = false, isWanted = false)))
        }
    }

    suspend fun getAllMovies(databaseId: String): QuerySnapshot =
        db.collection(ROOT).document(databaseId).collection(MOVIES).orderBy(USER_NAME).get().await()

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

    suspend fun setWatchedMovie(databaseId: String, movieId: String, user: User) {

        val movie = db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).get().await()
            .toObject(Movie::class.java)
        val userToRemove = movie?.users?.find { it.name == user.name }

        // Remove user
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).update(MOVIE_USERS_LIST,
            FieldValue.arrayRemove(userToRemove))

        // Add user with correct checked element
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).update(MOVIE_USERS_LIST,
            FieldValue.arrayUnion(user))
    }

    suspend fun setWantedMovie(databaseId: String, movieId: String, user: User) {
        val movie = db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).get().await()
            .toObject(Movie::class.java)
        val userToRemove = movie?.users?.find { it.name == user.name }

        // Remove user
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).update(MOVIE_USERS_LIST,
            FieldValue.arrayRemove(userToRemove))

        // Add user with correct checked element
        db.collection(ROOT).document(databaseId).collection(MOVIES).document(movieId).update(MOVIE_USERS_LIST,
            FieldValue.arrayUnion(user))
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
        const val MOVIE_NAME = "name"
        const val MOVIE_USERS_LIST = "users"
    }
}