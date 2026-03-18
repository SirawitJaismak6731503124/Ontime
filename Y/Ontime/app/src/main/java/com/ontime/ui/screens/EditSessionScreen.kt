package com.ontime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.data.model.FocusSession
import com.ontime.ui.theme.DarkGrey
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.ErrorRed
import com.ontime.ui.theme.HighlightWhite
import com.ontime.ui.theme.LightGrey
import com.ontime.ui.theme.SurfaceGrey
import com.ontime.ui.theme.WhiteText

/**
 * Edit Session Screen - interface for creating or editing a focus session
 */
@Composable
fun EditSessionScreen(
    session: FocusSession? = null,
    onSave: (FocusSession) -> Unit,
    onDelete: (FocusSession) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(session?.title ?: "") }
    var startTime by remember { mutableStateOf(session?.startTime ?: "") }
    var endTime by remember { mutableStateOf(session?.endTime ?: "") }
    var reminderMessage by remember { mutableStateOf(session?.reminderMessage ?: "") }
    val blockedApps = remember { mutableStateListOf(*session?.blockedApps?.toTypedArray() ?: emptyArray()) }
    var newAppName by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = WhiteText,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceGrey)
                    .padding(8.dp)
                    .weight(0.1f)
            )
            Text(
                text = "EDIT SESSION",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText,
                modifier = Modifier
                    .weight(0.9f)
                    .padding(start = 16.dp)
            )
        }
        
        // Time Block Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkGrey),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                text_input(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = "Start",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "–",
                    fontSize = 24.sp,
                    color = WhiteText,
                    modifier = Modifier.weight(0.1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                text_input(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = "End",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Title Section
        Text(
            text = "TITLE",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = LightGrey
        )
        input_field(
            value = title,
            onValueChange = { title = it },
            placeholder = "e.g., Doing Homework",
            modifier = Modifier.fillMaxWidth()
        )
        
        // Blocked Apps Section
        Text(
            text = "BLOCKED APPS",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = LightGrey
        )
        
        blockedApps.forEachIndexed { index, app ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceGrey)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = app,
                    fontSize = 14.sp,
                    color = WhiteText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "✕",
                    fontSize = 20.sp,
                    color = ErrorRed,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Transparent)
                        .padding(4.dp)
                )
            }
        }
        
        // Add App Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceGrey)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            input_field(
                value = newAppName,
                onValueChange = { newAppName = it },
                placeholder = "Add app name...",
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "+",
                fontSize = 20.sp,
                color = WhiteText,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Reminder Message Section
        Text(
            text = "REMINDER MESSAGE",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = LightGrey
        )
        input_field(
            value = reminderMessage,
            onValueChange = { reminderMessage = it },
            placeholder = "Your motivational message...",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            singleLine = false
        )
        
        // Bottom Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val updatedSession = FocusSession(
                        id = session?.id ?: System.currentTimeMillis().toString(),
                        title = title,
                        startTime = startTime,
                        endTime = endTime,
                        blockedApps = blockedApps.toList(),
                        reminderMessage = reminderMessage
                    )
                    onSave(updatedSession)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "SAVE SESSION",
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
            }
            
            if (session != null) {
                Button(
                    onClick = { onDelete(session) },
                    modifier = Modifier
                        .weight(0.5f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "DELETE",
                        fontWeight = FontWeight.Bold,
                        color = WhiteText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun input_field(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        shape = RoundedCornerShape(12.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(color = WhiteText),
            singleLine = singleLine,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = LightGrey
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun text_input(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = LightGrey,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        input_field(
            value = value,
            onValueChange = onValueChange,
            placeholder = "00:00 AM",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

import androidx.compose.ui.text.style.TextAlign
