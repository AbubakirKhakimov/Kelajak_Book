package com.x.a_technologies.kelajak_book.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationController = findNavController(R.id.fragmentContainerView)
        binding.bottomNavigation.setupWithNavController(navigationController)

        findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.homeFragment
                || destination.id == R.id.searchFragment
                || destination.id == R.id.bookmarkFragment
                || destination.id == R.id.profileFragment
                || destination.id == R.id.bookDetailsFragment
                || destination.id == R.id.allReviewsFragment){
                binding.bottomNavigation.visibility = View.VISIBLE
            }else{
                binding.bottomNavigation.visibility = View.GONE
            }
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        Hawk.init(newBase).build();
        super.attachBaseContext(newBase)
    }

}