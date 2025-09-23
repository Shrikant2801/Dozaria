package com.example.dozaria.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.model.Challenge
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChallengesUiState(
    val isLoading: Boolean = false,
    val challenges: List<Challenge> = emptyList(),
    val errorMessage: String? = null,
    val currentUserId: String = "",
    val showCreateDialog: Boolean = false
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState
    
    init {
        val currentUser = authRepository.getCurrentUser()
        _uiState.value = _uiState.value.copy(currentUserId = currentUser?.uid ?: "")
        loadChallenges()
    }
    
    fun loadChallenges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            challengeRepository.getChallenges().fold(
                onSuccess = { challenges ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        challenges = challenges
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load challenges"
                    )
                }
            )
        }
    }
    
    fun joinChallenge(challengeId: String) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUserId
            if (currentUserId.isNotEmpty()) {
                challengeRepository.joinChallenge(challengeId, currentUserId).fold(
                    onSuccess = {
                        loadChallenges() // Refresh to show updated participant count
                    },
                    onFailure = { /* Handle error silently for now */ }
                )
            }
        }
    }
    
    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }
    
    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }
}