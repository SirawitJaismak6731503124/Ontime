package com.ontime

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ontime.data.local.OnTimeDatabase
import com.ontime.data.remote.FirebaseRepository
import com.ontime.service.FocusMonitorService
import com.ontime.ui.screens.DashboardScreen
import com.ontime.ui.screens.EditSessionScreen
import com.ontime.ui.screens.ReminderScreen
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.OnTimeTheme
import com.ontime.viewmodel.FocusSessionViewModel
import com.ontime.viewmodel.FocusSessionViewModelFactory

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: FocusSessionViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Database and ViewModels
        val database = OnTimeDatabase.getDatabase(this)
        val firebaseRepository = FirebaseRepository()
        val factory = FocusSessionViewModelFactory(
            database.focusSessionDao(),
            firebaseRepository
        )
        viewModel = ViewModelProvider(this, factory).get(FocusSessionViewModel::class.java)
        
        // Start the focus monitor service
        startFocusMonitorService()
        
        setContent {
            OnTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    // State management
                    val allSessions by viewModel.allSessions.collectAsState()
                    val currentSession by viewModel.currentSession.collectAsState()
                    var showReminderScreen = false
                    
                    // Check if we should show reminder screen
                    val sessionIdFromNotification = intent.getStringExtra("session_id")
                    val showReminder = intent.getBooleanExtra("show_reminder", false)
                    
                    if (showReminder && sessionIdFromNotification != null) {
                        // Save that we're showing reminder to prevent re-showing
                        intent.removeExtra("show_reminder")
                        
                        allSessions.find { it.id == sessionIdFromNotification }?.let { session ->
                            ReminderScreen(
                                session = session,
                                onDismiss = {
                                    viewModel.clearCurrentSession()
                                    showReminderScreen = false
                                }
                            )
                        }
                    } else if (currentSession != null) {
                        // Show Edit Screen
                        EditSessionScreen(
                            session = currentSession,
                            onSave = { session ->
                                viewModel.updateSession(session)
                                viewModel.clearCurrentSession()
                            },
                            onDelete = { session ->
                                viewModel.deleteSession(session)
                                viewModel.clearCurrentSession()
                            },
                            onBack = {
                                viewModel.clearCurrentSession()
                            }
                        )
                    } else {
                        // Show Dashboard
                        DashboardScreen(
                            sessions = allSessions,
                            onAddSessionClick = {
                                // Start with new empty session
                                viewModel.setCurrentSession(
                                    com.ontime.data.model.FocusSession(
                                        title = "",
                                        startTime = "",
                                        endTime = ""
                                    )
                                )
                            },
                            onSessionClick = { session ->
                                viewModel.setCurrentSession(session)
                            }
                        )
                    }
                }
            }
        }
    }
    
    private fun startFocusMonitorService() {
        val serviceIntent = Intent(this, FocusMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            @Suppress("DEPRECATION")
            startService(serviceIntent)
        }
    }
}
