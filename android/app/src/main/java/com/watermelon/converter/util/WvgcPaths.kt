// Watermelon Vector Converter
// Copyright (c) 2026 Suhail Muzaffari. All rights reserved.
// Proprietary and source-available. Reuse prohibited without written permission.
// See LICENSE for terms.

package com.watermelon.converter.util

import android.os.Environment
import java.io.File

/**
 * Unified output locations. Regardless of how a conversion happens (single
 * import, ZIP batch, or a marked-files custom batch from the file manager),
 * results land in one of these two well-known folders so the user always
 * knows where to find converted files.
 */
object WvgcPaths {

    private val root: File
        get() = File(Environment.getExternalStorageDirectory(), "Watermelon/VectorConverter")

    val batchFilesDir: File get() = File(root, "Batch_files").apply { mkdirs() }
    val isolatedFilesDir: File get() = File(root, "Isolated_files").apply { mkdirs() }

    /** Ensure both output directories exist (call once after permission grant). */
    fun ensureDirs() {
        batchFilesDir
        isolatedFilesDir
    }
}