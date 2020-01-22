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

class AddMovieViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepository by lazy { FirestoreRepository() }

    var movieName = MutableLiveData<String>()
    var movieDescription1 = MutableLiveData<String>()
    var movieDescription2 = MutableLiveData<String>()

    val isMovieSaved = SingleLiveEvent<Boolean>()

    fun saveMovieIntoDatabase() {
        viewModelScope.launch {

            if (!movieName.value.isNullOrBlank()) {
                Movie().apply {
                    this.name = movieName.value?.trim() ?: ""
                    this.description1 = movieDescription1.value?.trim() ?: ""
                    this.description2 = movieDescription2.value?.trim() ?: ""
                }.run {
                    val databaseId = getApplication<BaseApplication>().prefs.getDatabaseId()
                    firestoreRepository.saveMovie(databaseId, this) //FIXME: Do not have error management
                    isMovieSaved.value = true
                }
            } else {
                isMovieSaved.value = false
            }
        }
    }
}
