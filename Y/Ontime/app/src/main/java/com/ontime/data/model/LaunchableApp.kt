package com.ontime.data.model

import android.graphics.drawable.Drawable

data class LaunchableApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable
)