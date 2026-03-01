package com.ontime.data

import java.time.LocalTime
import java.util.UUID

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(10, 0),
    val blockedApps: List<String> = emptyList(),
    val reminderMessage: String = ""
)
