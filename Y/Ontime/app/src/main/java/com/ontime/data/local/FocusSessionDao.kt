package com.ontime.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ontime.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for FocusSession entities
 */
@Dao
interface FocusSessionDao {
    
    @Insert
    suspend fun insert(session: FocusSession): Long
    
    @Update
    suspend fun update(session: FocusSession)
    
    @Delete
    suspend fun delete(session: FocusSession)
    
    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): FocusSession?
    
    @Query("SELECT * FROM focus_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<FocusSession>>
    
    @Query("SELECT * FROM focus_sessions WHERE isActive = 1")
    fun getActiveSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE isActive = 1")
    suspend fun getActiveSessionsOnce(): List<FocusSession>
    
    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()
    
    @Query("SELECT * FROM focus_sessions WHERE startTime <= :currentTime AND endTime >= :currentTime")
    fun getSessionsForTime(currentTime: String): Flow<List<FocusSession>>
}
