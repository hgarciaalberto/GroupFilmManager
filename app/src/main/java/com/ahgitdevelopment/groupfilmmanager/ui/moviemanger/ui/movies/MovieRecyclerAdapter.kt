package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.data.User
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieRecyclerAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<MovieRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = movies.size


    class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        lateinit var movieId: String
        private val title = itemView.findViewById(R.id.title) as TextView

        private val info1 = itemView.findViewById(R.id.info1) as TextView
        private val info2 = itemView.findViewById(R.id.info2) as TextView
        private val recyclerView = itemView.findViewById(R.id.recyclerView) as RecyclerView

        private var usersCached: List<User>? = null

        private val firestoreRepository by lazy { FirestoreRepository(context.applicationContext as BaseApplication) }

        fun bind(movie: Movie) {
            movieId = movie.id

            title.text = movie.name
            info1.text = movie.description1
            info2.text = movie.description2

            info1.visibility = if (info1.text.isNotBlank()) View.VISIBLE else View.GONE
            info2.visibility = if (info2.text.isNotBlank()) View.VISIBLE else View.GONE

            recyclerView.visibility = View.GONE

            itemView.setOnClickListener {
                recyclerView.visibility = if (recyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE

                if (recyclerView.visibility == View.VISIBLE) {
                    setUserRecycler(movie)
                }
            }
        }

        private fun setUserRecycler(movie: Movie) {

            val uiScope = CoroutineScope(Dispatchers.Main)
            uiScope.launch {

                if (usersCached == null) {
                    usersCached = firestoreRepository.getAllUsersInMovie(movie.id).toObjects(User::class.java).toList()
                }

                val myAdapter = UserRecyclerAdapter(movie.id, usersCached!!).apply {
                    setHasStableIds(true)
                }

                recyclerView.apply {
                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = myAdapter
                }
            }
        }
    }
}

//https://github.com/firebase/FirebaseUI-Android/issues/1131