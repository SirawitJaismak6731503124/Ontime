package com.ontime.util

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
                val parsedTime = java.time.LocalTime.parse(trimmedValue.uppercase(Locale.US), formatter)
                parsedTime.format(outputFormatter)
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }
}