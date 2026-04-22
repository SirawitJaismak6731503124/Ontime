package com.ontime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.ontime.util.TimeUtils

@Composable
fun EditSessionScreen(
    session: FocusSession,
    onSave: (FocusSession) -> Unit,
    onDelete: (FocusSession) -> Unit,
    onBack: () -> Unit,
    onPickApps: () -> Unit
) {
    var title by remember(session.id) { mutableStateOf(session.title) }
    var description by remember(session.id) { mutableStateOf(session.description) }
    var startTime by remember(session.id) { mutableStateOf(session.startTime) }
    var endTime by remember(session.id) { mutableStateOf(session.endTime) }
    var reminderMessage by remember(session.id) { mutableStateOf(session.reminderMessage) }
    var validationError by remember(session.id) { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Edit session",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText
            )
            OutlinedButton(onClick = onBack) {
                Text(text = "Back")
            }
        }

        FieldCard(
            label = "TITLE",
            value = title,
            onValueChange = { title = it },
            placeholder = "e.g. Read a book"
        )

        FieldCard(
            label = "DESCRIPTION",
            value = description,
            onValueChange = { description = it },
            placeholder = "Why this activity matters",
            singleLine = false,
            height = 140.dp
        )

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
                TimeField(
                    label = "Start",
                    value = startTime,
                    onValueChange = { startTime = it },
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "–",
                    fontSize = 24.sp,
                    color = WhiteText,
                    modifier = Modifier.weight(0.1f),
                    textAlign = TextAlign.Center
                )
                TimeField(
                    label = "End",
                    value = endTime,
                    onValueChange = { endTime = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SELECTED APPS",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = LightGrey
            )
            if (session.blockedApps.isEmpty()) {
                Text(text = "No apps selected yet", fontSize = 14.sp, color = LightGrey)
            } else {
                session.blockedApps.forEach { appPackage ->
                    Text(
                        text = appPackage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceGrey)
                            .padding(12.dp),
                        color = WhiteText,
                        fontSize = 14.sp
                    )
                }
            }

            Button(
                onClick = onPickApps,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite)
            ) {
                Text(text = "Choose apps", color = DeepBlack, fontWeight = FontWeight.Bold)
            }
        }

        FieldCard(
            label = "NOTIFICATION MESSAGE",
            value = reminderMessage,
            onValueChange = { reminderMessage = it },
            placeholder = "Short notification text",
            singleLine = false,
            height = 120.dp
        )

        validationError?.let {
            Text(text = it, color = ErrorRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val normalizedTimes = TimeUtils.normalizeSessionTimes(startTime, endTime)
                    when {
                        title.isBlank() -> validationError = "Please enter a title."
                        description.isBlank() -> validationError = "Please enter a description."
                        normalizedTimes == null -> validationError = "Use a valid time format like 09:00 or 9:00 AM."
                        session.blockedApps.isEmpty() -> validationError = "Select at least one app."
                        else -> {
                            validationError = null
                            onSave(
                                session.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    startTime = normalizedTimes.first,
                                    endTime = normalizedTimes.second,
                                    reminderMessage = reminderMessage.trim()
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite)
            ) {
                Text(text = "Save", color = DeepBlack, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onDelete(session) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text(text = "Delete", color = WhiteText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FieldCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    height: Dp = 56.dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (label.isNotBlank()) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LightGrey)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            shape = RoundedCornerShape(14.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(12.dp),
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(color = WhiteText),
                singleLine = singleLine,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(text = placeholder, fontSize = 14.sp, color = LightGrey)
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun TimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LightGrey)
        FieldCard(
            label = "",
            value = value,
            onValueChange = onValueChange,
            placeholder = "09:00",
            height = 56.dp
        )
    }
}