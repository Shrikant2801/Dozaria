package com.example.dozaria.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.model.Challenge
import com.example.dozaria.data.model.ChallengeDifficulty
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.ChallengeRepository
import com.example.dozaria.data.repository.UserRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class CreateChallengeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreated: Boolean = false
)

@HiltViewModel
class CreateChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateChallengeUiState())
    val uiState: StateFlow<CreateChallengeUiState> = _uiState
    
    fun createChallenge(
        title: String,
        description: String,
        xpReward: Int,
        deadline: Date,
        difficulty: ChallengeDifficulty
    ) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                // Get user profile for creator name
                userRepository.getUser(currentUser.uid).fold(
                    onSuccess = { user ->
                        val challenge = Challenge(
                            creatorId = currentUser.uid,
                            creatorName = user.name,
                            title = title,
                            description = description,
                            xpReward = (xpReward * difficulty.multiplier).toInt(),
                            deadline = Timestamp(deadline),
                            difficulty = difficulty
                        )
                        
                        challengeRepository.createChallenge(challenge).fold(
                            onSuccess = {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isCreated = true
                                )
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = exception.message ?: "Failed to create challenge"
                                )
                            }
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to get user info"
                        )
                    }
                )
            }
        }
    }
}