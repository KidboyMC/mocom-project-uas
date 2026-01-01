package com.example.nobarek.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nobarek.screen.*
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

    val movieViewModel: MovieViewModel = viewModel(factory = movieViewModelFactory)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)

    val featuredMovies by movieViewModel.featuredMovies.collectAsState()
    val popularMovies by movieViewModel.popularMovies.collectAsState()
    val searchResults by movieViewModel.searchResults.collectAsState()
    val selectedMovie by movieViewModel.selectedMovie.collectAsState()

    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val isAdmin by userViewModel.isAdmin.collectAsState()
    val loginError by userViewModel.loginError.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {

        // SPLASH
        composable("splash_screen") {
            SplashScreen {
                navController.navigate("login_screen") {
                    popUpTo("splash_screen") { inclusive = true }
                }
            }
        }

        // LOGIN
        composable("login_screen") {
            LoginScreen(
                onLoginClick = { u, p -> userViewModel.login(u, p) },
                onRegisterClick = { navController.navigate("register_screen") },
                errorMessage = loginError
            )

            LaunchedEffect(loggedInUser) {
                if (loggedInUser != null) {
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            }
        }

        // REGISTER
        composable("register_screen") {
            RegisterScreen(
                onRegisterClick = { u, p ->
                    userViewModel.register(u, p, "user") {}
                },
                onBackToLoginClick = {
                    userViewModel.clearError()
                    navController.popBackStack()
                },
                errorMessage = loginError
            )
        }

        // HOME
        composable("home_screen") {
            HomeScreen(
                featuredMovies = featuredMovies,
                popularMovies = popularMovies,
                genreMovies = featuredMovies,
                isAdmin = isAdmin,
                onSearchTriggered = { query ->
                    movieViewModel.searchMovies(query)
                    navController.navigate("movie_list_screen?query=$query")
                },
                onViewMoreClick = {
                    movieViewModel.loadAllMoviesForList()
                    navController.navigate("movie_list_screen?query=")
                },
                onMovieClick = { id ->
                    movieViewModel.getMovieDetail(id)
                    navController.navigate("movie_detail_screen/$id?isAdmin=false")
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

        // MOVIE LIST
        composable(
            route = "movie_list_screen?query={query}",
            arguments = listOf(navArgument("query") { defaultValue = "" })
        ) { backStack ->
            val query = backStack.arguments?.getString("query") ?: ""

            LaunchedEffect(query) {
                if (query.isNotEmpty()) movieViewModel.searchMovies(query)
                else movieViewModel.loadAllMoviesForList()
            }

            UnifiedMovieListScreen(
                initialQuery = query,
                results = searchResults,
                totalResults = searchResults.size,
                onMovieClick = { id ->
                    movieViewModel.getMovieDetail(id)
                    navController.navigate("movie_detail_screen/$id?isAdmin=false")
                },
                onSearchTriggered = { q ->
                    movieViewModel.searchMovies(q)
                    navController.navigate("movie_list_screen?query=$q")
                }
            )
        }

        // DETAIL
        composable(
            route = "movie_detail_screen/{movieId}?isAdmin={isAdmin}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType },
                navArgument("isAdmin") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStack ->
            val adminMode = backStack.arguments?.getBoolean("isAdmin") ?: false

            if (selectedMovie != null) {
                MovieDetailScreen(
                    movie = selectedMovie!!,
                    isAdminMode = adminMode,
                    onEditClick = {
                        navController.navigate("add_edit_movie?movieId=${selectedMovie!!.id}")
                    },
                    onDeleteClick = {
                        movieViewModel.deleteMovie(selectedMovie!!.id)
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // ADD / EDIT
        composable(
            route = "add_edit_movie?movieId={movieId}",
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            val id = it.arguments?.getInt("movieId") ?: 0
            AddEditMovieScreen(
                navController = navController,
                viewModel = movieViewModel,
                movieId = id
            )
        }

        // ADMIN
        composable("admin_screen") {
            val allMovies = (featuredMovies + popularMovies).distinctBy { it.id }

            AdminScreen(
                movies = allMovies,
                onBackClick = { navController.popBackStack() },
                onAddClick = {
                    navController.navigate("add_edit_movie?movieId=0")
                },
                onEditClick = { id ->
                    navController.navigate("add_edit_movie?movieId=$id")
                },
                onDeleteClick = { id ->
                    movieViewModel.deleteMovie(id)
                },
                onMovieClick = { id ->
                    movieViewModel.getMovieDetail(id)
                    navController.navigate("movie_detail_screen/$id?isAdmin=true")
                }
            )
        }
    }
}
