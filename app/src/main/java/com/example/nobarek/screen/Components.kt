package com.example.nobarek.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nobarek.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(50.dp)),
        placeholder = { Text("Search movies...") },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE0E0E0),
            unfocusedContainerColor = Color(0xFFE0E0E0),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@Composable
fun BottomNavigationBar(isAdmin: Boolean = false, navController: NavController) {
    // Admin Filter
    val items = remember(isAdmin) {
        if (isAdmin) {
            listOf(BottomNavItem.Home, BottomNavItem.Movie, BottomNavItem.Series)
        } else {
            listOf(BottomNavItem.Home, BottomNavItem.Movie, BottomNavItem.Series, BottomNavItem.Favorite, BottomNavItem.Profile)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Use startsWith so that if there are parameters, they are still detected.
    val isMovieTabSelected = currentRoute == BottomNavItem.Movie.route ||
            currentRoute?.startsWith("movie_list_screen") == true

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            // Check if this item is a Movie, if yes use custom logic, if not use standard
            val isSelected = if (item == BottomNavItem.Movie) isMovieTabSelected else currentRoute == item.route

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color(0xFFFFC107),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}