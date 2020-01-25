package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.addmovie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.common.SingleLiveEvent
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import kotlinx.coroutines.launch
import java.util.*

class AddMovieViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepository by lazy { FirestoreRepository(application as BaseApplication) }

    var movieName = MutableLiveData<String>()
    var movieDescription1 = MutableLiveData<String>()
    var movieDescription2 = MutableLiveData<String>()

    val isMovieSaved = SingleLiveEvent<Boolean>()

    fun saveMovieIntoDatabase() {
        viewModelScope.launch {

            if (!movieName.value.isNullOrBlank()) {
                Movie().apply {
                    this.name = movieName.value?.trim()?.toUpperCase(Locale.getDefault()) ?: ""
                    this.description1 = movieDescription1.value?.trim() ?: ""
                    this.description2 = movieDescription2.value?.trim() ?: ""
                }.run {
                    firestoreRepository.saveMovie(this)
                    isMovieSaved.value = true
                }
            } else {
                isMovieSaved.value = false
            }
        }
    }
}
