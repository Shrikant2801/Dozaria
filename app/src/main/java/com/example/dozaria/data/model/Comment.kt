package com.example.dozaria.data.model

import com.google.firebase.Timestamp

data class Comment(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val likes: List<String> = emptyList() // User IDs who liked the comment
) {
    constructor() : this("", "", "", "", "", "", Timestamp.now(), emptyList())
}