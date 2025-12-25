package com.example.nobarek.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// DATA MODELS
data class Movie(
    val title: String,
    val season: String,
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
fun MovieDetailScreen(movie: Movie) {
    val scrollState = rememberScrollState()

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
                text = "${movie.title}:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = movie.season,
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

            Spacer(modifier = Modifier.height(32.dp))

            // Cast Section
            CastSection(castList = movie.cast)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CastSection(castList: List<CastMember>) {
    // FlowRow to have items automatically drop down if not loaded
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 2
    ) {
        castList.forEach { actor ->
            CastChip(actor)
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun CastChip(actor: CastMember) {
    Surface(
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier.height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            // Cast Img
            AsyncImage(
                model = actor.photoUrl,
                contentDescription = actor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Cast Name
            Text(
                text = actor.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

// PREVIEW
@Preview(showBackground = true)
@Composable
fun PreviewMovieDetail() {
    MaterialTheme {
        MovieDetailScreen(movie = dummyMovie)
    }
}

// Dummy Data
val dummyMovie = Movie(
    title = "Stranger Things",
    season = "Season 5",
    genres = listOf("Fantasy", "Sci-Fi", "Supernatural"),
    rating = 8.6,
    duration = "1h",
    description = "In a small town where everyone knows everyone, a peculiar incident starts a chain of events that leads to a child's disappearance, which begins to tear at the fabric of an otherwise-peaceful community.",
    posterUrl = "https://pasjabar.com/wp-content/uploads/2025/11/stranger-things-characters-5.jpg",
    cast = listOf(
        CastMember("Millie Bobby Brown", "https://assets.teenvogue.com/photos/6706b4defee2b18e092f6a2c/16:9/w_2560%2Cc_limit/2052040266"),
        CastMember("Finn Wolfhard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQyv5XIPId7xz1ZidxS4HhatZOsUZJAWz4oPQ&s"),
        CastMember("Winona Ryder", "https://static.wikia.nocookie.net/timburton/images/9/94/Winona_Ryder.jpg/revision/latest?cb=20240103025223")
    )
)