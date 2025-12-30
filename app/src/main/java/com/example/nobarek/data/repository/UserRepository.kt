package com.example.nobarek.data.repository

import com.example.nobarek.data.local.UserDao
import com.example.nobarek.data.local.UserEntity

class UserRepository(private val userDao: UserDao) {
    
    // Login - validate username and password
    suspend fun login(username: String, password: String): UserEntity? {
        val user = userDao.getUserByUsername(username)
        return if (user != null && user.password == password) {
            user
        } else {
            null
        }
    }
    
    // Register new user
    suspend fun register(username: String, password: String, role: String = "user"): Boolean {
        // Check if username already exists
        val exists = userDao.isUsernameExists(username) > 0
        if (exists) return false
        
        val user = UserEntity(
            username = username,
            password = password,
            role = role
        )
        val result = userDao.insertUser(user)
        return result > 0 // Returns true if insertion successful
    }
    
    // Get user by ID
    suspend fun getUserById(id: Int): UserEntity? {
        return userDao.getUserById(id)
    }
    
    // Seed default users (call this on first app run)
    suspend fun seedDefaultUsers() {
        // Check if users already exist
        val allUsers = userDao.getAllUsers()
        if (allUsers.isEmpty()) {
            // Seed default admin and user
            userDao.insertUser(UserEntity(username = "admin", password = "admin123", role = "admin"))
            userDao.insertUser(UserEntity(username = "user", password = "user123", role = "user"))
        }
    }
}
