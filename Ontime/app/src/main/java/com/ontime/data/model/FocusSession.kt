package com.ontime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDateTime

/**
 * Data class representing a focus session
 * @param id Unique identifier for the session
 * @param title Title of the session (e.g., "Doing Homework")
 * @param startTime Start time of the focus session
 * @param endTime End time of the focus session
 * @param blockedApps List of app package names to block during this session
 * @param reminderMessage Motivational message to show when interrupted
 * @param createdAt When this session was created
 */
@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val startTime: String, // Format: "HH:mm" or full LocalDateTime string
    val endTime: String,   // Format: "HH:mm" or full LocalDateTime string
    val blockedApps: List<String> = emptyList(),
    val reminderMessage: String = "",
    @ServerTimestamp
    val createdAt: com.google.firebase.Timestamp? = null,
    val isActive: Boolean = true
)
