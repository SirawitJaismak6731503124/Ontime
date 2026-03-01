package com.ontime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.ui.components.SessionCard
import com.ontime.ui.theme.Background
import com.ontime.ui.theme.PrimaryWhite
import com.ontime.viewmodel.SessionViewModel

@Composable
fun DashboardScreen(
    viewModel: SessionViewModel,
    onAddSessionClick: () -> Unit,
    onSessionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = "OnTime",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryWhite,
            modifier = Modifier.padding(top = 12.dp)
        )

        // Sessions List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(uiState.sessions) { session ->
                SessionCard(
                    session = session,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Add Session Button
        Button(
            onClick = onAddSessionClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = PrimaryWhite
            ),
            border = androidx.compose.material3.BorderStroke(2.dp, PrimaryWhite)
        ) {
            Text(
                text = "+ Add Session",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
