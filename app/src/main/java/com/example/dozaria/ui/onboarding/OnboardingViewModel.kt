package com.example.dozaria.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false,
    val selectedInterests: Set<String> = emptySet()
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState
    
    val availableInterests = listOf(
        "Travel", "Fitness", "Reading", "Cooking", "Photography",
        "Music", "Art", "Gaming", "Sports", "Technology",
        "Fashion", "Dancing", "Writing", "Hiking", "Swimming"
    )
    
    fun toggleInterest(interest: String) {
        val currentInterests = _uiState.value.selectedInterests
        val newInterests = if (currentInterests.contains(interest)) {
            currentInterests - interest
        } else {
            currentInterests + interest
        }
        _uiState.value = _uiState.value.copy(selectedInterests = newInterests)
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null && _uiState.value.selectedInterests.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                userRepository.updateUserInterests(
                    currentUser.uid,
                    _uiState.value.selectedInterests.toList()
                ).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isCompleted = true
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to save interests"
                        )
                    }
                )
            }
        }
    }
}