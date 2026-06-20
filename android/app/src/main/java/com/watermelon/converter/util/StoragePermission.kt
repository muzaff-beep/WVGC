// Watermelon Vector Converter
// Copyright (c) 2026 Suhail Muzaffari. All rights reserved.
// Proprietary and source-available. Reuse prohibited without written permission.
// See LICENSE for terms.

package com.watermelon.converter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

/**
 * MANAGE_EXTERNAL_STORAGE ("All files access") has no normal runtime dialog.
 * The user must be sent to a dedicated system Settings screen to grant it.
 * This object checks status and builds that intent.
 */
object StoragePermission {

    fun isGranted(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // pre-R: legacy storage permissions, not handled here
        }

    /** Intent to the per-app "All files access" settings screen. */
    fun requestIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:" + context.packageName)
        return intent
    }
}