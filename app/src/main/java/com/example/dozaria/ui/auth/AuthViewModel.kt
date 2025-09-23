package com.example.dozaria.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.model.User
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.UserRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val isSignupSuccessful: Boolean = false,
    val hasCompletedOnboarding: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.login(email, password).fold(
                onSuccess = { user ->
                    // Check if user has completed onboarding
                    userRepository.getUser(user.uid).fold(
                        onSuccess = { userProfile ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                hasCompletedOnboarding = userProfile.interests.isNotEmpty()
                            )
                        },
                        onFailure = {
                            // User profile doesn't exist, need onboarding
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                hasCompletedOnboarding = false
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
            )
        }
    }
    
    fun signup(email: String, password: String, name: String, age: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.signup(email, password).fold(
                onSuccess = { firebaseUser ->
                    val user = User(
                        id = firebaseUser.uid,
                        email = email,
                        name = name,
                        age = age,
                        createdAt = Timestamp.now()
                    )
                    
                    authRepository.createUserProfile(user).fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSignupSuccessful = true
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Failed to create profile"
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Signup failed"
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}