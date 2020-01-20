package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ahgitdevelopment.groupfilmmanager.R

class FavouriteMoviesFragment : Fragment() {

    private lateinit var favouritesMoviesViewModel: FavouritesMoviesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        favouritesMoviesViewModel = ViewModelProviders.of(this).get(FavouritesMoviesViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val textView: TextView = root.findViewById(R.id.text_dashboard)

        favouritesMoviesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}