package com.example.dozaria.data.repository

import com.example.dozaria.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUser(userId: String): Result<User> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            val user = document.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLeaderboard(): Result<List<User>> {
        return try {
            val querySnapshot = firestore.collection("users")
                .orderBy("xpPoints", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            
            val users = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addXpToUser(userId: String, xp: Int): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentXp = snapshot.getLong("xpPoints") ?: 0L
                transaction.update(userRef, "xpPoints", currentXp + xp)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserInterests(userId: String, interests: List<String>): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("interests", interests).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}