package com.ahgitdevelopment.groupfilmmanager.ui.moviemanger

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ahgitdevelopment.groupfilmmanager.R
import com.ahgitdevelopment.groupfilmmanager.base.BaseActivity
import com.ahgitdevelopment.groupfilmmanager.base.BaseApplication
import com.ahgitdevelopment.groupfilmmanager.ui.import.ImportMovies
import com.ahgitdevelopment.groupfilmmanager.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MovieManagerActivity : BaseActivity() {

    private val prefs by lazy { (application as BaseApplication).prefs }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_manager)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_movies, R.id.navigation_favorites, R.id.navigation_addMovie))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movie_manager_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.menuImportFile -> {
                Toast.makeText(this, "Import File", Toast.LENGTH_SHORT).show()
                ImportMovies(this).run {
                    import()
                }
            }

            R.id.menuExportFile -> {
                Toast.makeText(this, "Export File", Toast.LENGTH_SHORT).show()
            }

            R.id.menuSettings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }

            R.id.menuSignOut -> {
                Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show()
                prefs.clear()
                launchMainActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchMainActivity() {
        Intent(this, MainActivity::class.java).run {
            startActivity(this)
        }
    }
}
