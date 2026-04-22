package com.ontime.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.UsageEvents
import android.app.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ontime.DetailActivity
import com.ontime.data.local.OnTimeDatabase
import com.ontime.util.NotificationDebouncer
import com.ontime.util.PermissionUtils
import com.ontime.util.ReminderContract
import com.ontime.util.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime

class FocusMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var database: OnTimeDatabase
    private lateinit var debouncer: NotificationDebouncer

    private var monitoringJob: Job? = null

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "focus_interruption"
        private const val FOREGROUND_NOTIFICATION_ID = 1002
        private const val POLLING_INTERVAL_MS = 1_500L
    }

    override fun onCreate() {
        super.onCreate()
        database = OnTimeDatabase.getDatabase(this)
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        debouncer = NotificationDebouncer(this)
        setupNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!PermissionUtils.hasUsageStatsPermission(this) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !PermissionUtils.hasNotificationPermission(this))
        ) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())
        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        if (monitoringJob?.isActive == true) return

        monitoringJob = serviceScope.launch {
            while (isActive) {
                try {
                    val currentPackage = getCurrentForegroundAppPackage()
                    if (!currentPackage.isNullOrBlank()) {
                        val currentTime = LocalTime.now()
                        val sessions = database.focusSessionDao().getActiveSessionsOnce()

                        sessions.forEach { session ->
                            if (TimeUtils.isCurrentTimeWithinRange(currentTime, session.startTime, session.endTime) &&
                                session.blockedApps.contains(currentPackage)
                            ) {
                                maybeNotify(session, currentPackage)
                            }
                        }
                    }

                    delay(POLLING_INTERVAL_MS)
                } catch (_: Exception) {
                    delay(POLLING_INTERVAL_MS)
                }
            }
        }
    }

    private fun getCurrentForegroundAppPackage(): String? {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 2000L
        val events = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        var foregroundPackage: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && event.eventType == UsageEvents.Event.ACTIVITY_RESUMED)
            ) {
                foregroundPackage = event.packageName
            }
        }

        return foregroundPackage
    }

    private fun maybeNotify(session: com.ontime.data.model.FocusSession, packageName: String) {
        if (!debouncer.shouldTrigger(session.id, packageName)) return

        val notificationId = (session.id + packageName).hashCode()
        notificationManager.notify(notificationId, buildInterruptionNotification(session, packageName, notificationId))
    }

    private fun buildInterruptionNotification(
        session: com.ontime.data.model.FocusSession,
        packageName: String,
        notificationId: Int
    ): Notification {
        val detailIntent = Intent(this, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(ReminderContract.EXTRA_SESSION_ID, session.id)
            putExtra(ReminderContract.EXTRA_SESSION_TITLE, session.title)
            putExtra(ReminderContract.EXTRA_SESSION_DESCRIPTION, session.description)
            putExtra(ReminderContract.EXTRA_SESSION_START_TIME, session.startTime)
            putExtra(ReminderContract.EXTRA_SESSION_END_TIME, session.endTime)
            putExtra(ReminderContract.EXTRA_TRIGGER_PACKAGE, packageName)
            putExtra(ReminderContract.EXTRA_BLOCKED_APPS, session.blockedApps.toTypedArray())
            putExtra(DetailActivity.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val contentIntent = PendingIntent.getActivity(
            this,
            notificationId,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val shortMessage = session.reminderMessage.ifBlank {
            "Time to focus on ${session.title.lowercase()}"
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(session.title)
            .setContentText(shortMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(session.description.ifBlank { shortMessage }))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("OnTime is running")
            .setContentText("Monitoring selected apps")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Focus reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications shown when a selected app opens during a scheduled session"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        monitoringJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}