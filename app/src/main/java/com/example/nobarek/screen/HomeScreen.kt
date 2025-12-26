package com.example.nobarek.screen

import SearchBar
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
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
    onSearchTriggered: (String) -> Unit,
    onMovieClick: (Int) -> Unit,
    onViewMoreClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    Scaffold(
        topBar = { Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEAEAEA))
                .padding(start = 16.dp, end = 16.dp, top = 64.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { newText -> searchQuery = newText },
                    onSearch = {
                        // Panggil navigasi ke halaman Search Result di sini
                        onSearchTriggered(searchQuery)
                    }
                )
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
            items(popularMovies) { movie ->
                PopularMovieItem(movie, onClick = { onMovieClick(movie.id) }) // Trigger Click
            }

            // SECTION: BROWSE BY GENRE
            item {
                Spacer(modifier = Modifier.height(24.dp))
                GenreHeader(selectedGenre = "Action", onGenreClick = {})
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(genreMovies) { movie ->
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
fun GenreHeader(selectedGenre: String, onGenreClick: () -> Unit) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = selectedGenre, fontSize = 12.sp, color = Color.Gray)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
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