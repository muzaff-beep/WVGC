// Watermelon Vector Converter
// Copyright (c) 2026 Suhail Muzaffari. All rights reserved.
// Proprietary and source-available. Reuse prohibited without written permission.
// See LICENSE for terms.

package com.watermelon.converter.data.model

import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

/**
 * File + graphical-structure properties for the preview panel properties
 * section. Combines filesystem metadata (from java.io.File) with the
 * structural analysis returned by the Rust core (Contract C-5.0).
 */
data class VectorProperties(
    // --- file metadata ---
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val lastModified: Long,
    val createdMs: Long?,         // null if the filesystem doesn't store this

    // --- dimensions ---
    val width: Float,
    val height: Float,
    val viewportW: Float,
    val viewportH: Float,

    // --- graphical structure ---
    val pathCount: Int,
    val groupCount: Int,
    val usesPaths: Boolean,
    val usesGradients: Boolean,
    val usesSolidColors: Boolean,
    val usesStrokes: Boolean,
    val singleColorTintable: Boolean,
    val tintColor: String?,
    val isAnimated: Boolean,
) {
    companion object {
        fun from(file: File, analysisJson: String): VectorProperties {
            val j = JSONObject(analysisJson)

            // Try to read creation time via NIO (works on some Android filesystems;
            // on others, falls back to null — we show it only if the OS provides it).
            val created: Long? = try {
                val attrs = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
                val ct = attrs.creationTime().toMillis()
                // Many filesystems return epoch or equal-to-mtime if creation
                // time isn't tracked; treat those as unavailable.
                if (ct > 0L && ct != file.lastModified()) ct else null
            } catch (_: Exception) { null }

            return VectorProperties(
                name = file.name,
                path = file.absolutePath,
                sizeBytes = file.length(),
                lastModified = file.lastModified(),
                createdMs = created,
                width = j.getDouble("width").toFloat(),
                height = j.getDouble("height").toFloat(),
                viewportW = j.getDouble("viewportW").toFloat(),
                viewportH = j.getDouble("viewportH").toFloat(),
                pathCount = j.getInt("pathCount"),
                groupCount = j.getInt("groupCount"),
                usesPaths = j.getBoolean("usesPaths"),
                usesGradients = j.getBoolean("usesGradients"),
                usesSolidColors = j.getBoolean("usesSolidColors"),
                usesStrokes = j.getBoolean("usesStrokes"),
                singleColorTintable = j.getBoolean("singleColorTintable"),
                tintColor = if (j.isNull("tintColor")) null else j.getString("tintColor"),
                isAnimated = j.getBoolean("isAnimated"),
            )
        }
    }
}