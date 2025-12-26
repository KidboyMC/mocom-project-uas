package com.example.nobarek.data

import com.example.nobarek.screen.CastMember
import com.example.nobarek.screen.Movie
import com.example.nobarek.screen.MovieItem

// Use one object to store all dummy data
object MovieData {
    // Data for Home Screen
    val featuredMovies = listOf(
        MovieItem(1, "Fallout", 8.3, "https://image.idntimes.com/post/20240307/prime-video-fallout-key-arts-april-11-b4eac55061a96a5027dd94105f79757d.jpg"),
        MovieItem(2, "Avatar: Fire", 7.6, "https://resizing.flixster.com/Pq8B1yv0h4jocOwrukzjlvcdQDQ=/ems.cHJkLWVtcy1hc3NldHMvbW92aWVzLzRlNjBlZDhkLTg3NjItNDgzNy05OWYyLTE4ODI0MjBmMGU0OC5qcGc="),
        MovieItem(3, "Stranger Things", 8.6, "https://pasjabar.com/wp-content/uploads/2025/11/stranger-things-characters-5.jpg")
    )

    val popularMovies = listOf(
        MovieItem(4, "Stranger Things", 8.6, "https://pasjabar.com/wp-content/uploads/2025/11/stranger-things-characters-5.jpg", listOf("Fantasy", "Sci-Fi", "Supernatural"), "1h", "(1.5M)"),
        MovieItem(5, "Dhurandhar", 8.6, "https://www.lab-1.nl/wp-content/uploads/2025/12/Ranveer.jpeg", listOf("Gangster", "Action"), "3h 40m", "(83K)")
    )

    // Data for Detail Screen
    // This function searches for movies based on ID, then returns the complete Movie object
    fun getMovieDetailById(id: Int): Movie {
        // Default dummy if ID not found
        return Movie(
            title = "Unknown Movie",
            season = "Season 1",
            genres = listOf("Action"),
            rating = 0.0,
            duration = "-",
            description = "Description not found.",
            posterUrl = "",
            cast = emptyList()
        )
    }
}

// Simple extension function for mapping dummy data to details
fun getDummyMovieDetail(id: Int, title: String, poster: String): Movie {
    return Movie(
        title = title,
        season = "Season 1",
        genres = listOf("Sci-Fi", "Action"),
        rating = 8.5,
        duration = "2h 10m",
        description = "This is a dummy description for $title. The movie is packed with action and drama.",
        posterUrl = poster,
        cast = listOf(
            CastMember("Actor A", "https://via.placeholder.com/150"),
            CastMember("Actor B", "https://via.placeholder.com/150")
        )
    )
}