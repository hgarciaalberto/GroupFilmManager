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

    var name = MutableLiveData<String>()
    var description1 = MutableLiveData<String>()
    var description2 = MutableLiveData<String>()

    val isMovieSaved = SingleLiveEvent<Boolean>()

    fun saveMovieIntoDatabase() {
        viewModelScope.launch {

            if (!name.value.isNullOrBlank()) {
                Movie(name.value?.trim() ?: "",
                    description1.value?.trim() ?: "",
                    description2.value?.trim() ?: "").run {
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
