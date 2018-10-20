package de.gdgnuremberg.mobile.citytours

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(mainNavigationFragment)
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() =
            Navigation.findNavController(this, R.id.mainNavigationFragment).navigateUp()
}
