package com.example.dozaria.data.repository

import com.example.dozaria.data.model.Challenge
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    
    suspend fun createChallenge(challenge: Challenge): Result<String> {
        return try {
            val challengeId = UUID.randomUUID().toString()
            val challengeWithId = challenge.copy(id = challengeId)
            firestore.collection("challenges").document(challengeId).set(challengeWithId).await()
            Result.success(challengeId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getChallenges(): Result<List<Challenge>> {
        return try {
            val querySnapshot = firestore.collection("challenges")
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val challenges = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Challenge::class.java)
            }
            Result.success(challenges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getChallenge(challengeId: String): Result<Challenge> {
        return try {
            val document = firestore.collection("challenges").document(challengeId).get().await()
            val challenge = document.toObject(Challenge::class.java)
            if (challenge != null) {
                Result.success(challenge)
            } else {
                Result.failure(Exception("Challenge not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun joinChallenge(challengeId: String, userId: String): Result<Unit> {
        return try {
            val challengeRef = firestore.collection("challenges").document(challengeId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(challengeRef)
                val participants = snapshot.get("participants") as? List<String> ?: emptyList()
                if (!participants.contains(userId)) {
                    transaction.update(challengeRef, "participants", participants + userId)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun completeChallenge(challengeId: String, userId: String): Result<Unit> {
        return try {
            val challengeRef = firestore.collection("challenges").document(challengeId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(challengeRef)
                val completedBy = snapshot.get("completedBy") as? List<String> ?: emptyList()
                if (!completedBy.contains(userId)) {
                    transaction.update(challengeRef, "completedBy", completedBy + userId)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserChallenges(userId: String): Result<List<Challenge>> {
        return try {
            val querySnapshot = firestore.collection("challenges")
                .whereArrayContains("participants", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val challenges = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Challenge::class.java)
            }
            Result.success(challenges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}