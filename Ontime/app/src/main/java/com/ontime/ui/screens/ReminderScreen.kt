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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.data.model.FocusSession
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.HighlightWhite
import com.ontime.ui.theme.WhiteText

/**
 * Full-screen reminder view shown when user opens the app from notification
 * Displays motivational message and blocked app information
 */
@Composable
fun ReminderScreen(
    session: FocusSession,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stay Focused!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = HighlightWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Blocked App Warning
            Column(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You tried to open:",
                    fontSize = 14.sp,
                    color = com.ontime.ui.theme.LightGrey,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = session.blockedApps.joinToString(", "),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
            }
        }
        
        // Reminder Message
        Text(
            text = session.reminderMessage,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier
                .weight(2f)
                .padding(vertical = 32.dp)
        )
        
        // Session Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Focus Session: ${session.title}",
                fontSize = 14.sp,
                color = com.ontime.ui.theme.LightGrey,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "${session.startTime} - ${session.endTime}",
                fontSize = 12.sp,
                color = com.ontime.ui.theme.LightGrey
            )
        }
        
        // Dismiss Button
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Got it! Let's focus",
                fontWeight = FontWeight.Bold,
                color = DeepBlack,
                fontSize = 16.sp
            )
        }
    }
}

import androidx.compose.ui.unit.dp as dpUnit
