package com.example.dozaria.data.repository

import com.example.dozaria.data.model.Post
import com.example.dozaria.data.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    
    suspend fun createPost(post: Post): Result<String> {
        return try {
            val postId = UUID.randomUUID().toString()
            val postWithId = post.copy(id = postId)
            firestore.collection("posts").document(postId).set(postWithId).await()
            Result.success(postId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPosts(): Result<List<Post>> {
        return try {
            val querySnapshot = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val posts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return try {
            val querySnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val posts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val likes = snapshot.get("likes") as? List<String> ?: emptyList()
                val newLikes = if (likes.contains(userId)) {
                    likes - userId
                } else {
                    likes + userId
                }
                transaction.update(postRef, "likes", newLikes)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addComment(postId: String, comment: Comment): Result<Unit> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val comments = snapshot.get("comments") as? List<Map<String, Any>> ?: emptyList()
                val commentMap = mapOf(
                    "id" to comment.id,
                    "postId" to comment.postId,
                    "userId" to comment.userId,
                    "userName" to comment.userName,
                    "userProfileImage" to comment.userProfileImage,
                    "content" to comment.content,
                    "createdAt" to comment.createdAt,
                    "likes" to comment.likes
                )
                transaction.update(postRef, "comments", comments + commentMap)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadImage(imageUri: android.net.Uri): Result<String> {
        return try {
            val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}