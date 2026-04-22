package com.ontime

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.ui.theme.DarkGrey
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.HighlightWhite
import com.ontime.ui.theme.LightGrey
import com.ontime.ui.theme.OnTimeTheme
import com.ontime.ui.theme.WhiteText
import com.ontime.util.ReminderContract

class DetailActivity : ComponentActivity() {

    private var reminderPayload by mutableStateOf(ReminderPayload.empty())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderPayload = parseIntent(intent)
        cancelPostedNotification(reminderPayload.notificationId)

        setContent {
            OnTimeTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepBlack)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = reminderPayload.title, color = WhiteText, fontSize = 30.sp)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkGrey),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = reminderPayload.description.ifBlank { "No description was provided." },
                            modifier = Modifier.padding(18.dp),
                            color = WhiteText,
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = "Triggered by: ${reminderPayload.triggerPackage.ifBlank { "unknown app" }}",
                        color = LightGrey,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Schedule: ${reminderPayload.startTime} - ${reminderPayload.endTime}",
                        color = LightGrey,
                        fontSize = 13.sp
                    )

                    if (reminderPayload.blockedApps.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkGrey),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "Selected apps", color = WhiteText, fontSize = 16.sp)
                                reminderPayload.blockedApps.forEach { app ->
                                    Text(text = app, color = LightGrey, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { finish() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite)
                    ) {
                        Text(text = "Close", color = DeepBlack)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        reminderPayload = parseIntent(intent)
        cancelPostedNotification(reminderPayload.notificationId)
    }

    private fun parseIntent(intent: Intent): ReminderPayload {
        return ReminderPayload(
            sessionId = intent.getStringExtra(ReminderContract.EXTRA_SESSION_ID).orEmpty(),
            title = intent.getStringExtra(ReminderContract.EXTRA_SESSION_TITLE).orEmpty(),
            description = intent.getStringExtra(ReminderContract.EXTRA_SESSION_DESCRIPTION).orEmpty(),
            startTime = intent.getStringExtra(ReminderContract.EXTRA_SESSION_START_TIME).orEmpty(),
            endTime = intent.getStringExtra(ReminderContract.EXTRA_SESSION_END_TIME).orEmpty(),
            triggerPackage = intent.getStringExtra(ReminderContract.EXTRA_TRIGGER_PACKAGE).orEmpty(),
            blockedApps = intent.getStringArrayExtra(ReminderContract.EXTRA_BLOCKED_APPS)?.toList().orEmpty(),
            notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        )
    }

    private fun cancelPostedNotification(notificationId: Int) {
        if (notificationId == -1) return
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    data class ReminderPayload(
        val sessionId: String,
        val title: String,
        val description: String,
        val startTime: String,
        val endTime: String,
        val triggerPackage: String,
        val blockedApps: List<String>,
        val notificationId: Int
    ) {
        companion object {
            fun empty(): ReminderPayload = ReminderPayload("", "Activity reminder", "", "", "", "", emptyList(), -1)
        }
    }

    companion object {
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}