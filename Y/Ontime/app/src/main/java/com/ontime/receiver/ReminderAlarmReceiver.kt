package com.ontime.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ontime.DetailActivity
import com.ontime.data.model.FocusSession
import com.ontime.util.AlarmScheduler
import com.ontime.util.ReminderContract

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getStringExtra(ReminderContract.EXTRA_SESSION_ID).orEmpty()
        val title = intent.getStringExtra(ReminderContract.EXTRA_SESSION_TITLE).orEmpty()
        val description = intent.getStringExtra(ReminderContract.EXTRA_SESSION_DESCRIPTION).orEmpty()
        val time = intent.getStringExtra(ReminderContract.EXTRA_SESSION_TIME).orEmpty()

        if (sessionId.isBlank() || title.isBlank() || time.isBlank()) return

        val notificationId = sessionId.hashCode()
        val openDetailIntent = Intent(context, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(ReminderContract.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderContract.EXTRA_SESSION_TITLE, title)
            putExtra(ReminderContract.EXTRA_SESSION_DESCRIPTION, description)
            putExtra(ReminderContract.EXTRA_SESSION_TIME, time)
            putExtra(DetailActivity.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openDetailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(manager)

        val body = description.ifBlank { "Reminder for $title" }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(contentIntent)
            .build()

        manager.notify(notificationId, notification)

        AlarmScheduler.scheduleReminder(
            context,
            FocusSession(
                id = sessionId,
                title = title,
                description = description,
                time = time
            )
        )
    }

    private fun ensureChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Activity reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Scheduled activity reminder notifications"
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "activity_reminders"
    }
}
