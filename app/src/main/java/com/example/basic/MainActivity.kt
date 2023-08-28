package com.example.basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.basic.databinding.ActivityMainBinding
import com.example.basic.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val TAG: String = this.javaClass.simpleName
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationGraph()

    }

    private fun setNavigationGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.onbording_nav)
        navGraph.setStartDestination(
            if (PreferenceManager.isOnBoardingCompleted) {

                if(FirebaseAuth.getInstance().currentUser !=null){
                    R.id.mainScreenFragment
                }else{
                    R.id.loggedin
                }
            } else {
                R.id.onBoardingViewpagerFragment
            }
        )
        navController.graph = navGraph
    }
}