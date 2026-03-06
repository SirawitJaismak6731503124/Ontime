package com.ontime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ontime.data.local.FocusSessionDao
import com.ontime.data.model.FocusSession
import com.ontime.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing focus sessions
 */
class FocusSessionViewModel(
    private val dao: FocusSessionDao,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    
    private val _allSessions = MutableStateFlow<List<FocusSession>>(emptyList())
    val allSessions: StateFlow<List<FocusSession>> = _allSessions.asStateFlow()
    
    private val _currentSession = MutableStateFlow<FocusSession?>(null)
    val currentSession: StateFlow<FocusSession?> = _currentSession.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()
    
    init {
        loadAllSessions()
    }
    
    private fun loadAllSessions() {
        viewModelScope.launch {
            dao.getAllSessions().collect { sessions ->
                _allSessions.value = sessions
            }
        }
    }
    
    fun createSession(
        title: String,
        startTime: String,
        endTime: String,
        blockedApps: List<String>,
        reminderMessage: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = FocusSession(
                    title = title,
                    startTime = startTime,
                    endTime = endTime,
                    blockedApps = blockedApps,
                    reminderMessage = reminderMessage
                )
                dao.insert(session)
                // Sync to Firebase
                firebaseRepository.syncSessionToFirebase(session)
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Error creating session: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateSession(session: FocusSession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                dao.update(session)
                firebaseRepository.syncSessionToFirebase(session)
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Error updating session: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteSession(session: FocusSession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                dao.delete(session)
                firebaseRepository.deleteSessionFromFirebase(session.id)
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting session: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setCurrentSession(session: FocusSession) {
        _currentSession.value = session
    }
    
    fun clearCurrentSession() {
        _currentSession.value = null
    }
}

class FocusSessionViewModelFactory(
    private val dao: FocusSessionDao,
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocusSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FocusSessionViewModel(dao, firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
