package com.example.nobarek.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// DATA MODELS
data class Movie(
    val id: Int = 0,
    val title: String,
    val genres: List<String>,
    val rating: Double,
    val duration: String,
    val description: String,
    val posterUrl: String,
    val cast: List<CastMember>
)

data class CastMember(
    val name: String,
    val photoUrl: String
)

// MAIN SCREEN
@Composable
fun MovieDetailScreen(
    movie: Movie,
    isAdminMode: Boolean = false,
    isFavorite: Boolean = false, // ✅ BARU
    onFavoriteClick: () -> Unit = {}, // ✅ BARU
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAEAEA))
                .verticalScroll(scrollState)
                .navigationBarsPadding()
        ) {
            // Header Image
            HeaderSection(imageUrl = movie.posterUrl)

            // Content Body
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Genres
                Text(
                    text = movie.genres.joinToString(" • "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row (Rating & Duration)
                StatsSection(rating = movie.rating, duration = movie.duration)

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }

        // TOP BAR (Back, Favorite & Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Row {
                // ✅ BARU - Favorite Button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFC107) else Color.White
                    )
                }

                // Delete Button (Only in Admin Mode)
                if (isAdminMode) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }

        // Edit FAB (Only in Admin Mode)
        if (isAdminMode) {
            FloatingActionButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Movie")
            }
        }
    }
}

// SUB-COMPONENTS
@Composable
fun HeaderSection(imageUrl: String) {
    // AsyncImage from Coil to load URL
    AsyncImage(
        model = imageUrl,
        contentDescription = "Movie Poster",
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StatsSection(rating: Double, duration: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Rating Item
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "IMDb Rating", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = "$rating/10", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
        }

        // Duration Item
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Duration",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Duration", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = duration, color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
        }
    }
}