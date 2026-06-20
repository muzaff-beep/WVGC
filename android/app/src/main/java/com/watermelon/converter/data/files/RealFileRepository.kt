// Watermelon Vector Converter
// Copyright (c) 2026 Suhail Muzaffari. All rights reserved.
// Proprietary and source-available. Reuse prohibited without written permission.
// See LICENSE for terms.

package com.watermelon.converter.data.files

import android.os.Environment
import com.watermelon.converter.logging.AppLogger
import java.io.File

/**
 * Real filesystem browsing (MANAGE_EXTERNAL_STORAGE). Lazy: lists one
 * directory level at a time (expand on tap), same pattern as before, just
 * backed by java.io.File instead of SAF document queries — much faster.
 */
class RealFileRepository {

    /** The default starting point: shared storage root. */
    fun defaultRoot(): File = Environment.getExternalStorageDirectory()

    fun listChildren(dir: File): List<FileNode> {
        val out = ArrayList<FileNode>()
        try {
            val children = dir.listFiles() ?: return emptyList()
            for (f in children) {
                // Skip hidden/system dirs to keep the tree usable.
                if (f.name.startsWith(".")) continue
                out.add(
                    FileNode(
                        file = f,
                        name = f.name,
                        isDirectory = f.isDirectory,
                        sizeBytes = if (f.isFile) f.length() else 0L,
                        lastModified = f.lastModified(),
                    )
                )
            }
        } catch (e: SecurityException) {
            AppLogger.logError("RealFileRepository", "permission denied listing ${dir.path}", e)
        } catch (e: Exception) {
            AppLogger.logError("RealFileRepository", "listChildren failed for ${dir.path}", e)
        }
        out.sortWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        return out
    }

    /**
     * Recursive search within [root] (current folder + subfolders), matching
     * [query] case-insensitively against file/dir names. Bounded by [maxResults]
     * so a huge tree can't hang the UI.
     */
    fun search(root: File, query: String, maxResults: Int = 200): List<FileNode> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        val out = ArrayList<FileNode>()
        fun walk(dir: File) {
            if (out.size >= maxResults) return
            val children = try { dir.listFiles() } catch (e: SecurityException) { null } ?: return
            for (f in children) {
                if (out.size >= maxResults) return
                if (f.name.startsWith(".")) continue
                if (f.name.lowercase().contains(q)) {
                    out.add(
                        FileNode(
                            file = f, name = f.name, isDirectory = f.isDirectory,
                            sizeBytes = if (f.isFile) f.length() else 0L,
                            lastModified = f.lastModified(),
                        )
                    )
                }
                if (f.isDirectory) walk(f)
            }
        }
        walk(root)
        return out
    }
}
