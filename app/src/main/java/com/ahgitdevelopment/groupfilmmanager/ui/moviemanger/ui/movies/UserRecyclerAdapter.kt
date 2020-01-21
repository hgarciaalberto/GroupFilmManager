package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserRecyclerAdapter(private val movie: Movie) : RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_checked, parent, false)
        return ViewHolder(view, parent.context, movie)
    }

    override fun getItemCount(): Int {
        return movie.users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movie.users[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(itemView: View, private val context: Context, private val movie: Movie) :
        RecyclerView.ViewHolder(itemView) {

        private val firestoreRepository by lazy { FirestoreRepository() }

        private val name = itemView.findViewById(R.id.name) as TextView
        private val watched = itemView.findViewById(R.id.watched) as CheckBox
        private val wanted = itemView.findViewById(R.id.wanted) as CheckBox

        fun bind(user: User) {
            name.text = user.name
            watched.text = context.resources.getString(R.string.user_movieWatched)
            watched.isChecked = user.isWatched
            wanted.text = context.resources.getString(R.string.user_movieWanted)
            wanted.isChecked = user.isWanted

            watched.setOnCheckedChangeListener { _, isChecked ->

                // Get movieId
                val databaseId = (context.applicationContext as BaseApplication).prefs.getDatabaseId()

                val uiScope = CoroutineScope(Dispatchers.Main)
                uiScope.launch {
                    movie.name.let { movieName ->
                        firestoreRepository.getDatabaseIdByName(databaseId, movieName).let { movieId ->
                            if (movieId.isNotBlank()) {
                                user.isWatched = isChecked
                                firestoreRepository.setWatchedMovie(databaseId, movieId, user)
                            }
                        }
                    }
                }
            }

            wanted.setOnCheckedChangeListener { _, isChecked ->

                // Get movieId
                val databaseId = (context.applicationContext as BaseApplication).prefs.getDatabaseId()

                val uiScope = CoroutineScope(Dispatchers.Main)
                uiScope.launch {
                    movie.name.let { movieName ->
                        firestoreRepository.getDatabaseIdByName(databaseId, movieName).let { movieId ->
                            if (movieId.isNotBlank()) {
                                user.isWanted = isChecked
                                firestoreRepository.setWantedMovie(databaseId, movieId, user)
                            }
                        }
                    }
                }
            }
        }
    }
}