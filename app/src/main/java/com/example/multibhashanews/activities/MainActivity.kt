package com.example.multibhashanews.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.multibhashanews.R
import com.example.multibhashanews.database.ArticleDatabase
import com.example.multibhashanews.databinding.ActivityMainBinding
import com.example.multibhashanews.model.UserInfo
import com.example.multibhashanews.repository.NewsRepository
import com.example.multibhashanews.viewModel.NewsViewModel
import com.example.multibhashanews.viewModel.NewsViewModelProviderFactory

class MainActivity : BaseClass() {
    private lateinit var binding: ActivityMainBinding
    lateinit var newsViewModel: NewsViewModel
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("CommitPrefEdits")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        newsViewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment =supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // ADD THIS LISTENER TO HIDE/SHOW THE TOOLBAR
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.articleFragment -> {
                    binding.mainToolbar.visibility = View.GONE
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.mainToolbar.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.headLinesFragment,
                R.id.categoryFragment,
                R.id.favouritesFragment,
                R.id.searchFragment,
                R.id.aboutFragment
            )
        )
        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)

        var data = intent.getParcelableExtra("userInfo",UserInfo::class.java)
        var email = data?.email?.toString()

        if (email != null) {
            // Fresh login: Save this email to SharedPreferences for next time
            val prefs = getSharedPreferences("emailData", Context.MODE_PRIVATE)
            prefs.edit().putString("email", email).apply()
            Log.d("MainActivity", "Saved new login email: $email")
        } else {
            // Not a fresh login: Load the email from the last session
            val prefs = getSharedPreferences("emailData", Context.MODE_PRIVATE)
            email = prefs.getString("email", null) // Get saved email
            Log.d("MainActivity", "Loaded saved email: $email")
        }
        email?.let {
            sharedViewModel.userEmail.value = it
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        // This ensures the back arrow in the Toolbar works correctly
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}