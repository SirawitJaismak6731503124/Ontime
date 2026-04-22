package com.ontime.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.ui.theme.DarkGrey
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.HighlightWhite
import com.ontime.ui.theme.LightGrey
import com.ontime.ui.theme.WhiteText

@Composable
fun PermissionGateScreen(
    notificationsGranted: Boolean,
    onGrantNotifications: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "OnTime setup", color = WhiteText, fontSize = 24.sp)
        Text(
            text = "The app needs notification permission to show activity reminders.",
            color = LightGrey,
            fontSize = 14.sp
        )

        PermissionCard(
            title = "Notifications",
            body = if (notificationsGranted) "Granted" else "Required to show reminder notifications.",
            buttonText = if (notificationsGranted) "Granted" else "Allow notifications",
            enabled = !notificationsGranted,
            onClick = onGrantNotifications
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    body: String,
    buttonText: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGrey),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = title, color = WhiteText, fontSize = 18.sp)
            Text(text = body, color = LightGrey, fontSize = 13.sp)
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite)
            ) {
                Text(text = buttonText, color = DeepBlack)
            }
        }
    }
}