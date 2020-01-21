package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MovieRecyclerAdapter(options: FirestoreRecyclerOptions<Movie>) :
    FirestoreRecyclerAdapter<Movie, MovieRecyclerAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, postion: Int, model: Movie) {
        holder.bind(model)
    }

    class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById(R.id.title) as TextView
        private val info1 = itemView.findViewById(R.id.info1) as TextView
        private val info2 = itemView.findViewById(R.id.info2) as TextView
        private val recyclerView = itemView.findViewById(R.id.recyclerView) as RecyclerView

        fun bind(movie: Movie) {
            title.text = movie.name
            info1.text = movie.description1
            info2.text = movie.description2

            val myAdapter = UserRecyclerAdapter(movie.users)

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 3)
                adapter = myAdapter
            }
        }
    }
}
