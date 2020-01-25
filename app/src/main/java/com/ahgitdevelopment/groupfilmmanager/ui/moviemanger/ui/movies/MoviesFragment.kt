package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.movie_list_item.view.*
import java.util.*

class MoviesFragment : Fragment() {

    private val TAG = "MoiesFragment"

    private lateinit var moviesViewModel: MoviesViewModel

    private var mAdapter: MovieRecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (arguments?.getBoolean(IS_FAVOURITE_FRAGMENT)) {
            true -> view.findViewById<FloatingActionButton>(R.id.fab).visibility = View.GONE
            else -> view.findViewById<FloatingActionButton>(R.id.fab).visibility = View.VISIBLE
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            findNavController().navigate(R.id.navigation_addMovie)
        }

        moviesViewModel.getMovies(arguments?.getBoolean(IS_FAVOURITE_FRAGMENT))
    }

    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()

        moviesViewModel.updateMovies.observe(this, androidx.lifecycle.Observer { query ->

            try {
                if (query != null) {
                    setRecyclerView(query)
                } else {
                    mAdapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Known issue deleting first element", e)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        //Don't know why I cannot access search item by its Id or name...
        menu.getItem(0).isVisible = !(arguments?.getBoolean(IS_FAVOURITE_FRAGMENT) ?: false)

        // Setup Search
        (menu.getItem(0).actionView as SearchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    view?.let { searchFilter(query ?: "") }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    view?.let { searchFilter(newText ?: "") }
                    return false
                }
            })


        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                Toast.makeText(requireActivity(), "Search", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun searchFilter(searchText: String) =
        moviesViewModel.searchFilter(searchText.toUpperCase(Locale.getDefault()).trim())


    private fun setRecyclerView(query: Query) {
        val options = FirestoreRecyclerOptions.Builder<Movie>()
            .setLifecycleOwner(activity)
            .setQuery(query, Movie::class.java)
            .build()

        mAdapter = MovieRecyclerAdapter(options).apply {
            startListening()
            setHasStableIds(true)
        }

        recyclerView.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
        }
    }

    private val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, RIGHT) {

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            when (actionState) {
                ACTION_STATE_SWIPE -> viewHolder?.itemView?.deleteIcon?.visibility = View.VISIBLE
            }
        }

//        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//            val dragFlags = UP or DOWN
//            val swipeFlags = /*LEFT or*/ RIGHT
//            return Callback.makeMovementFlags(0, swipeFlags)
//        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            when (direction) {
                RIGHT -> {
                    (viewHolder as MovieRecyclerAdapter.ViewHolder).movieId.let { movieId ->
                        moviesViewModel.removeMovie(movieId)
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    companion object {
        private const val IS_FAVOURITE_FRAGMENT = "isFavourite"
    }
}


