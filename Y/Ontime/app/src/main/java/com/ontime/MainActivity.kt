package com.ontime

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ontime.data.local.OnTimeDatabase
import com.ontime.data.model.FocusSession
import com.ontime.ui.screens.DashboardScreen
import com.ontime.ui.screens.EditSessionScreen
import com.ontime.ui.screens.PermissionGateScreen
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.OnTimeTheme
import com.ontime.util.AlarmScheduler
import com.ontime.util.PermissionUtils
import com.ontime.viewmodel.FocusSessionViewModel
import com.ontime.viewmodel.FocusSessionViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FocusSessionViewModel

    private var hasNotificationPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = OnTimeDatabase.getDatabase(this)
        val factory = FocusSessionViewModelFactory(database.focusSessionDao())
        viewModel = ViewModelProvider(this, factory)[FocusSessionViewModel::class.java]

        refreshPermissionState()

        setContent {
            OnTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    val allSessions by viewModel.allSessions.collectAsState()
                    val currentSession by viewModel.currentSession.collectAsState()
                    val notificationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        hasNotificationPermission = granted
                    }

                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission -> {
                            PermissionGateScreen(
                                notificationsGranted = hasNotificationPermission,
                                onGrantNotifications = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        hasNotificationPermission = true
                                    }
                                }
                            )
                        }

                        currentSession != null -> {
                            EditSessionScreen(
                                session = currentSession,
                                onSave = { session ->
                                    viewModel.saveSession(session)
                                    AlarmScheduler.scheduleReminder(this@MainActivity, session)
                                    viewModel.clearCurrentSession()
                                },
                                onDelete = { session ->
                                    viewModel.deleteSession(session)
                                    AlarmScheduler.cancelReminder(this@MainActivity, session.id)
                                    viewModel.clearCurrentSession()
                                },
                                onBack = {
                                    viewModel.clearCurrentSession()
                                }
                            )
                        }

                        else -> {
                            DashboardScreen(
                                sessions = allSessions,
                                onAddSessionClick = {
                                    viewModel.setCurrentSession(
                                        FocusSession(
                                            title = "",
                                            description = "",
                                            time = ""
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
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionState()
    }

    private fun refreshPermissionState() {
        hasNotificationPermission = PermissionUtils.hasNotificationPermission(this)
    }
}