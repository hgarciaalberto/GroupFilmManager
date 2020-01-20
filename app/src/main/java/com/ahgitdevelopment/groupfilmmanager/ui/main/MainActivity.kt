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
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.databinding.ActivityMainBinding
import com.ahgitdevelopment.groupfilmmanager.ui.moviemanger.MovieManagerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        val mainBinding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }

        if (viewModel.getPrefsDatabaseId().isNotBlank() &&
            viewModel.getPrefsUserId().isNotBlank() &&
            viewModel.getPrefsUserName().isNotBlank()) {
            viewModel.saveUserIntoDatabase()
            launchActivity()
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.createDbAction.observe(this, Observer {
            if (it == true) {
                viewModel.saveUserIntoDatabase()
                launchActivity()
            } else
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show()
        })

        viewModel.joinDbAction.observe(this, Observer {
            if (it == true)
                showJoinDialog(getString(R.string.dialog_title_instertDbCode), joinListener)
            else
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()

        if ((application as BaseApplication).prefs.getUserName().isBlank()) {
            showJoinDialog(getString(R.string.dialog_title_insertUserName), nameListener)
        }
    }

    private fun showJoinDialog(title: String, listener: JoinDialogFragment.MyListener) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev: Fragment? = supportFragmentManager.findFragmentByTag(DIALOG_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val newFragment: JoinDialogFragment? = JoinDialogFragment.newInstance(listener, title)
        newFragment?.isCancelable = false
        newFragment?.show(ft, DIALOG_TAG)
    }

    private fun launchActivity() {
        Intent(this, MovieManagerActivity::class.java).run {
            startActivity(this)
        }
    }

    private val joinListener = object : JoinDialogFragment.OnJoinClickListener {
        override fun onJoinClickListener(databaseId: String) {

            val uiScope = CoroutineScope(Dispatchers.Main)
            uiScope.launch {
                viewModel.existDatabaseId(databaseId).let {
                    if (it?.documents?.size != null && it.documents.size >= 0) {
                        viewModel.setPrefsDatabaseId(databaseId)
                        val userId = viewModel.getUserId(databaseId)
                        viewModel.setPrefsUserId(userId)
                        viewModel.saveUserIntoDatabase()
                        viewModel.updateMoviesWithNewUser(databaseId, userId)
                        launchActivity()
                    } else {
                        Toast.makeText(this@MainActivity, R.string.join_again_error_message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private val nameListener = object : JoinDialogFragment.OnAddNameClickListener {
        override fun onAddNameClickListener(name: String) = viewModel.setPrefsUserName(name)
    }


    companion object {
        private const val DIALOG_TAG = "dialog_tag"
    }
}


//JoinDialogFragment.OnJoinClickListener, JoinDialogFragment.OnAddNameClickListener