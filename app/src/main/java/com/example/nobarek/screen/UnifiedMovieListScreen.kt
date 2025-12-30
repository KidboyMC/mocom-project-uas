package com.example.nobarek.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// DATA MODELS
data class MovieResult(
    val id: Int,
    val title: String,
    val rating: Double,
    val posterUrl: String
)

// MAIN SCREEN
@Composable
fun UnifiedMovieListScreen(
    initialQuery: String = "", // Default is empty for "Movie List" mode
    results: List<MovieResult>,
    totalResults: Int,
    onMovieClick: (Int) -> Unit,
    onSearchTriggered: (String) -> Unit // Callback when enter is pressed
) {
    // Search State
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val focusManager = LocalFocusManager.current
    
    // Sort and Filter State
    var selectedSortOption by remember { mutableStateOf("Name (A-Z)") }
    var selectedGenreFilter by remember { mutableStateOf("All") }
    
    // Apply sorting and filtering
    val sortedAndFilteredResults = remember(results, selectedSortOption, selectedGenreFilter) {
        var filtered = results
        
        // Apply genre filter (Note: MovieResult doesn't have genres, so this is a placeholder)
        // In real implementation, you'd need to add genres to MovieResult
        // For now, we'll just apply sorting
        
        // Apply sorting
        when (selectedSortOption) {
            "Name (A-Z)" -> filtered.sortedBy { it.title }
            "Name (Z-A)" -> filtered.sortedByDescending { it.title }
            "Rating (High-Low)" -> filtered.sortedByDescending { it.rating }
            "Rating (Low-High)" -> filtered.sortedBy { it.rating }
            else -> filtered
        }
    }

    // Dynamic Logic Title:
    // If searchQuery is not empty -> Search Mode
    // If searchQuery is empty -> Movie List Mode (View All)
    val isSearchMode = searchQuery.isNotEmpty()
    val pageTitle = if (isSearchMode) "Search Results: $searchQuery" else "Movie List"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE))
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .navigationBarsPadding()
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                focusManager.clearFocus()
                onSearchTriggered(searchQuery)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title & Sort Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Title
            Text(
                text = pageTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Filter/Sort Row (Only appears if NOT in search mode)
            if (!isSearchMode) {
                Spacer(modifier = Modifier.height(16.dp))
                SortFilterRow(
                    selectedSortOption = selectedSortOption,
                    selectedGenreFilter = selectedGenreFilter,
                    onSortOptionSelected = { selectedSortOption = it },
                    onGenreFilterSelected = { selectedGenreFilter = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid Content
        Box(modifier = Modifier.weight(1f)) {
            if (results.isEmpty()) {
                Text(
                    text = if (isSearchMode) "No results found for \"$searchQuery\"" else "No movies available.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(sortedAndFilteredResults) { movie ->
                        MovieGridItem(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }
            }
        }

        // Pagination Footer
        val itemsPerPage = 6
        val totalPages = if (totalResults > 0) (totalResults + itemsPerPage - 1) / itemsPerPage else 1

        PaginationFooter(
            currentPage = 1,
            totalPages = totalPages,
            currentCount = results.size,
            totalCount = totalResults
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// SUB-COMPONENTS

@Composable
fun SortFilterRow(
    selectedSortOption: String,
    selectedGenreFilter: String,
    onSortOptionSelected: (String) -> Unit,
    onGenreFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Sort By:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(8.dp))

        SortChip(
            text = selectedSortOption,
            options = listOf("Name (A-Z)", "Name (Z-A)", "Rating (High-Low)", "Rating (Low-High)"),
            onOptionSelected = onSortOptionSelected
        )
        Spacer(modifier = Modifier.width(8.dp))
        SortChip(
            text = if (selectedGenreFilter == "All") "All Genre" else selectedGenreFilter,
            options = listOf("All", "Action", "Drama", "Comedy", "Horror", "Sci-Fi", "Fantasy", "Romance", "Thriller"),
            onOptionSelected = onGenreFilterSelected
        )
    }
}

@Composable
fun SortChip(
    text: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(text = text, fontSize = 12.sp, color = Color.Gray)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = option,
                            fontWeight = if (option == text || (text == "All Genre" && option == "All")) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MovieGridItem(movie: MovieResult, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
        , horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Rating Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${movie.rating}/10 IMDb",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PaginationFooter(currentPage: Int, totalPages: Int, currentCount: Int, totalCount: Int) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Showing $currentCount of $totalCount", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PageBox(number = currentPage, isSelected = true)
            if (totalPages > 1) PageBox(number = currentPage + 1, isSelected = false)
            if (totalPages > 2) PageBox(number = currentPage + 2, isSelected = false)
            if (totalPages > 3) Text("...", modifier = Modifier.align(Alignment.Bottom))
            if (totalPages > 3) PageBox(number = totalPages, isSelected = false)
        }
    }
}

@Composable
fun PageBox(number: Int, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(if (isSelected) Color(0xFFFFC107) else Color.Transparent, shape = RoundedCornerShape(4.dp))
            .clickable { /* Handle Page Change */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}