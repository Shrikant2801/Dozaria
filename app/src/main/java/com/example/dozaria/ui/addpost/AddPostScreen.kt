package com.example.dozaria.ui.addpost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dozaria.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostScreen(
    onPostCreated: () -> Unit,
    viewModel: AddPostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var content by remember { mutableStateOf("") }
    
    LaunchedEffect(uiState.isPosted) {
        if (uiState.isPosted) {
            onPostCreated()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.create_post),
            style = MaterialTheme.typography.headlineMedium
        )
        
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(stringResource(R.string.post_content)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10,
            placeholder = { Text("Share something amazing with the community!") }
        )
        
        Button(
            onClick = { viewModel.createPost(content) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && content.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.post))
            }
        }
        
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}