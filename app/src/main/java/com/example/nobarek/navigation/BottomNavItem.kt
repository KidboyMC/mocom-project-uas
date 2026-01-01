package com.example.nobarek.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home_screen", "Home", Icons.Default.Home)
    object Movie : BottomNavItem("movie_tab", "Movie", Icons.Default.Movie)
    object Series : BottomNavItem("series_tab", "Series", Icons.Default.Tv)
    object Favorite : BottomNavItem("favorite_tab", "Favorite", Icons.Default.Favorite)
    object Profile : BottomNavItem("profile_tab", "Profile", Icons.Default.Person)
}