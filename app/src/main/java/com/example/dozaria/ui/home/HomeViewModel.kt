package com.example.dozaria.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dozaria.data.model.Post
import com.example.dozaria.data.repository.AuthRepository
import com.example.dozaria.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val errorMessage: String? = null,
    val currentUserId: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    
    init {
        val currentUser = authRepository.getCurrentUser()
        _uiState.value = _uiState.value.copy(currentUserId = currentUser?.uid ?: "")
        loadPosts()
    }
    
    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            postRepository.getPosts().fold(
                onSuccess = { posts ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        posts = posts
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load posts"
                    )
                }
            )
        }
    }
    
    fun likePost(postId: String) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUserId
            if (currentUserId.isNotEmpty()) {
                postRepository.likePost(postId, currentUserId).fold(
                    onSuccess = {
                        loadPosts() // Refresh posts to show updated likes
                    },
                    onFailure = { /* Handle error silently for now */ }
                )
            }
        }
    }
}