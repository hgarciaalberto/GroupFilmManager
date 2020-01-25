package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.common.SingleLiveEvent
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository.Companion.MOVIE_NAME
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MoviesViewModel(application: Application) : AndroidViewModel(application), EventListener<QuerySnapshot> {
    private val TAG = "MoviesViewModel"

    private val firestoreRepository by lazy { FirestoreRepository(application as BaseApplication) }

    val updateMovies = SingleLiveEvent<List<Movie>>()

    /**
     * ContentLoadingProgressBar in the xml to show the loading time
     */
    private val _loading = MutableLiveData<Int>()
    val loading: LiveData<Int> = _loading

    init {
        _loading.value = View.GONE
    }


    fun getMovies(isFavouriteFragment: Boolean?) {
        Log.d(TAG, "valor del favourite fragment: ${isFavouriteFragment.toString()}")
        viewModelScope.launch {
            _loading.value = View.VISIBLE
            when (isFavouriteFragment) {
                true -> firestoreRepository.getAllFavouritesMovies(this@MoviesViewModel)
                false -> firestoreRepository.getAllMovies(this@MoviesViewModel)
            }
        }
    }

    fun searchFilter(searchText: String) {


        //FIXME
        viewModelScope.launch {
            _loading.value = View.VISIBLE
            firestoreRepository.getAllMoviesRef().orderBy(MOVIE_NAME)
                .startAt(searchText).endAt("${searchText}\uf8ff").get().await()
                .let {
                    updateMovies.value = it.toObjects(Movie::class.java)
                }
        }
    }

    fun removeMovie(movieId: String) {
        viewModelScope.launch {
            firestoreRepository.removeMovie(movieId).run {
                getMovies(false)
            }
        }
    }

    override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
//        if (exception != null) {
//            Log.e(TAG, "Firestore listener fail", exception)
//            updateMovies.value = arrayListOf()
//        }

        Log.d(TAG, "Num of elements in the array: ${snapshot?.size()}")
        updateMovies.value = snapshot?.toObjects(Movie::class.java)

        _loading.value = View.GONE
    }
}