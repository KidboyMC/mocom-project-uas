package com.example.nobarek.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nobarek.viewmodel.MovieViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit
) {
    val popularMovies by viewModel.popularMovies.collectAsState()
    val featuredMovies by viewModel.featuredMovies.collectAsState()
    val backgroundColor = Color(0xFFEAEAEA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (featuredMovies.isNotEmpty()) {
                    MovieBannerSlider(movies = featuredMovies.take(5), onMovieClick = onMovieClick)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
                    Text("Popular Movies", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Most watched films this week", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            items(popularMovies) { movie ->
                NetflixStyleCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieBannerSlider(movies: List<MovieItem>, onMovieClick: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { movies.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(pagerState.currentPage, isDragged) {
        if (!isDragged) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % movies.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth().height(220.dp).statusBarsPadding()
        ) { page ->
            SliderCard(movie = movies[page], onClick = { onMovieClick(movies[page].id) })
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.5f)
                Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).height(8.dp).width(if (pagerState.currentPage == iteration) 24.dp else 8.dp))
            }
        }
    }
}

@Composable
fun SliderCard(movie: MovieItem, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(model = movie.posterUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFC107)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("Featured", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Text(text = movie.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            }
        }
    }
}

@Composable
fun NetflixStyleCard(movie: MovieItem, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)) {
            AsyncImage(model = movie.posterUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Text(text = movie.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, modifier = Modifier.padding(top = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
            Text(text = "${movie.rating}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}