// Watermelon Vector Converter
// Copyright (c) 2026 Suhail Muzaffari. All rights reserved.
// Proprietary and source-available. Reuse prohibited without written permission.
// See LICENSE for terms.

package com.watermelon.converter.data.files

import android.net.Uri

/** A node in the SAF-browsed tree. Directories are expandable; files are leaves. */
data class FileNode(
    val uri: Uri,
    val name: String,
    val isDirectory: Boolean,
    val mimeType: String?,
    val sizeBytes: Long,
    val lastModified: Long,
) {
    val kind: FileKind get() = when {
        isDirectory -> FileKind.Directory
        name.endsWith(".svg", ignoreCase = true) -> FileKind.Svg
        name.endsWith(".xml", ignoreCase = true) -> FileKind.Xml
        else -> FileKind.Other
    }
}

enum class FileKind { Directory, Svg, Xml, Other }

/** Which file types the tree should show. */
data class TypeFilter(
    val showSvg: Boolean = true,
    val showXml: Boolean = true,
) {
    fun accepts(node: FileNode): Boolean = when (node.kind) {
        FileKind.Directory -> true          // always show dirs so the tree is navigable
        FileKind.Svg -> showSvg
        FileKind.Xml -> showXml
        FileKind.Other -> false             // never show non-SVG/XML files
    }
}
