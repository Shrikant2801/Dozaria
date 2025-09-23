package com.example.dozaria.data.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val age: Int = 0,
    val interests: List<String> = emptyList(),
    val xpPoints: Int = 0,
    val profileImageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val completedChallenges: List<String> = emptyList(),
    val postsCount: Int = 0
) {
    constructor() : this("", "", "", 0, emptyList(), 0, "", Timestamp.now(), emptyList(), 0)
}