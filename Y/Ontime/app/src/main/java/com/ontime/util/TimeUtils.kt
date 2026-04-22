package com.ontime.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.util.Locale

object TimeUtils {
    private val outputFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    private val inputFormatters = listOf(
        DateTimeFormatter.ofPattern("H:mm", Locale.US),
        DateTimeFormatter.ofPattern("HH:mm", Locale.US),
        DateTimeFormatter.ofPattern("h:mm a", Locale.US),
        DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h:mm")
            .optionalStart()
            .appendPattern(" a")
            .optionalEnd()
            .toFormatter(Locale.US)
    )

    fun normalizeTimeInput(rawValue: String): String? {
        val trimmedValue = rawValue.trim()
        if (trimmedValue.isEmpty()) return null

        return inputFormatters.firstNotNullOfOrNull { formatter ->
            try {
                val parsedTime = LocalTime.parse(trimmedValue.uppercase(Locale.US), formatter)
                parsedTime.format(outputFormatter)
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }

    fun normalizeSessionTimes(startTime: String, endTime: String): Pair<String, String>? {
        val normalizedStart = normalizeTimeInput(startTime) ?: return null
        val normalizedEnd = normalizeTimeInput(endTime) ?: return null
        return normalizedStart to normalizedEnd
    }

    fun isCurrentTimeWithinRange(currentTime: LocalTime, startTimeText: String, endTimeText: String): Boolean {
        val normalizedStart = normalizeTimeInput(startTimeText) ?: return false
        val normalizedEnd = normalizeTimeInput(endTimeText) ?: return false
        val startTime = LocalTime.parse(normalizedStart, outputFormatter)
        val endTime = LocalTime.parse(normalizedEnd, outputFormatter)

        return isCurrentTimeWithinRange(currentTime, startTime, endTime)
    }

    fun isCurrentTimeWithinRange(currentTime: LocalTime, startTime: LocalTime, endTime: LocalTime): Boolean {
        return if (startTime == endTime) {
            true
        } else if (startTime.isBefore(endTime)) {
            !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime)
        } else {
            !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime)
        }
    }
}