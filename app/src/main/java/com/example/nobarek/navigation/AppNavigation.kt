package com.example.nobarek.navigation

import com.example.nobarek.screen.UnifiedMovieListScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nobarek.screen.AddEditMovieScreen
import com.example.nobarek.screen.HomeScreen
import com.example.nobarek.screen.MovieDetailScreen
import com.example.nobarek.screen.SplashScreen
import com.example.nobarek.viewmodel.MovieViewModel
import com.example.nobarek.viewmodel.MovieViewModelFactory

@Composable
fun AppNavigation(viewModelFactory: MovieViewModelFactory) {
    val navController = rememberNavController()
    // Initialize ViewModel
    val viewModel: MovieViewModel = viewModel(factory = viewModelFactory)

    // Collect State from ViewModel
    val featuredMovies by viewModel.featuredMovies.collectAsState()
    val popularMovies by viewModel.popularMovies.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedMovie by viewModel.selectedMovie.collectAsState()

    NavHost(navController = navController, startDestination = "splash_screen") {

        composable("splash_screen") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("home_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            )
        }

        composable("home_screen") {
            HomeScreen(
                featuredMovies = featuredMovies,
                popularMovies = popularMovies,
                genreMovies = featuredMovies,
                onSearchTriggered = { query ->
                    if (query.isNotEmpty()) {
                        viewModel.searchMovies(query)
                        navController.navigate("movie_list_screen?query=$query")
                    }
                },
                onViewMoreClick = {
                    viewModel.loadAllMoviesForList()
                    navController.navigate("movie_list_screen?query=")
                },
                onMovieClick = { movieId ->
                    viewModel.getMovieDetail(movieId)
                    navController.navigate("movie_detail_screen/$movieId")
                },

                onAddClick = {
                    navController.navigate("add_edit_movie?movieId=0")
                }
            )
        }

        composable(
            route = "movie_list_screen?query={query}",
            arguments = listOf(navArgument("query") { defaultValue = "" })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""

            // Trigger search if query changed
            LaunchedEffect(query) {
                if(query.isNotEmpty()) viewModel.searchMovies(query)
                else viewModel.loadAllMoviesForList()
            }

            UnifiedMovieListScreen(
                initialQuery = query,
                results = searchResults,
                totalResults = searchResults.size,
                onMovieClick = { movieId ->
                    viewModel.getMovieDetail(movieId)
                    navController.navigate("movie_detail_screen/$movieId")
                },
                onSearchTriggered = { newQuery ->
                    viewModel.searchMovies(newQuery)
                    navController.navigate("movie_list_screen?query=$newQuery")
                }
            )
        }

        composable(
            route = "movie_detail_screen/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) {
            if (selectedMovie != null) {
                MovieDetailScreen(
                    movie = selectedMovie!!,
                    onEditClick = {
                        // Navigate to Edit
                        navController.navigate("add_edit_movie?movieId=${selectedMovie!!.id}")
                    },
                    onDeleteClick = {
                        // Delete Data from DB
                        viewModel.deleteMovie(selectedMovie!!.id)

                        // Return to Home
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            } else {
                // LoadingSpinner
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            route = "add_edit_movie?movieId={movieId}",
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            AddEditMovieScreen(
                navController = navController,
                viewModel = viewModel,
                movieId = movieId
            )
        }
    }
}