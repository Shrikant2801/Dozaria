package com.example.dozaria.ui.addpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.model.Post
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.PostRepository
import com.example.dozaria.data.repository.UserRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddPostUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPosted: Boolean = false
)

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddPostUiState())
    val uiState: StateFlow<AddPostUiState> = _uiState
    
    fun createPost(content: String, imageUris: List<android.net.Uri> = emptyList()) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                // Get user profile
                userRepository.getUser(currentUser.uid).fold(
                    onSuccess = { user ->
                        // Upload images if any
                        val imageUrls = mutableListOf<String>()
                        imageUris.forEach { uri ->
                            postRepository.uploadImage(uri).fold(
                                onSuccess = { url -> imageUrls.add(url) },
                                onFailure = { /* Handle error */ }
                            )
                        }
                        
                        val post = Post(
                            userId = currentUser.uid,
                            userName = user.name,
                            userProfileImage = user.profileImageUrl,
                            content = content,
                            imageUrls = imageUrls,
                            createdAt = Timestamp.now()
                        )
                        
                        postRepository.createPost(post).fold(
                            onSuccess = {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isPosted = true
                                )
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = exception.message ?: "Failed to create post"
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