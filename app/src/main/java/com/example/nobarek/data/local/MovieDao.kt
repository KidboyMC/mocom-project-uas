package com.example.nobarek.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    // Select All Data
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    // Select Data by Category (Featured/Popular)
    @Query("SELECT * FROM movies WHERE type = :category")
    suspend fun getMoviesByType(category: String): List<MovieEntity>

    // Search Movies
    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    suspend fun searchMovies(query: String): List<MovieEntity>

    // Select Movie Detail
    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    // Add Data (Multiple Data)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    // Add Movie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    // Delete Movie
    @Delete
    suspend fun deleteMovie(movie: MovieEntity)
}