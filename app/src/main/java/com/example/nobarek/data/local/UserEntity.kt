package com.example.nobarek.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String, // In production, this should be hashed
    val role: String // "admin" or "user"
)
