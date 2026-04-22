package com.ontime.util

import android.content.Context

class NotificationDebouncer(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun shouldTrigger(sessionId: String, packageName: String, minimumIntervalMs: Long = DEFAULT_INTERVAL_MS): Boolean {
        val triggerKey = buildKey(sessionId, packageName)
        val lastKey = preferences.getString(KEY_LAST_TRIGGER_KEY, null)
        val lastTriggerAt = preferences.getLong(KEY_LAST_TRIGGER_AT, 0L)
        val now = System.currentTimeMillis()

        if (lastKey != triggerKey) {
            persist(triggerKey, now)
            return true
        }

        if (now - lastTriggerAt >= minimumIntervalMs) {
            persist(triggerKey, now)
            return true
        }

        return false
    }

    private fun persist(triggerKey: String, timestamp: Long) {
        preferences.edit()
            .putString(KEY_LAST_TRIGGER_KEY, triggerKey)
            .putLong(KEY_LAST_TRIGGER_AT, timestamp)
            .apply()
    }

    private fun buildKey(sessionId: String, packageName: String): String {
        return "$sessionId:$packageName"
    }

    companion object {
        private const val PREFS_NAME = "notification_debouncer"
        private const val KEY_LAST_TRIGGER_KEY = "last_trigger_key"
        private const val KEY_LAST_TRIGGER_AT = "last_trigger_at"
        private const val DEFAULT_INTERVAL_MS = 3 * 60 * 1000L
    }
}