package com.example.nobarek.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nobarek.viewmodel.MovieViewModel

@Composable
fun SeriesScreen(
    viewModel: MovieViewModel,
    onSeriesClick: (Int) -> Unit
) {
    val featuredSeries by viewModel.featuredMovies.collectAsState()
    val popularSeries by viewModel.popularMovies.collectAsState()
    val heroItem = featuredSeries.firstOrNull()
    val backgroundColor = Color(0xFFEAEAEA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // 1. HERO BANNER
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (heroItem != null) {
                    HeroBanner(movie = heroItem, onClick = { onSeriesClick(heroItem.id) })
                }
            }

            // 2. TRENDING SECTION
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    SectionTitle("Trending Now")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // Jarak antar kartu lebih lega
                    ) {
                        items(popularSeries.take(5)) { series ->
                            // Menggunakan Kartu Cinematic (Landscape) agar beda dengan bawahnya
                            CinematicTrendingCard(series, onClick = { onSeriesClick(series.id) })
                        }
                    }
                }
            }

            // 3. ALL SERIES GRID
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle("All Series")
            }

            items(popularSeries) { series ->
                SeriesGridCard(series, onClick = { onSeriesClick(series.id) })
            }
        }
    }
}


@Composable
fun HeroBanner(movie: MovieItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Surface(color = Color(0xFFFFC107), shape = RoundedCornerShape(4.dp)) {
                Text(
                    text = "FEATURED SERIES",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold, color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Watch Now", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CinematicTrendingCard(movie: MovieItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(260.dp) // Lebih lebar (Landscape)
            .height(150.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent),
                            endX = 400f
                        )
                    )
            )

            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.align(Alignment.Center).size(48.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
                    .fillMaxWidth(0.7f) // Batasi lebar teks agar tidak nabrak kanan
            ) {
                Text(
                    text = "TRENDING #1",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Season 1",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun SeriesGridCard(movie: MovieItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        ) {
            Box {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color(0xFFFFC107),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${movie.rating}",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
    )
}