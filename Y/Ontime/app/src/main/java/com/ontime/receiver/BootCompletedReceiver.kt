package com.ontime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ontime.data.local.OnTimeDatabase
import com.ontime.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            val sessions = OnTimeDatabase.getDatabase(context).focusSessionDao().getActiveSessionsOnce()
            sessions.forEach { session ->
                AlarmScheduler.scheduleReminder(context, session)
            }
        }
    }
}
