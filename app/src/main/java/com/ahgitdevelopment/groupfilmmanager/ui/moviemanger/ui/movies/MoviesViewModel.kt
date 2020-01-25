package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.common.SingleLiveEvent
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository.Companion.MOVIE_NAME
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MoviesViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MoviesViewModel"

    private val firestoreRepository by lazy { FirestoreRepository(application as BaseApplication) }

    val updateMovies = SingleLiveEvent<Query?>()

    fun getMovies(isFavouriteFragment: Boolean?) {
        viewModelScope.launch {
            Log.d(TAG, "valor del favourite fragment: ${isFavouriteFragment.toString()}")
            when (isFavouriteFragment) {
                true -> firestoreRepository.getAllFavouritesMovies()
                false -> firestoreRepository.getAllMovies()
                else -> null
            }.let {
                if (it != null) {
                    Log.d(TAG, "Num of elements in the array: ${it.query.get().await().size()}")
                    updateMovies.value = it.query
                } else {
                    Log.d(TAG, "query null")
                    updateMovies.value = null
                }
            }
        }
    }

    fun searchFilter(searchText: String) {
        viewModelScope.launch {
            firestoreRepository.getAllMoviesRef().orderBy(MOVIE_NAME)
                .startAt(searchText).endAt("${searchText}\uf8ff")
                .let {
                    updateMovies.value = it
                }
        }
    }

    fun removeMovie(movieId: String) {
        viewModelScope.launch {
            firestoreRepository.removeMovie(movieId).let {
                getMovies(true).let {
                    updateMovies.value = null
                }
            }
        }
    }
}