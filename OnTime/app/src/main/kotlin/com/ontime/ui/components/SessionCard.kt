package com.ontime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.data.FocusSession
import com.ontime.ui.theme.CardSurface
import com.ontime.ui.theme.LightGray
import com.ontime.ui.theme.PrimaryWhite
import com.ontime.ui.theme.SecondaryGray

@Composable
fun SessionCard(
    session: FocusSession,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // FOCUS SESSION label
            Text(
                text = "FOCUS SESSION",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = SecondaryGray,
                letterSpacing = 1.5.sp
            )

            // Time Range
            Text(
                text = "${session.startTime} – ${session.endTime}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryWhite
            )

            // Task Title
            Text(
                text = session.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = LightGray
            )

            // Blocked Apps
            if (session.blockedApps.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(session.blockedApps) { app ->
                        AppChip(appName = app)
                    }
                }
            }
        }
    }
}

@Composable
fun AppChip(
    appName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = appName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryWhite,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
