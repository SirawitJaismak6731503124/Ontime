package com.ontime.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ontime.data.model.FocusSession
import com.ontime.receiver.ReminderAlarmReceiver
import java.util.Calendar

object AlarmScheduler {

    fun scheduleReminder(context: Context, session: FocusSession) {
        val normalizedTime = TimeUtils.normalizeTimeInput(session.time) ?: return
        val triggerAtMillis = nextTriggerTimeMillis(normalizedTime)

        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(ReminderContract.EXTRA_SESSION_ID, session.id)
            putExtra(ReminderContract.EXTRA_SESSION_TITLE, session.title)
            putExtra(ReminderContract.EXTRA_SESSION_DESCRIPTION, session.description)
            putExtra(ReminderContract.EXTRA_SESSION_TIME, normalizedTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            session.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    fun cancelReminder(context: Context, sessionId: String) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun nextTriggerTimeMillis(timeText: String): Long {
        val hourMinute = timeText.split(":")
        val hour = hourMinute.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = hourMinute.getOrNull(1)?.toIntOrNull() ?: 0

        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_YEAR, 1)
        }

        return next.timeInMillis
    }
}
