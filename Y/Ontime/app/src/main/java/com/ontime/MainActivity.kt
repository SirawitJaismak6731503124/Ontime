package com.ontime

import android.Manifest
import android.content.Intent
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ontime.data.local.InstalledAppRepository
import com.ontime.data.local.OnTimeDatabase
import com.ontime.data.model.FocusSession
import com.ontime.data.model.LaunchableApp
import com.ontime.data.remote.FirebaseRepository
import com.ontime.service.FocusMonitorService
import com.ontime.ui.screens.AppPickerScreen
import com.ontime.ui.screens.DashboardScreen
import com.ontime.ui.screens.EditSessionScreen
import com.ontime.ui.screens.PermissionGateScreen
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.OnTimeTheme
import com.ontime.util.PermissionUtils
import com.ontime.viewmodel.FocusSessionViewModel
import com.ontime.viewmodel.FocusSessionViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FocusSessionViewModel
    private lateinit var installedAppRepository: InstalledAppRepository

    private var hasUsageAccess by mutableStateOf(false)
    private var hasNotificationPermission by mutableStateOf(false)
    private var isPickingApps by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = OnTimeDatabase.getDatabase(this)
        val firebaseRepository = FirebaseRepository()
        val factory = FocusSessionViewModelFactory(
            database.focusSessionDao(),
            firebaseRepository
        )
        viewModel = ViewModelProvider(this, factory)[FocusSessionViewModel::class.java]
        installedAppRepository = InstalledAppRepository(this)

        refreshPermissionState()
        updateMonitoringServiceState()

        setContent {
            OnTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    val allSessions by viewModel.allSessions.collectAsState()
                    val currentSession by viewModel.currentSession.collectAsState()
                    val installedApps by produceState(initialValue = emptyList<LaunchableApp>(), installedAppRepository) {
                        value = installedAppRepository.getLaunchableApps()
                    }
                    val notificationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        hasNotificationPermission = granted
                        updateMonitoringServiceState()
                    }

                    when {
                        !hasUsageAccess || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) -> {
                            PermissionGateScreen(
                                usageAccessGranted = hasUsageAccess,
                                notificationsGranted = hasNotificationPermission,
                                onGrantUsageAccess = { PermissionUtils.openUsageAccessSettings(this@MainActivity) },
                                onGrantNotifications = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        hasNotificationPermission = true
                                        updateMonitoringServiceState()
                                    }
                                },
                                onOpenBatteryOptimization = { PermissionUtils.openBatteryOptimizationSettings(this@MainActivity) }
                            )
                        }

                        isPickingApps && currentSession != null -> {
                            AppPickerScreen(
                                apps = installedApps,
                                selectedPackages = currentSession.blockedApps,
                                onBack = { isPickingApps = false },
                                onConfirm = { selectedPackages ->
                                    viewModel.updateCurrentSessionBlockedApps(selectedPackages.toList())
                                    isPickingApps = false
                                }
                            )
                        }

                        currentSession != null -> {
                            EditSessionScreen(
                                session = currentSession,
                                onSave = { session ->
                                    viewModel.saveSession(session)
                                    viewModel.clearCurrentSession()
                                    isPickingApps = false
                                    updateMonitoringServiceState()
                                },
                                onDelete = { session ->
                                    viewModel.deleteSession(session)
                                    viewModel.clearCurrentSession()
                                    isPickingApps = false
                                },
                                onBack = {
                                    viewModel.clearCurrentSession()
                                    isPickingApps = false
                                },
                                onPickApps = { isPickingApps = true }
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
                                            startTime = "",
                                            endTime = ""
                                        )
                                    )
                                    isPickingApps = false
                                },
                                onSessionClick = { session ->
                                    viewModel.setCurrentSession(session)
                                    isPickingApps = false
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
        updateMonitoringServiceState()
    }

    private fun refreshPermissionState() {
        hasUsageAccess = PermissionUtils.hasUsageStatsPermission(this)
        hasNotificationPermission = PermissionUtils.hasNotificationPermission(this)
    }

    private fun updateMonitoringServiceState() {
        val shouldRunService = hasUsageAccess && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission)
        val serviceIntent = Intent(this, FocusMonitorService::class.java)

        if (shouldRunService) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                @Suppress("DEPRECATION")
                startService(serviceIntent)
            }
        } else {
            stopService(serviceIntent)
        }
    }
}