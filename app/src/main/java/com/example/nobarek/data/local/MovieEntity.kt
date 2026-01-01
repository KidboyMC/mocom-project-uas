package com.example.nobarek.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rating: Double,
    val posterUrl: String,
    val description: String,
    val genres: String,
    val duration: String,
    val type: String, // "featured" atau "popular"
    val isFavorite: Boolean = false // ✅ BARU - untuk tracking favorit
)