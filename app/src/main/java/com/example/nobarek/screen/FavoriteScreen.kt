package com.example.nobarek.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nobarek.viewmodel.MovieViewModel
import kotlin.random.Random

// Still Placeholder
@Composable
fun FavoriteScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit
) {
    val favoriteList by viewModel.popularMovies.collectAsState()
    val lightBackground = Color(0xFFEAEAEA)
    val cardBackground = Color.White
    val accentColor = Color(0xFFFFC107)

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Movies", "Series")

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = lightBackground,
    ) { paddingValues ->

        if (favoriteList.isEmpty()) {
            EmptyStateLightUI()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = "My Favorite",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Text(
                            text = "Continue where you left off",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        items(filters) { filter ->
                            FilterChipLight(
                                text = filter,
                                isSelected = filter == selectedFilter,
                                accentColor = accentColor,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Continue Watching",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                    )
                }

                if (favoriteList.isNotEmpty()) {
                    item {
                        BigContinueWatchingCardLight(
                            movie = favoriteList.first(),
                            accentColor = accentColor,
                            onClick = { onMovieClick(favoriteList.first().id) }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Watchlist",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${favoriteList.size} items",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                items(favoriteList.drop(1)) { movie ->
                    LightWatchlistRow(
                        movie = movie,
                        cardColor = cardBackground,
                        onClick = { onMovieClick(movie.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}



@Composable
fun BigContinueWatchingCardLight(movie: MovieItem, accentColor: Color, onClick: () -> Unit) {
    val randomProgress = remember { Random.nextFloat() * 0.6f + 0.2f }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = accentColor, // Icon Kuning
                    modifier = Modifier.size(64.dp)
                )
            }
            LinearProgressIndicator(
            progress = { randomProgress },
            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .align(Alignment.BottomCenter),
            color = accentColor,
            trackColor = Color.White.copy(alpha = 0.5f),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "S1:E5 • 24m remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun LightWatchlistRow(movie: MovieItem, cardColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(90.dp)
            .background(cardColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        // Poster Kecil
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(70.dp)
                .fillMaxHeight()
        )

        // Info Text
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Movie • 2h 10m",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Button Remove
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.BookmarkRemove,
                    contentDescription = "Remove",
                    tint = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun FilterChipLight(text: String, isSelected: Boolean, accentColor: Color, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color.Black else Color.White,
        shape = RoundedCornerShape(50),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) accentColor else Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EmptyStateLightUI() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.BookmarkRemove,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}