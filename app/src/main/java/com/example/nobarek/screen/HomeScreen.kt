package com.example.nobarek.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// DATA MODELS
data class MovieItem(
    val id: Int,
    val title: String,
    val rating: Double,
    val posterUrl: String,
    val genres: List<String> = emptyList(),
    val duration: String = "",
    val reviewCount: String = ""
)

// MAIN SCREEN
@Composable
fun HomeScreen(
    featuredMovies: List<MovieItem>,
    popularMovies: List<MovieItem>,
    genreMovies: List<MovieItem>,
    isAdmin: Boolean,
    onSearchTriggered: (String) -> Unit,
    onMovieClick: (Int) -> Unit,
    onViewMoreClick: () -> Unit,
    onAdminClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All") }
    var browseGenre by remember { mutableStateOf("Action") }
    var selectedRating by remember { mutableStateOf("All") }
    
    // Filter popular movies based on selected filters
    val filteredPopularMovies = remember(popularMovies, selectedGenre, selectedRating) {
        popularMovies.filter { movie ->
            val genreMatch = selectedGenre == "All" || movie.genres.contains(selectedGenre)
            val ratingMatch = when (selectedRating) {
                "All" -> true
                "≥7.0" -> movie.rating >= 7.0
                "≥8.0" -> movie.rating >= 8.0
                "≥9.0" -> movie.rating >= 9.0
                else -> true
            }
            genreMatch && ratingMatch
        }
    }
    
    // Filter genre movies based on browse genre
    val filteredGenreMovies = remember(genreMovies, browseGenre) {
        genreMovies.filter { movie ->
            movie.genres.contains(browseGenre)
        }
    }
    Scaffold(
        topBar = { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEAEAEA))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 64.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { newText -> searchQuery = newText },
                        onSearch = {
                            // Panggil navigasi ke halaman Search Result di sini
                            onSearchTriggered(searchQuery)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Admin Button - Only show if user is admin
                    if (isAdmin) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFC107),
                            modifier = Modifier.clickable { onAdminClick() }
                        ) {
                            Text(
                                text = "Admin",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    // Logout Button
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF5350),
                        modifier = Modifier.clickable { onLogoutClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAEAEA))
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // SECTION: FEATURED
            item {
                SectionHeader(title = "Featured", onClick = { onViewMoreClick() })
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(featuredMovies) { movie ->
                        FeaturedMovieCard(movie, onClick = { onMovieClick(movie.id) })
                    }
                }
            }

            // SECTION: POPULAR
            item {
                SectionHeader(title = "Popular", onClick = { onViewMoreClick() }) // Trigger View More
            }
            items(filteredPopularMovies) { movie ->
                PopularMovieItem(movie, onClick = { onMovieClick(movie.id) }) // Trigger Click
            }
            
            // Show message if no movies match the filter
            if (filteredPopularMovies.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies found with selected filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            // SECTION: BROWSE BY GENRE
            item {
                Spacer(modifier = Modifier.height(24.dp))
                GenreHeader(
                    selectedGenre = browseGenre, 
                    onGenreSelected = { newGenre -> browseGenre = newGenre }
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(filteredGenreMovies) { movie ->
                        FeaturedMovieCard(movie, onClick = { onMovieClick(movie.id) }) // Re-use card yang sama dengan featured
                    }
                }
            }
        }
    }
}

// SUB-COMPONENTS
@Composable
fun SectionHeader(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        // View More
        Surface(
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color.Gray),
            color = Color.Transparent,
            modifier = Modifier.clickable { onClick() }
        ) {
            Text(
                text = "View More",
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun GenreHeader(selectedGenre: String, onGenreSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val genres = listOf("Action", "Drama", "Comedy", "Horror", "Sci-Fi", "Fantasy", "Romance", "Thriller")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Browse by Genre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(text = selectedGenre, fontSize = 12.sp, color = Color.Gray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genres.forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(genre) },
                            onClick = {
                                onGenreSelected(genre)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            // View More Button (Reused style)
            Surface(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.Gray),
                color = Color.Transparent
            ) {
                Text(
                    text = "View More",
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// MAIN CARD
@Composable
fun FeaturedMovieCard(movie: MovieItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${movie.rating}/10 IMDb", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

// POPULAR ITEM
@Composable
fun PopularMovieItem(movie: MovieItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Small Poster
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Movie Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${movie.rating} ${movie.reviewCount} IMDb",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                movie.genres.take(3).forEach { genre ->
                    GenreChip(genre)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = movie.duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun GenreChip(text: String) {
    Surface(
        color = Color(0xFFEFE6D5),
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(text = text, fontSize = 10.sp, color = Color.Black)
        }
    }
}
