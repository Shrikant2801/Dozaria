package com.example.dozaria.ui.challenges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dozaria.R
import com.example.dozaria.data.model.ChallengeDifficulty
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeDialog(
    onDismiss: () -> Unit,
    onChallengeCreated: () -> Unit,
    viewModel: CreateChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var xpReward by remember { mutableStateOf("50") }
    var selectedDifficulty by remember { mutableStateOf(ChallengeDifficulty.EASY) }
    var deadlineDate by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }.time) }
    
    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) {
            onChallengeCreated()
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_challenge),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.challenge_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.challenge_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                OutlinedTextField(
                    value = xpReward,
                    onValueChange = { xpReward = it },
                    label = { Text("Base XP Reward") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                // Difficulty selection
                Text(text = "Difficulty", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChallengeDifficulty.values().forEach { difficulty ->
                        FilterChip(
                            onClick = { selectedDifficulty = difficulty },
                            label = { 
                                Text("${difficulty.displayName} (${difficulty.multiplier}x)") 
                            },
                            selected = selectedDifficulty == difficulty,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Deadline display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Deadline",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(deadlineDate),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Text(
                    text = "Final XP Reward: ${(xpReward.toIntOrNull() ?: 0) * selectedDifficulty.multiplier}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Button(
                        onClick = {
                            val xp = xpReward.toIntOrNull() ?: 50
                            viewModel.createChallenge(
                                title = title,
                                description = description,
                                xpReward = xp,
                                deadline = deadlineDate,
                                difficulty = selectedDifficulty
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading && title.isNotBlank() && description.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Create")
                        }
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
    }
}