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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun SearchResultScreen(
    initialQuery: String,
    results: List<MovieResult>,
    totalResults: Int,
    onMovieClick: (Int) -> Unit
) {
    // State for search text field
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA))
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .navigationBarsPadding()
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                focusManager.clearFocus()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title Section
        Text(
            text = "Search Results: $initialQuery",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Grid Content (Dynamic)
        Box(modifier = Modifier.weight(1f)) {
            if (results.isEmpty()) {
                Text(
                    text = "No results found.",
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
                    items(results) { movie ->
                        MovieGridItem(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }
            }
        }
        val totalPages = if (totalResults > 0) (totalResults + 6 - 1) / 6 else 1
        // Pagination Footer
        PaginationFooter(
            currentPage = 1,
            totalPages = totalPages,
            currentCount = results.size,
            totalCount = totalResults
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
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
fun MovieGridItem(movie: MovieResult, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        // Poster Image
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
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
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
fun PaginationFooter(
    currentPage: Int,
    totalPages: Int,
    currentCount: Int,
    totalCount: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Showing $currentCount of $totalCount",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFFFC107), shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentPage.toString(),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// PREVIEW
@Preview(showBackground = true)
@Composable
fun PreviewPaginationLogic() {
    Column {
        // Case 1: (Showing 6 of 20)
        // Let's say 'results' contains only 6 items (because of pagination)
        val dummyListPage1 = List(6) { MovieResult(it, "Movie $it", 8.0, "") }

        Text("Kasus 1: Data Banyak (Page 1)")
        PaginationFooter(
            currentPage = 1,
            totalPages = 4,
            currentCount = dummyListPage1.size,
            totalCount = 20
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Case 2: (Showing 4 of 4)
        val dummyListSmall = List(4) { MovieResult(it, "Movie $it", 8.0, "") }

        Text("Kasus 2: Data Sedikit (< 6)")
        PaginationFooter(
            currentPage = 1,
            totalPages = 1,
            currentCount = dummyListSmall.size,
            totalCount = 4
        )
    }
}

val dummyResults = listOf(
    MovieResult(1, "Stranger Things", 8.6, "https://pasjabar.com/wp-content/uploads/2025/11/stranger-things-characters-5.jpg"),
    MovieResult(2, "Dark", 8.7, "https://m.media-amazon.com/images/M/MV5BOWJjMGViY2UtNTAzNS00ZGFjLWFkNTMtMDBiMDMyZTM1NTY3XkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg"),
    MovieResult(3, "Black Mirror", 8.7, "https://m.media-amazon.com/images/M/MV5BODcxMWI2NDMtYTc3NC00OTZjLWFmNmUtM2NmY2I1ODkxYzczXkEyXkFqcGc@._V1_.jpg")
)