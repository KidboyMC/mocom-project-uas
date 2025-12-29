package com.example.nobarek.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nobarek.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    navController: NavController,
    viewModel: MovieViewModel,
    movieId: Int
) {
    // State for Form
    var title by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var genres by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // If Edit Mode (movieId != 0), load older data
    LaunchedEffect(movieId) {
        if (movieId != 0) {
            viewModel.getMovieDetail(movieId)
        }
    }

    // Automatically fill form when data is available
    val selectedMovie by viewModel.selectedMovie.collectAsState()
    LaunchedEffect(selectedMovie) {
        if (movieId != 0 && selectedMovie != null) {
            val m = selectedMovie!!
            title = m.title
            rating = m.rating.toString()
            duration = m.duration
            genres = m.genres.joinToString(",")
            posterUrl = m.posterUrl
            description = m.description
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (movieId == 0) "Add Movie" else "Edit Movie") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Field: Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Field: Rating
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating (0.0 - 10.0)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Field: Duration
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (e.g. 2h 10m)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Field: Genre
            OutlinedTextField(
                value = genres,
                onValueChange = { genres = it },
                label = { Text("Genres (coma separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Field: Poster URL
            OutlinedTextField(
                value = posterUrl,
                onValueChange = { posterUrl = it },
                label = { Text("Poster URL (Link Gambar)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Field: Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(24.dp))

            // SAVE BUTTON
            Button(
                onClick = {
                    viewModel.saveMovie(
                        id = movieId, // 0 = Add, >0 = Update
                        title = title,
                        rating = rating.toDoubleOrNull() ?: 0.0,
                        description = description,
                        posterUrl = posterUrl,
                        genres = genres,
                        duration = duration
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}