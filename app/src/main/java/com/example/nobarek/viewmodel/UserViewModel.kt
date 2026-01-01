package com.example.nobarek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nobarek.data.local.UserEntity
import com.example.nobarek.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    
    private val _loggedInUser = MutableStateFlow<UserEntity?>(null)
    val loggedInUser: StateFlow<UserEntity?> = _loggedInUser.asStateFlow()
    
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()
    
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()
    
    init {
        // Seed default users on initialization
        seedDefaultUsers()
    }
    
    private fun seedDefaultUsers() {
        viewModelScope.launch {
            repository.seedDefaultUsers()
        }
    }

    fun forceLogin(username: String, role: String) {
        // Kita langsung set state user sebagai login
        // Asumsikan User adalah data class Anda
        _loggedInUser.value = UserEntity(username = username, password = "", role = role)
        _isAdmin.value = (role == "admin")
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _loginError.value = "Username and password cannot be empty"
                return@launch
            }
            
            val user = repository.login(username, password)
            if (user != null) {
                _loggedInUser.value = user
                _isAdmin.value = (user.role == "admin")
                _loginError.value = null
            } else {
                _loginError.value = "Invalid username or password"
            }
        }
    }
    
    fun logout() {
        _loggedInUser.value = null
        _isAdmin.value = false
        _loginError.value = null
    }
    
    fun register(username: String, password: String, role: String = "user", onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _loginError.value = "Username and password cannot be empty"
                return@launch
            }
            
            val success = repository.register(username, password, role)
            if (success) {
                _loginError.value = null
                onSuccess()
            } else {
                _loginError.value = "Username already exists"
            }
        }
    }
    
    fun clearError() {
        _loginError.value = null
    }
}

// Factory
class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
