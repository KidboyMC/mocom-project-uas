package com.example.nobarek.navigation

import com.example.nobarek.screen.UnifiedMovieListScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nobarek.screen.AddEditMovieScreen
import com.example.nobarek.screen.AdminScreen
import com.example.nobarek.screen.HomeScreen
import com.example.nobarek.screen.LoginScreen
import com.example.nobarek.screen.MovieDetailScreen
import com.example.nobarek.screen.RegisterScreen
import com.example.nobarek.screen.SplashScreen
import com.example.nobarek.viewmodel.MovieViewModel
import com.example.nobarek.viewmodel.MovieViewModelFactory
import com.example.nobarek.viewmodel.UserViewModel
import com.example.nobarek.viewmodel.UserViewModelFactory

@Composable
fun AppNavigation(
    movieViewModelFactory: MovieViewModelFactory,
    userViewModelFactory: UserViewModelFactory
) {
    val navController = rememberNavController()
    
    // Initialize ViewModels
    val movieViewModel: MovieViewModel = viewModel(factory = movieViewModelFactory)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)

    // Collect State from MovieViewModel
    val featuredMovies by movieViewModel.featuredMovies.collectAsState()
    val popularMovies by movieViewModel.popularMovies.collectAsState()
    val searchResults by movieViewModel.searchResults.collectAsState()
    val selectedMovie by movieViewModel.selectedMovie.collectAsState()
    
    // Collect State from UserViewModel
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val isAdmin by userViewModel.isAdmin.collectAsState()
    val loginError by userViewModel.loginError.collectAsState()

    NavHost(navController = navController, startDestination = "splash_screen") {

        composable("splash_screen") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("login_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            )
        }
        
        
        composable("login_screen") {
            LoginScreen(
                onLoginClick = { username, password ->
                    userViewModel.login(username, password)
                },
                onRegisterClick = {
                    navController.navigate("register_screen")
                },
                errorMessage = loginError
            )
            
            // Navigate to home when logged in successfully
            LaunchedEffect(loggedInUser) {
                if (loggedInUser != null) {
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            }
        }
        
        composable("register_screen") {
            var registrationSuccess by remember { mutableStateOf(false) }
            
            RegisterScreen(
                onRegisterClick = { username, password ->
                    userViewModel.register(username, password, "user") {
                        // On success callback
                        registrationSuccess = true
                    }
                },
                onBackToLoginClick = {
                    userViewModel.clearError()
                    navController.popBackStack()
                },
                errorMessage = loginError
            )
            
            // Navigate to login after successful registration
            LaunchedEffect(registrationSuccess) {
                if (registrationSuccess) {
                    userViewModel.clearError()
                    navController.popBackStack()
                }
            }
        }

        composable("home_screen") {
            HomeScreen(
                featuredMovies = featuredMovies,
                popularMovies = popularMovies,
                genreMovies = featuredMovies,
                isAdmin = isAdmin,
                onSearchTriggered = { query ->
                    if (query.isNotEmpty()) {
                        movieViewModel.searchMovies(query)
                        navController.navigate("movie_list_screen?query=$query")
                    }
                },
                onViewMoreClick = {
                    movieViewModel.loadAllMoviesForList()
                    navController.navigate("movie_list_screen?query=")
                },
                onMovieClick = { movieId ->
                    movieViewModel.getMovieDetail(movieId)
                    navController.navigate("movie_detail_screen/$movieId?isAdmin=false")
                },
                onAdminClick = {
                    navController.navigate("admin_screen")
                },
                onLogoutClick = {
                    userViewModel.logout()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
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
                if(query.isNotEmpty()) movieViewModel.searchMovies(query)
                else movieViewModel.loadAllMoviesForList()
            }

            UnifiedMovieListScreen(
                initialQuery = query,
                results = searchResults,
                totalResults = searchResults.size,
                onMovieClick = { movieId ->
                    movieViewModel.getMovieDetail(movieId)
                    navController.navigate("movie_detail_screen/$movieId?isAdmin=false")
                },
                onSearchTriggered = { newQuery ->
                    movieViewModel.searchMovies(newQuery)
                    navController.navigate("movie_list_screen?query=$newQuery")
                }
            )
        }

        composable(
            route = "movie_detail_screen/{movieId}?isAdmin={isAdmin}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType },
                navArgument("isAdmin") { 
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
            if (selectedMovie != null) {
                MovieDetailScreen(
                    movie = selectedMovie!!,
                    isAdminMode = isAdmin,
                    onEditClick = {
                        // Navigate to Edit
                        navController.navigate("add_edit_movie?movieId=${selectedMovie!!.id}")
                    },
                    onDeleteClick = {
                        // Delete Data from DB
                        movieViewModel.deleteMovie(selectedMovie!!.id)

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
                viewModel = movieViewModel,
                movieId = movieId
            )
        }

        // Admin Screen Route
        composable("admin_screen") {
            // Combine all movies untuk admin
            val allMovies = featuredMovies + popularMovies
            val distinctMovies = allMovies.distinctBy { it.title }
            
            AdminScreen(
                movies = distinctMovies,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddClick = {
                    navController.navigate("add_edit_movie?movieId=0")
                },
                onEditClick = { movieId ->
                    navController.navigate("add_edit_movie?movieId=$movieId")
                },
                onDeleteClick = { movieId ->
                    movieViewModel.deleteMovie(movieId)
                },
                onMovieClick = { movieId ->
                    movieViewModel.getMovieDetail(movieId)
                    navController.navigate("movie_detail_screen/$movieId?isAdmin=true")
                }
            )
        }
    }
}