package com.ontime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp

/**
 * Data class representing a scheduled reminder activity.
 */
@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val description: String = "",
    val time: String, // Format: "HH:mm"
    @ServerTimestamp
    val createdAt: com.google.firebase.Timestamp? = null,
    val isActive: Boolean = true
)
