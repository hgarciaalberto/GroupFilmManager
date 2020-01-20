package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.ui.addmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.databinding.FragmentAddmovieBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddMovieFragment : Fragment() {

    private lateinit var addMovieViewModel: AddMovieViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentAddmovieBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_addmovie, container, false)

        addMovieViewModel = ViewModelProviders.of(this).get(AddMovieViewModel::class.java)

        binding.apply {
            lifecycleOwner = this@AddMovieFragment.viewLifecycleOwner
            viewModel = this@AddMovieFragment.addMovieViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<FloatingActionButton>(R.id.fabSaveMovie).setOnClickListener {
            addMovieViewModel.saveMovieIntoDatabase()
        }

        view.findViewById<FloatingActionButton>(R.id.fabCancelMovie).setOnClickListener {
            findNavController().popBackStack()
        }

        addMovieViewModel.isMovieSaved.observe(this, Observer {
            when (it) {
                true -> {
                    Toast.makeText(requireContext(), "movie saved: $it", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                false -> {
                    Toast.makeText(requireContext(), R.string.error_message_empty_value, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}