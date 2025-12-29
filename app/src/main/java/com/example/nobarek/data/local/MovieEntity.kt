package com.example.nobarek.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID++
    val title: String,
    val rating: Double,
    val posterUrl: String,
    val description: String,
    val genres: String,
    val duration: String,
    val type: String // Featured or Popular
)