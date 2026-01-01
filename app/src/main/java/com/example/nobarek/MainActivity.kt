package com.example.nobarek

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.nobarek.data.local.AppDatabase
import com.example.nobarek.data.repository.MovieRepository
import com.example.nobarek.data.repository.UserRepository
import com.example.nobarek.navigation.AppNavigation
import com.example.nobarek.ui.theme.NobaRekTheme
import com.example.nobarek.viewmodel.MovieViewModelFactory
import com.example.nobarek.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Database & Repositories
        val database = AppDatabase.getDatabase(applicationContext)
        val movieRepository = MovieRepository(database.movieDao())
        val userRepository = UserRepository(database.userDao())
        
        // 2. Create ViewModelFactories
        val movieViewModelFactory = MovieViewModelFactory(movieRepository)
        val userViewModelFactory = UserViewModelFactory(userRepository)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        setContent {
            NobaRekTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // 3. Send Factories to AppNavigation
                    AppNavigation(
                        movieViewModelFactory = movieViewModelFactory,
                        userViewModelFactory = userViewModelFactory
                    )
                }
            }
        }
    }
}