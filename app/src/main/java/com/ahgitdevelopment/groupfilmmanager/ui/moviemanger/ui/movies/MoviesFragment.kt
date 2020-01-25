package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.data.Movie
import com.ahgitdevelopment.groupfilmmanager.databinding.FragmentMoviesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.movie_list_item.view.*
import java.util.*

class MoviesFragment : Fragment() {

    private val TAG = "MoviesFragment"

    private lateinit var moviesViewModel: MoviesViewModel

    private var mAdapter: MovieRecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        val fragmentBinding: FragmentMoviesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movies, container, false
        )

        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel::class.java)
        fragmentBinding.apply {
            lifecycleOwner = activity
            viewModel = moviesViewModel
        }

        moviesViewModel.getMovies(arguments?.getBoolean(IS_FAVOURITE_FRAGMENT))

        return fragmentBinding.root
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
    }

    override fun onStart() {
        super.onStart()

        moviesViewModel.updateMovies.observe(this, Observer { movies ->
            movies?.let {
                setRecyclerView(it)
                mAdapter?.notifyDataSetChanged()
            }
        })
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

    fun searchFilter(searchText: String) {
        moviesViewModel.searchFilter(searchText.toUpperCase(Locale.getDefault()).trim())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                Toast.makeText(requireActivity(), "Search", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setRecyclerView(movies: List<Movie>) {

        Log.w(TAG, "RECYCLER DONE!!!!!")

        mAdapter = MovieRecyclerAdapter(movies).apply {
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

        private var viewHolderAux: RecyclerView.ViewHolder? = null

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            when (actionState) {
                ACTION_STATE_SWIPE -> {
                    viewHolderAux = viewHolder
                    viewHolder?.itemView?.deleteIcon?.visibility = View.VISIBLE
                }

                ACTION_STATE_IDLE -> viewHolderAux?.itemView?.deleteIcon?.visibility = View.GONE
            }
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//            val dragFlags = UP or DOWN
            val swipeFlags = /*LEFT or*/ RIGHT
            return ItemTouchHelper.Callback.makeMovementFlags(0, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            when (direction) {
                RIGHT -> {
                    (viewHolder as MovieRecyclerAdapter.ViewHolder).movieId.let { movieId ->
                        moviesViewModel.removeMovie(movieId)
                    }
                }
            }
        }
    }

    companion object {
        private const val IS_FAVOURITE_FRAGMENT = "isFavourite"
    }
}


