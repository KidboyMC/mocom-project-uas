package com.example.nobarek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.nobarek.data.local.AppDatabase
import com.example.nobarek.data.repository.MovieRepository
import com.example.nobarek.navigation.AppNavigation
import com.example.nobarek.ui.theme.NobaRekTheme
import com.example.nobarek.viewmodel.MovieViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Database & Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = MovieRepository(database.movieDao())
        val viewModelFactory = MovieViewModelFactory(repository)

        enableEdgeToEdge()
        setContent {
            NobaRekTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // 2. Send Factory to AppNavigation
                    AppNavigation(viewModelFactory = viewModelFactory)
                }
            }
        }
    }
}