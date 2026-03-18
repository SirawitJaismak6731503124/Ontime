package com.ontime.service

import android.app.PendingIntent
import android.app.Service
import android.app.UsageStatsManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ontime.data.local.OnTimeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Background service that monitors foreground app and triggers notifications
 * when blocked apps are opened during active focus sessions
 */
class FocusMonitorService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var database: OnTimeDatabase
    
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "focus_interruption"
        private const val NOTIFICATION_ID = 1001
        private const val FOREGROUND_SERVICE_ID = 1002
    }
    
    override fun onCreate() {
        super.onCreate()
        setupNotificationChannel()
        database = OnTimeDatabase.getDatabase(this)
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_SERVICE_ID, createForegroundNotification())
        startMonitoring()
        return START_STICKY
    }
    
    private fun startMonitoring() {
        serviceScope.launch {
            while (true) {
                try {
                    // Check current foreground app every 1 second
                    val currentApp = getCurrentForegroundApp()
                    
                    if (currentApp != null) {
                        // Get active sessions for current time
                        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        
                        database.focusSessionDao().getSessionsForTime(currentTime).collect { sessions ->
                            for (session in sessions) {
                                if (session.blockedApps.contains(currentApp)) {
                                    // Trigger interruption notification
                                    showInterruptionNotification(session)
                                }
                            }
                        }
                    }
                    
                    delay(1000) // Check every 1 second
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    private fun getCurrentForegroundApp(): String? {
        return try {
            val timestamp = System.currentTimeMillis()
            val stats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    timestamp - 1000,
                    timestamp
                )
            } else {
                @Suppress("DEPRECATION")
                usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST,
                    timestamp - 1000,
                    timestamp
                )
            }
            
            if (stats.isEmpty()) {
                return null
            }
            
            stats.maxByOrNull { it.lastTimeUsed }?.packageName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun showInterruptionNotification(session: com.ontime.data.model.FocusSession) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("session_id", session.id)
            putExtra("show_reminder", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            session.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Stay Focused!")
            .setContentText(session.title)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("OnTime is Running")
            .setContentText("Monitoring your focus sessions...")
            .setOngoing(true)
            .build()
    }
    
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Focus Interruption",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for blocked app interruptions"
                enableVibration(true)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext.cancel()
    }
}

import com.ontime.MainActivity
import kotlinx.coroutines.cancel
