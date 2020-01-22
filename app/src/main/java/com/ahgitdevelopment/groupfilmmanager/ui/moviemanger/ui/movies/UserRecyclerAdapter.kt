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
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserRecyclerAdapter(private val movieId: String, private val users: List<User>) :
    RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_checked, parent, false)
        return ViewHolder(view, parent.context, movieId)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(itemView: View, private val context: Context, private val movieId: String) :
        RecyclerView.ViewHolder(itemView) {

        private val firestoreRepository by lazy { FirestoreRepository(context.applicationContext as BaseApplication) }

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

                val uiScope = CoroutineScope(Dispatchers.Main)
                uiScope.launch {
                    user.isWatched = isChecked
                    firestoreRepository.setWatchedMovie(movieId, user)
                }
            }

            wanted.setOnCheckedChangeListener { _, isChecked ->

                val uiScope = CoroutineScope(Dispatchers.Main)
                uiScope.launch {
                    user.isWanted = isChecked
                    firestoreRepository.setWantedMovie(movieId, user)
                }
            }
        }
    }
}