package com.ontime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.data.model.FocusSession
import com.ontime.ui.theme.DarkGrey
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.HighlightWhite
import com.ontime.ui.theme.LightGrey
import com.ontime.ui.theme.WhiteText

/**
 * Dashboard Screen - Main list view of all focus sessions
 */
@Composable
fun DashboardScreen(
    sessions: List<FocusSession>,
    onAddSessionClick: () -> Unit,
    onSessionClick: (FocusSession) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Header
            Text(
                text = "OnTime",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            if (sessions.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No sessions yet",
                        fontSize = 18.sp,
                        color = LightGrey,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Create your first focus session",
                        fontSize = 14.sp,
                        color = LightGrey,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                // Sessions List
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sessions) { session ->
                        SessionCard(
                            session = session,
                            onClick = { onSessionClick(session) }
                        )
                    }
                }
            }
        }
        
        // Add Session Button
        Button(
            onClick = onAddSessionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "+ Add Session",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = WhiteText
            )
        }
    }
}

/**
 * Individual session card component
 */
@Composable
fun SessionCard(
    session: FocusSession,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = DarkGrey
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with session label and time
            Text(
                text = "FOCUS SESSION",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = LightGrey,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Time Range
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.startTime,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
                Text(
                    text = " – ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = session.endTime,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
            }
            
            // Title
            Text(
                text = session.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = WhiteText,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Blocked Apps Chips
            if (session.blockedApps.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    session.blockedApps.take(2).forEach { app ->
                        AppChip(app)
                    }
                    if (session.blockedApps.size > 2) {
                        Text(
                            text = "+${session.blockedApps.size - 2}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LightGrey
                        )
                    }
                }
            }
        }
    }
}

/**
 * App chip component for displaying blocked app names
 */
@Composable
fun AppChip(appName: String) {
    Text(
        text = appName,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF333333))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = LightGrey
    )
}

import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.unit.dp as dpUnit
import androidx.compose.foundation.layout.height
