package com.example.nobarek.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Insert
    suspend fun insertMovie(movie: MovieEntity)

    @Insert
    suspend fun insertAll(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE type = :type")
    suspend fun getMoviesByType(type: String): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR genres LIKE '%' || :query || '%'")
    suspend fun searchMovies(query: String): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    // ✅ BARU - Query untuk favorit
    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    suspend fun getFavoriteMovies(): List<MovieEntity>

    // ✅ BARU - Update status favorit
    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :movieId")
    suspend fun updateFavoriteStatus(movieId: Int, isFavorite: Boolean)
}