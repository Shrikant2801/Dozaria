package com.example.dozaria.data.model

import com.google.firebase.Timestamp

data class Challenge(
    val id: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val videoUrl: String = "",
    val xpReward: Int = 0,
    val deadline: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now(),
    val participants: List<String> = emptyList(), // User IDs
    val completedBy: List<String> = emptyList(), // User IDs who completed
    val isActive: Boolean = true,
    val category: String = "",
    val difficulty: ChallengeDifficulty = ChallengeDifficulty.EASY
) {
    constructor() : this("", "", "", "", "", "", "", 0, Timestamp.now(), Timestamp.now(), emptyList(), emptyList(), true, "", ChallengeDifficulty.EASY)
}

enum class ChallengeDifficulty(val displayName: String, val multiplier: Double) {
    EASY("Easy", 1.0),
    MEDIUM("Medium", 1.5),
    HARD("Hard", 2.0)
}