package com.example.dozaria.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState {
    object Loading : SplashState()
    object NavigateToLogin : SplashState()
    object NavigateToOnboarding : SplashState()
    object NavigateToMain : SplashState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _splashState.value = SplashState.NavigateToLogin
            } else {
                // Check if user has completed onboarding
                val userResult = userRepository.getUser(currentUser.uid)
                userResult.fold(
                    onSuccess = { user ->
                        if (user.interests.isEmpty()) {
                            _splashState.value = SplashState.NavigateToOnboarding
                        } else {
                            _splashState.value = SplashState.NavigateToMain
                        }
                    },
                    onFailure = {
                        // User profile doesn't exist, need onboarding
                        _splashState.value = SplashState.NavigateToOnboarding
                    }
                )
            }
        }
    }
}