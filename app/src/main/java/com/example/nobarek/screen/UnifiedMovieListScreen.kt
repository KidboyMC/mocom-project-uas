import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                SortFilterRow()
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
                    items(results) { movie ->
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
fun SortFilterRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Sort By:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(8.dp))

        SortChip(text = "Name (A - Z)")
        Spacer(modifier = Modifier.width(8.dp))
        SortChip(text = "All Genre")
    }
}

@Composable
fun SortChip(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { /* Handle Sort Click */ }
    ) {
        Text(text = text, fontSize = 12.sp, color = Color.Gray)
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(50.dp)),
        placeholder = { Text("Search movies...") },
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
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