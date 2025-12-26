package com.example.nobarek.navigation

import MovieResult
import UnifiedMovieListScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nobarek.data.MovieData
import com.example.nobarek.data.getDummyMovieDetail
import com.example.nobarek.screen.HomeScreen
import com.example.nobarek.screen.MovieDetailScreen
import com.example.nobarek.screen.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        // 1. SPLASH SCREEN
        composable("splash_screen") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("home_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            )
        }

        // 2. HOME SCREEN
        composable("home_screen") {
            HomeScreen(
                featuredMovies = MovieData.featuredMovies,
                popularMovies = MovieData.popularMovies,
                genreMovies = MovieData.featuredMovies,
                onSearchTriggered = { query ->
                    // Navigasi ke Movie List dengan Query Search
                    if (query.isNotEmpty()) {
                        navController.navigate("movie_list_screen?query=$query")
                    }
                },
                onViewMoreClick = {
                    // Navigasi ke Movie List tanpa query (Mode View All)
                    navController.navigate("movie_list_screen?query=")
                },
                onMovieClick = { movieId ->
                    // Navigasi ke Detail Movie
                    navController.navigate("movie_detail_screen/$movieId")
                }
            )
        }

        // 3. MOVIE LIST SCREEN (Search Result / View More)
        // Menerima parameter optional 'query'
        composable(
            route = "movie_list_screen?query={query}",
            arguments = listOf(navArgument("query") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""

            // Konversi data dummy ke format MovieResult untuk list
            val allMovies = (MovieData.featuredMovies + MovieData.popularMovies)
                .distinctBy { it.title } // <--- TAMBAHKAN INI (Hapus duplikat berdasarkan judul)
                .map {
                    MovieResult(it.id, it.title, it.rating, it.posterUrl)
                }

            // Filter sederhana jika ada query search
            val filteredResults = if (query.isNotEmpty()) {
                allMovies.filter { it.title.contains(query, ignoreCase = true) }
            } else {
                allMovies
            }

            UnifiedMovieListScreen(
                initialQuery = query,
                results = filteredResults,
                totalResults = filteredResults.size,
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail_screen/$movieId")
                },
                onSearchTriggered = { newQuery ->
                    // Jika search lagi di dalam halaman ini, update route
                    navController.navigate("movie_list_screen?query=$newQuery") {
                        popUpTo("movie_list_screen?query={query}") { inclusive = true }
                    }
                }
            )
        }

        // 4. MOVIE DETAIL SCREEN
        // Menerima parameter 'movieId'
        composable(
            route = "movie_detail_screen/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0

            // Cari data movie berdasarkan ID.
            // Di real app, kamu ambil dari Room/API. Di sini kita generate dummy.
            val selectedMovieTitle = (MovieData.featuredMovies + MovieData.popularMovies).find { it.id == movieId }?.title ?: "Unknown"
            val selectedPoster = (MovieData.featuredMovies + MovieData.popularMovies).find { it.id == movieId }?.posterUrl ?: ""

            val movieDetail = getDummyMovieDetail(movieId, selectedMovieTitle, selectedPoster)

            MovieDetailScreen(movie = movieDetail)
        }
    }
}