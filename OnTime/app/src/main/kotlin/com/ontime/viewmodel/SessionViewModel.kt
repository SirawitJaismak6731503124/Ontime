package com.ontime.viewmodel

import androidx.lifecycle.ViewModel
import com.ontime.data.FocusSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime

data class SessionUiState(
    val sessions: List<FocusSession> = emptyList(),
    val editingSession: FocusSession? = null,
    val isAddingNewSession: Boolean = false
)

class SessionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    // Initialize with sample data
    init {
        _uiState.value = SessionUiState(
            sessions = listOf(
                FocusSession(
                    id = "1",
                    title = "Doing Homework",
                    startTime = LocalTime.of(17, 0),
                    endTime = LocalTime.of(19, 0),
                    blockedApps = listOf("TikTok", "Instagram"),
                    reminderMessage = ""
                )
            )
        )
    }

    fun startEditingSession(session: FocusSession) {
        _uiState.update { it.copy(editingSession = session) }
    }

    fun startAddingNewSession() {
        _uiState.update { it.copy(editingSession = FocusSession(), isAddingNewSession = true) }
    }

    fun updateEditingSession(session: FocusSession) {
        _uiState.update { it.copy(editingSession = session) }
    }

    fun saveSession(session: FocusSession) {
        _uiState.update { state ->
            val updatedSessions = if (state.sessions.any { it.id == session.id }) {
                state.sessions.map { if (it.id == session.id) session else it }
            } else {
                state.sessions + session
            }
            state.copy(
                sessions = updatedSessions,
                editingSession = null,
                isAddingNewSession = false
            )
        }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(editingSession = null, isAddingNewSession = false) }
    }

    fun deleteSession(sessionId: String) {
        _uiState.update { state ->
            state.copy(sessions = state.sessions.filter { it.id != sessionId })
        }
    }

    fun addBlockedApp(app: String) {
        val current = _uiState.value.editingSession ?: return
        val updatedSession = current.copy(
            blockedApps = current.blockedApps + app
        )
        updateEditingSession(updatedSession)
    }

    fun removeBlockedApp(app: String) {
        val current = _uiState.value.editingSession ?: return
        val updatedSession = current.copy(
            blockedApps = current.blockedApps.filter { it != app }
        )
        updateEditingSession(updatedSession)
    }
}
