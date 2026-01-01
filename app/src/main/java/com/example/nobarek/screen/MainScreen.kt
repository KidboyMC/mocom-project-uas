package com.example.nobarek.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nobarek.viewmodel.MovieViewModel

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Movie : BottomNavItem("movie", "Movie", Icons.Default.Movie)
    object Series : BottomNavItem("series", "Series", Icons.Default.Tv)
    object Favorite : BottomNavItem("favorite", "Favorite", Icons.Default.Favorite)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val yellowThemeColor = Color(0xFFFFC107)
    val darkColor = Color(0xFF1F1F1F)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = yellowThemeColor,
                contentColor = darkColor
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Movie,
                    BottomNavItem.Series,
                    BottomNavItem.Favorite,
                    BottomNavItem.Profile
                )

                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = {
                            if (isSelected) Text(text = item.title, fontWeight = FontWeight.Bold)
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = yellowThemeColor,
                            selectedTextColor = darkColor,
                            indicatorColor = darkColor,
                            unselectedIconColor = darkColor.copy(alpha = 0.6f),
                            unselectedTextColor = darkColor.copy(alpha = 0.6f)
                        ),
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. HOME
            composable(BottomNavItem.Home.route) {
                val featuredMovies by viewModel.featuredMovies.collectAsState()
                val popularMovies by viewModel.popularMovies.collectAsState()

                HomeScreen(
                    featuredMovies = featuredMovies,
                    popularMovies = popularMovies,
                    genreMovies = featuredMovies,
                    onSearchTriggered = onSearchTriggered,
                    onMovieClick = onMovieClick,
                    onViewMoreClick = {},
                    onAddClick = onAddClick
                )
            }

            // 2. MOVIE
            composable(BottomNavItem.Movie.route) {
                // Panggil dengan parameter yang dibutuhkan
                MovieScreen(
                    viewModel = viewModel,
                    onMovieClick = onMovieClick
                )
            }

            // 3. SERIES
            composable(BottomNavItem.Series.route) {
                SeriesScreen(
                    viewModel = viewModel,
                    onSeriesClick = onMovieClick
                )
            }

            // 4. FAVORITE
            composable(BottomNavItem.Favorite.route) {
                FavoriteScreen(
                    viewModel = viewModel,
                    onMovieClick = onMovieClick
                )
            }

            // 5. PROFILE
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}