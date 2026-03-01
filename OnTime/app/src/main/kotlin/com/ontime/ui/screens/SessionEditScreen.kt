package com.ontime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.data.FocusSession
import com.ontime.ui.components.TimeBlockInput
import com.ontime.ui.theme.Background
import com.ontime.ui.theme.CardSurface
import com.ontime.ui.theme.LightGray
import com.ontime.ui.theme.PrimaryWhite
import com.ontime.ui.theme.SecondaryGray
import com.ontime.viewmodel.SessionViewModel

@Composable
fun SessionEditScreen(
    viewModel: SessionViewModel,
    onBackClick: () -> Unit,
    isNewSession: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val session = uiState.editingSession ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryWhite,
                    modifier = Modifier.padding(0.dp)
                )
            }

            Text(
                text = if (isNewSession) "NEW SESSION" else "EDIT SESSION",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryWhite,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Spacer to balance layout
            Spacer(modifier = Modifier.weight(0.12f))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // TIME BLOCK Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TIME BLOCK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryGray,
                        letterSpacing = 1.5.sp
                    )
                    TimeBlockInput(
                        startTime = session.startTime,
                        endTime = session.endTime
                    )
                }
            }

            // TITLE Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TITLE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryGray,
                        letterSpacing = 1.5.sp
                    )
                    TextField(
                        value = session.title,
                        onValueChange = { newTitle ->
                            viewModel.updateEditingSession(session.copy(title = newTitle))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        placeholder = {
                            Text(
                                "e.g., Deep Work Session",
                                color = LightGray,
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = CardSurface,
                            focusedTextColor = PrimaryWhite,
                            unfocusedTextColor = PrimaryWhite,
                            cursorColor = PrimaryWhite,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                            fontSize = 14.sp
                        )
                    )
                }
            }

            // BLOCKED APPS Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "BLOCKED APPS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryGray,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Blocked Apps Items
            items(session.blockedApps) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app,
                        color = PrimaryWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = { viewModel.removeBlockedApp(app) },
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove app",
                            tint = PrimaryWhite
                        )
                    }
                }
            }

            // Add App Button
            item {
                Button(
                    onClick = { /* Handle add app action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = PrimaryWhite
                    ),
                    border = androidx.compose.material3.BorderStroke(1.dp, SecondaryGray)
                ) {
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // REMINDER MESSAGE Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "REMINDER MESSAGE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryGray,
                        letterSpacing = 1.5.sp
                    )
                    TextField(
                        value = session.reminderMessage,
                        onValueChange = { newMessage ->
                            viewModel.updateEditingSession(session.copy(reminderMessage = newMessage))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                "Write your personal motivation here...",
                                color = LightGray,
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = CardSurface,
                            focusedTextColor = PrimaryWhite,
                            unfocusedTextColor = PrimaryWhite,
                            cursorColor = PrimaryWhite,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // Create/Save Button
        Button(
            onClick = {
                viewModel.saveSession(session)
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryWhite)
        ) {
            Text(
                text = if (isNewSession) "CREATE SESSION" else "SAVE SESSION",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Background
            )
        }
    }
}
