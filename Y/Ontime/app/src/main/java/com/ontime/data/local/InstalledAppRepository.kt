package com.ontime.data.local

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.ontime.data.model.LaunchableApp

class InstalledAppRepository(private val context: Context) {

    fun getLaunchableApps(): List<LaunchableApp> {
        val packageManager = context.packageManager
        val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        @Suppress("DEPRECATION")
        return packageManager.queryIntentActivities(launchIntent, 0)
            .map { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo
                LaunchableApp(
                    packageName = activityInfo.packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
    }
}