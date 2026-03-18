package com.ontime.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.ontime.data.model.FocusSession
import kotlinx.coroutines.tasks.await

/**
 * Remote data source using Firebase Firestore
 */
class FirebaseRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val sessionsCollection = firestore.collection("focus_sessions")
    
    suspend fun syncSessionToFirebase(session: FocusSession) {
        try {
            sessionsCollection.document(session.id).set(session).await()
        } catch (e: Exception) {
            // Log error - in production use a proper logging framework
            e.printStackTrace()
        }
    }
    
    suspend fun deleteSessionFromFirebase(sessionId: String) {
        try {
            sessionsCollection.document(sessionId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun getSessions(): List<FocusSession> {
        return try {
            val snapshot = sessionsCollection.get().await()
            snapshot.toObjects(FocusSession::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
