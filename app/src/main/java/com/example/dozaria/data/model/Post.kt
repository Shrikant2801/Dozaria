package com.example.dozaria.data.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val content: String = "",
    val imageUrls: List<String> = emptyList(),
    val videoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val likes: List<String> = emptyList(), // User IDs who liked
    val comments: List<Comment> = emptyList(),
    val isChallenge: Boolean = false,
    val challengeId: String = "", // If this post is a challenge completion
    val xpEarned: Int = 0
) {
    constructor() : this("", "", "", "", "", emptyList(), "", Timestamp.now(), emptyList(), emptyList(), false, "", 0)
}