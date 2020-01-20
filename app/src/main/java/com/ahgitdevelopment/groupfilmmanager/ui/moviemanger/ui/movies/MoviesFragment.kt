package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ahgitdevelopment.groupfilmmanager.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MoviesFragment : Fragment() {

    private lateinit var moviesViewModel: MoviesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)

        moviesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            Toast.makeText(requireActivity(), "Add movie", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.navigation_addMovie)
        }
    }
}