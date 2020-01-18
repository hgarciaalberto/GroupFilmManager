package com.ahgitdevelopment.groupfilmmanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.base.BaseActivity
import com.ahgitdevelopment.groupfilmmanager.databinding.ActivityMainBinding
import com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.MovieManagerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity(), JoinDialogFragment.OnJoinClickListener {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(
            MainActivityViewModel::class.java)
        val mainBinding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }

        if (viewModel.getPrefsDatabaseId().isNotBlank()) {
            launchActivity()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.createDbAction.observe(this, Observer {
            if (it == true)
                launchActivity()
            else
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show()
        })

        viewModel.joinDbAction.observe(this, Observer {
            if (it == true)
                showJoinDialog()
            else
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun showJoinDialog() {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev: Fragment? = supportFragmentManager.findFragmentByTag(
            DIALOG_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val newFragment: JoinDialogFragment? = JoinDialogFragment.newInstance(this)
        newFragment?.show(ft,
            DIALOG_TAG)
    }

    private fun launchActivity() {
        Intent(this, MovieManagerActivity::class.java).run {
            startActivity(this)
        }
    }

    override fun onJoinClickListener(databaseId: String) {

        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            val document = viewModel.existDatabaseId(databaseId)
            if (document?.exists() == true) {
                launchActivity()
            } else {
                Toast.makeText(this@MainActivity, R.string.join_again_error_message, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val DIALOG_TAG = "dialog_tag"
    }
}


