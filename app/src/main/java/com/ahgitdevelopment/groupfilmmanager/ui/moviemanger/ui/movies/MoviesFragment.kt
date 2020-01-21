package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.firebase.FirestoreRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesFragment : Fragment() {

    private lateinit var moviesViewModel: MoviesViewModel

    private val firestoreRepository by lazy { FirestoreRepository() }


    private lateinit var mAdapter: MovieRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            val databaseId = (activity?.application as BaseApplication).prefs.getDatabaseId()
            firestoreRepository.getAllMovies(databaseId).let {
                val options = FirestoreRecyclerOptions.Builder<Movie>()
                    .setLifecycleOwner(activity)
                    .setQuery(it.query, Movie::class.java)
                    .build()

                mAdapter = MovieRecyclerAdapter(options)

                view.findViewById<RecyclerView>(R.id.recyclerView).apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = mAdapter
                }
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            Toast.makeText(requireActivity(), "Movie added", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_addMovie)
        }
    }
}