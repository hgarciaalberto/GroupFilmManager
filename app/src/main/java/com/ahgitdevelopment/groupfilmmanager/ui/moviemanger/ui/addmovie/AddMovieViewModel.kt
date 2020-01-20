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

//    @Bindable
//    fun getName(): String {
//        data.name
//    }
//
//    fun setName(value: String) {
//        // Avoids infinite loops.
//        if (data.name != value) {
//            data.name = value
//
//            // React to the change.
//            saveData()
//
//            // Notify observers of a new value.
//            notifyPropertyChanged(BR.name)
//        }
//    }
//
//    @Bindable
//    fun getDesctiption1(): String = description1
//
//    fun setDesctiption1(description1: String) {
//        this.description1 = description1
//    }
//
//    @Bindable
//    fun getDesctiption2(): String = description2
//
//    fun setDesctiption2(description2: String) {
//        this.description2 = description2
//    }

    fun saveMovieIntoDatabase() {
        viewModelScope.launch {
            //            if (name.isNotBlank() && description1.isNotBlank() && description2.isNotBlank()) {
            if (!name.value.isNullOrBlank() && !description1.value.isNullOrBlank() && !description2.value.isNullOrBlank()) {
//                Movie(name, description1, description2).run {
                Movie(name.value ?: "", description1.value ?: "", description2.value ?: "").run {
                    //                    val databaseId = (application as BaseApplication).prefs.getDatabaseId()
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
