package com.example.nobarek.navigation

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nobarek.screen.AddEditMovieScreen
import com.example.nobarek.screen.AdminScreen
import com.example.nobarek.screen.BottomNavigationBar
import com.example.nobarek.screen.FavoriteScreen
import com.example.nobarek.screen.GetStartedScreen
import com.example.nobarek.screen.HomeScreen
import com.example.nobarek.screen.LoginScreen
import com.example.nobarek.screen.MovieDetailScreen
import com.example.nobarek.screen.ProfileScreen
import com.example.nobarek.screen.RegisterScreen
import com.example.nobarek.screen.SeriesScreen
import com.example.nobarek.screen.SplashScreen
import com.example.nobarek.screen.UnifiedMovieListScreen
import com.example.nobarek.viewmodel.MovieViewModel
import com.example.nobarek.viewmodel.UserViewModel
import androidx.core.content.edit

// --- ROOT NAVIGATION ---
@Composable
fun AppNavigation(
    movieViewModelFactory: MovieViewModel.MovieViewModelFactory,
    userViewModelFactory: UserViewModel.UserViewModelFactory
) {
    val rootNavController = rememberNavController() // Main NavController
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("nobarek_prefs", Context.MODE_PRIVATE) }

    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val loginError by userViewModel.loginError.collectAsState()

    // Outer NavHost (Splash, Login, & Main App Transition)
    NavHost(
        navController = rootNavController,
        startDestination = "splash_screen"
    ) {
        // SPLASH SCREEN
        composable("splash_screen") {
            SplashScreen {
                val isFirstTime = sharedPrefs.getBoolean("is_first_time", true)
                val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)
                val savedUsername = sharedPrefs.getString("username", "") ?: ""
                val savedRole = sharedPrefs.getString("role", "user") ?: "user"

                if (isFirstTime) {
                    rootNavController.navigate("get_started_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                } else if (isLoggedIn && savedUsername.isNotEmpty()) {
                    userViewModel.forceLogin(savedUsername, savedRole)
                    // Jump to route "main_app"
                    rootNavController.navigate("main_app") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                } else {
                    rootNavController.navigate("login_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            }
        }

        // GET STARTED
        composable("get_started_screen") {
            GetStartedScreen(
                onGetStartedClick = {
                    sharedPrefs.edit { putBoolean("is_first_time", false) }
                    rootNavController.navigate("login_screen") {
                        popUpTo("get_started_screen") { inclusive = true }
                    }
                }
            )
        }

        // LOGIN SCREEN
        composable("login_screen") {
            LoginScreen(
                onLoginClick = { u, p -> userViewModel.login(u, p) },
                onRegisterClick = { rootNavController.navigate("register_screen") },
                errorMessage = loginError
            )
            // Auto move listener if login success
            LaunchedEffect(loggedInUser) {
                if (loggedInUser != null) {
                    sharedPrefs.edit {
                        putBoolean("is_logged_in", true)
                            .putString("username", loggedInUser!!.username)
                            .putString("role", loggedInUser!!.role)
                    }

                    rootNavController.navigate("main_app") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            }
        }

        composable("register_screen") {
            RegisterScreen(
                onRegisterClick = { u, p -> userViewModel.register(u, p, "user") {} },
                onBackToLoginClick = {
                    userViewModel.clearError()
                    rootNavController.popBackStack()
                },
                errorMessage = loginError
            )
        }

        // MAIN APP
        composable("main_app") {
            MainAppScreen(
                rootNavController = rootNavController, // Pass root nav for logout
                movieViewModelFactory = movieViewModelFactory,
                userViewModel = userViewModel
            )
        }
    }
}

// MAIN APP SCREEN
@Composable
fun MainAppScreen(
    rootNavController: NavHostController, // Logout to login screen
    movieViewModelFactory: MovieViewModel.MovieViewModelFactory,
    userViewModel: UserViewModel
) {
    val homeNavController = rememberNavController() // NavController inner tab
    val movieViewModel: MovieViewModel = viewModel(factory = movieViewModelFactory)

    // State Data
    val featuredMovies by movieViewModel.featuredMovies.collectAsState()
    val popularMovies by movieViewModel.popularMovies.collectAsState()
    val searchResults by movieViewModel.searchResults.collectAsState()
    val selectedMovie by movieViewModel.selectedMovie.collectAsState()
    val favoriteMovies by movieViewModel.favoriteMovies.collectAsState()
    val isAdmin by userViewModel.isAdmin.collectAsState()

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("nobarek_prefs", Context.MODE_PRIVATE) }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    // Logic Bottom Bar Show/Hide
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Movie.route,
        BottomNavItem.Series.route,
        BottomNavItem.Favorite.route,
        BottomNavItem.Profile.route,
        "movie_list_screen?query={query}"
    )
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = Color(0xFFEAEAEA),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    isAdmin = isAdmin,
                    navController = homeNavController
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // HOME
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    featuredMovies = featuredMovies,
                    popularMovies = popularMovies,
                    genreMovies = featuredMovies,
                    isAdmin = isAdmin,
                    onSearchTriggered = { query ->
                        movieViewModel.searchMovies(query)
                        homeNavController.navigate("movie_list_screen?query=$query")
                    },
                    onViewMoreClick = {
                        movieViewModel.loadAllMoviesForList()
                        homeNavController.navigate("movie_list_screen?query=")
                    },
                    onMovieClick = { id ->
                        movieViewModel.getMovieDetail(id)
                        homeNavController.navigate("movie_detail_screen/$id?isAdmin=$isAdmin")
                    },
                    onAdminClick = { homeNavController.navigate("admin_screen") },
                    onLogoutClick = {
                        // LOGIC LOGOUT: Panggil rootNavController untuk keluar dari Main App
                        sharedPrefs.edit { clear() } // Hapus semua data prefs
                        userViewModel.logout()

                        rootNavController.navigate("login_screen") {
                            popUpTo("main_app") { inclusive = true }
                        }
                    }
                )
            }

            // MOVIE TAB
            composable(BottomNavItem.Movie.route) {
                LaunchedEffect(Unit) { movieViewModel.loadAllMoviesForList() }
                UnifiedMovieListScreen(
                    initialQuery = "",
                    results = searchResults,
                    totalResults = searchResults.size,
                    onMovieClick = { id ->
                        movieViewModel.getMovieDetail(id)
                        homeNavController.navigate("movie_detail_screen/$id?isAdmin=$isAdmin")
                    },
                    onSearchTriggered = { q ->
                        movieViewModel.searchMovies(q)
                        homeNavController.navigate("movie_list_screen?query=$q")
                    }
                )
            }

            // SERIES TAB
            composable(BottomNavItem.Series.route) {
                SeriesScreen(
                    viewModel = movieViewModel,
                    onSeriesClick = { id ->
                        movieViewModel.getMovieDetail(id)
                        homeNavController.navigate("movie_detail_screen/$id?isAdmin=$isAdmin")
                    }
                )
            }

            // FAVORITE TAB
            composable(BottomNavItem.Favorite.route) {
                FavoriteScreen(
                    viewModel = movieViewModel,
                    onMovieClick = { id ->
                        movieViewModel.getMovieDetail(id)
                        homeNavController.navigate("movie_detail_screen/$id?isAdmin=$isAdmin")
                    }
                )
            }

            // PROFILE TAB
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    userViewModel = userViewModel,
                    onLogoutClick = {
                        // LOGOUT
                        sharedPrefs.edit { clear() }
                        userViewModel.logout()

                        rootNavController.navigate("login_screen") {
                            popUpTo("main_app") { inclusive = true }
                        }
                    }
                )
            }

            // View More / Search
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
                        homeNavController.navigate("movie_detail_screen/$id?isAdmin=$isAdmin")
                    },
                    onSearchTriggered = { q ->
                        movieViewModel.searchMovies(q)
                        homeNavController.navigate("movie_list_screen?query=$q")
                    }
                )
            }

            // Detail
            composable(
                route = "movie_detail_screen/{movieId}?isAdmin={isAdmin}",
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType },
                    navArgument("isAdmin") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStack ->
                val adminMode = backStack.arguments?.getBoolean("isAdmin") ?: false
                val movieId = backStack.arguments?.getInt("movieId") ?: 0
                val isFavorite = favoriteMovies.any { it.id == movieId }
                
                if (selectedMovie != null) {
                    MovieDetailScreen(
                        movie = selectedMovie!!,
                        isAdminMode = adminMode,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            movieViewModel.toggleFavorite(selectedMovie!!.id, !isFavorite)
                        },
                        onEditClick = { homeNavController.navigate("add_edit_movie?movieId=${selectedMovie!!.id}") },
                        onDeleteClick = {
                            movieViewModel.deleteMovie(selectedMovie!!.id)
                            homeNavController.popBackStack()
                        },
                        onBackClick = { homeNavController.popBackStack() }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
            }

            // Admin Add/Edit
            composable(
                route = "add_edit_movie?movieId={movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType; defaultValue = 0 })
            ) {
                val id = it.arguments?.getInt("movieId") ?: 0
                AddEditMovieScreen(
                    navController = homeNavController,
                    viewModel = movieViewModel,
                    movieId = id
                )
            }

            // Admin Dashboard
            composable("admin_screen") {
                val allMovies = (featuredMovies + popularMovies).distinctBy { it.title }
                AdminScreen(
                    movies = allMovies,
                    onBackClick = { homeNavController.popBackStack() },
                    onAddClick = { homeNavController.navigate("add_edit_movie?movieId=0") },
                    onEditClick = { id -> homeNavController.navigate("add_edit_movie?movieId=$id") },
                    onDeleteClick = { id -> movieViewModel.deleteMovie(id) },
                    onMovieClick = { id ->
                        movieViewModel.getMovieDetail(id)
                        homeNavController.navigate("movie_detail_screen/$id?isAdmin=true")
                    }
                )
            }
        }
    }
}

