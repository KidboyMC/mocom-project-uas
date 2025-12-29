package com.example.nobarek.data.repository

import com.example.nobarek.data.local.MovieDao
import com.example.nobarek.data.local.MovieEntity

class MovieRepository(private val movieDao: MovieDao) {

    // Select data for Featured (Type = "featured")
    suspend fun getFeaturedMovies(): List<MovieEntity> {
        // Check it first, if empty use dummy (Seeding)
        val isFeaturedEmpty = movieDao.getMoviesByType("featured").isEmpty()
        val isPopularEmpty = movieDao.getMoviesByType("popular").isEmpty()

        // Seed if only both are empty
        if (isFeaturedEmpty && isPopularEmpty) {
            seedDatabase()
        }

        return movieDao.getMoviesByType("featured")
    }

    // Select data for Popular (Type = "popular")
    suspend fun getPopularMovies(): List<MovieEntity> {
        return movieDao.getMoviesByType("popular")
    }

    // Search
    suspend fun searchMovies(query: String): List<MovieEntity> {
        return movieDao.searchMovies(query)
    }

    // Get Detail
    suspend fun getMovieById(id: Int): MovieEntity? {
        return movieDao.getMovieById(id)
    }

    suspend fun saveMovie(movie: MovieEntity) {
        movieDao.insertMovie(movie)
    }

    suspend fun deleteMovie(movie: MovieEntity) {
        movieDao.deleteMovie(movie)
    }

    // SEEDING DATA
    private suspend fun seedDatabase() {
        val dummyData = listOf(
            MovieEntity(
                title = "Fallout",
                rating = 8.3,
                posterUrl = "https://image.idntimes.com/post/20240307/prime-video-fallout-key-arts-april-11-b4eac55061a96a5027dd94105f79757d.jpg",
                description = "Based on the video game series.",
                genres = "Action,Sci-Fi",
                duration = "1h",
                type = "featured"
            ),
            MovieEntity(
                title = "Stranger Things",
                rating = 8.6,
                posterUrl = "https://images.squarespace-cdn.com/content/v1/51b3dc8ee4b051b96ceb10de/8e95aca5-8f48-4678-b204-4b2af3f4c452/STARNGER+THINGS+5+Trailer+and+Poster+Teases+The+Final+Epic+Battle+and+%2522One+Last+Adventure%2522.jpg",
                description = "A group of kids in the 80s...",
                genres = "Fantasy,Horror",
                duration = "50m",
                type = "featured"
            ),
            MovieEntity(
                title = "Stranger Things",
                rating = 8.6,
                posterUrl = "https://images.squarespace-cdn.com/content/v1/51b3dc8ee4b051b96ceb10de/8e95aca5-8f48-4678-b204-4b2af3f4c452/STARNGER+THINGS+5+Trailer+and+Poster+Teases+The+Final+Epic+Battle+and+%2522One+Last+Adventure%2522.jpg",
                description = "A group of kids in the 80s...",
                genres = "Fantasy,Horror",
                duration = "50m",
                type = "popular"
            ),
            MovieEntity(
                title = "Dhurandhar",
                rating = 8.6,
                posterUrl = "https://www.lab-1.nl/wp-content/uploads/2025/12/Ranveer.jpeg",
                description = "An epic tale of adventure...",
                genres = "Action,Adventure",
                duration = "3h 40m",
                type = "popular"
            )
        )
        movieDao.insertAll(dummyData)
    }
}