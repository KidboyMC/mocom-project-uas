package com.example.nobarek.viewmodel

import com.example.nobarek.screen.MovieResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nobarek.data.local.MovieEntity
import com.example.nobarek.data.repository.MovieRepository
import com.example.nobarek.screen.Movie
import com.example.nobarek.screen.MovieItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // --- STATE FOR HOME SCREEN ---
    private val _featuredMovies = MutableStateFlow<List<MovieItem>>(emptyList())
    val featuredMovies: StateFlow<List<MovieItem>> = _featuredMovies.asStateFlow()

    private val _popularMovies = MutableStateFlow<List<MovieItem>>(emptyList())
    val popularMovies: StateFlow<List<MovieItem>> = _popularMovies.asStateFlow()

    // --- STATE FOR SEARCH SCREEN ---
    private val _searchResults = MutableStateFlow<List<MovieResult>>(emptyList())
    val searchResults: StateFlow<List<MovieResult>> = _searchResults.asStateFlow()

    // --- STATE FOR DETAIL SCREEN ---
    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Collect data from repository and convert it to MovieItem
            val featured = repository.getFeaturedMovies().map { entity ->
                MovieItem(entity.id, entity.title, entity.rating, entity.posterUrl, entity.genres.split(","), entity.duration)
            }
            _featuredMovies.value = featured

            val popular = repository.getPopularMovies().map { entity ->
                MovieItem(entity.id, entity.title, entity.rating, entity.posterUrl, entity.genres.split(","), entity.duration)
            }
            _popularMovies.value = popular
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            val results = repository.searchMovies(query).map { entity ->
                MovieResult(entity.id, entity.title, entity.rating, entity.posterUrl)
            }
            _searchResults.value = results
        }
    }

    // Reset search results if View More is clicked
    fun loadAllMoviesForList() {
        viewModelScope.launch {
            val featured = repository.getFeaturedMovies()
            val popular = repository.getPopularMovies()

            val allCombined = (featured + popular)
                // Filter by Title
                .distinctBy { it.title }

                .map { entity ->
                    MovieResult(
                        id = entity.id,
                        title = entity.title,
                        rating = entity.rating,
                        posterUrl = entity.posterUrl
                    )
                }

            _searchResults.value = allCombined
        }
    }

    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            val entity = repository.getMovieById(id)
            if (entity != null) {
                // Mapping Entity to Movie Detail Model
                _selectedMovie.value = Movie(
                    id = entity.id,
                    title = entity.title,
                    genres = entity.genres.split(","),
                    rating = entity.rating,
                    duration = entity.duration,
                    description = entity.description,
                    posterUrl = entity.posterUrl,
                    cast = emptyList() // TODO
                )
            }
        }
    }

    fun saveMovie(
        id: Int = 0,
        title: String,
        rating: Double,
        description: String,
        posterUrl: String,
        genres: String,
        duration: String
    ) {
        viewModelScope.launch {
            val movie = MovieEntity(
                id = id,
                title = title,
                rating = rating,
                description = description,
                posterUrl = posterUrl,
                genres = genres,
                duration = duration,
                type = "featured"
            )
            repository.saveMovie(movie)
            loadHomeData() // Refresh homepage
        }
    }

    fun deleteMovie(movieId: Int) {
        viewModelScope.launch {
            val entity = repository.getMovieById(movieId)
            if (entity != null) {
                repository.deleteMovie(entity)
                loadHomeData()
            }
        }
    }
}

// --- FACTORY (Inject Repository to ViewModel) ---
class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}