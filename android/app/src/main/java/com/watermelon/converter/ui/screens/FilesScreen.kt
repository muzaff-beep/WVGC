// Watermelon Vector Converter
// Copyright (c) 2026 Suhail Muzaffari. All rights reserved.
// Proprietary and source-available. Reuse prohibited without written permission.
// See LICENSE for terms.
package com.watermelon.converter.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.watermelon.converter.data.files.FileKind
import com.watermelon.converter.viewmodel.FileManagerViewModel
import com.watermelon.converter.viewmodel.PreviewState
import com.watermelon.converter.viewmodel.TreeRow
import com.watermelon.converter.viewmodel.BatchViewModel
import com.watermelon.converter.ui.sharedGraphViewModel
import com.watermelon.converter.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(nav: NavController, vm: FileManagerViewModel = viewModel()) {
    val root by vm.root.collectAsState()
    val rows by vm.rows.collectAsState()
    val filter by vm.filter.collectAsState()
    val selected by vm.selected.collectAsState()
    val preview by vm.preview.collectAsState()
    var fullScreen by remember { mutableStateOf(false) }
    val batchVm: BatchViewModel = nav.sharedGraphViewModel()

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri -> if (uri != null) vm.openRoot(uri) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Files") },
                actions = {
                    TextButton(onClick = { picker.launch(null) }) {
                        Text(if (root == null) "Pick folder" else "Change")
                    }
                },
            )
        },
        bottomBar = {
            if (selected.isNotEmpty()) {
                BottomAppBar {
                    Text("  ${selected.size} selected", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { vm.clearSelection() }) { Text("Clear") }
                    Button(
                        onClick = {
                            val uris = vm.selectedSvgUris()
                            if (uris.isNotEmpty()) {
                                batchVm.convertFromUris(uris)
                                vm.clearSelection()
                                nav.navigate(Routes.BATCH)
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp),
                    ) { Text("Convert") }
                }
            }
        },
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            FilterBar(
                showSvg = filter.showSvg,
                showXml = filter.showXml,
                onChange = { svg, xml -> vm.setFilter(svg, xml) },
            )
            HorizontalDivider()

            if (root == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pick a folder to browse SVG and XML files.")
                }
            } else {
                LazyColumn(Modifier.weight(1f)) {
                    items(rows, key = { it.node.uri.toString() }) { row ->
                        TreeRowItem(
                            row = row,
                            isSelected = selected.contains(row.node.uri.toString()),
                            onTapDir = { vm.toggleDir(row.node) },
                            onTapFile = { vm.preview(row.node) },
                            onToggleSelect = { vm.toggleSelect(row.node) },
                        )
                    }
                }
                // Docked preview pane.
                if (preview !is PreviewState.Empty) {
                    HorizontalDivider()
                    PreviewPane(
                        state = preview,
                        onExpand = { fullScreen = true },
                        onClose = { vm.closePreview() },
                        compact = true,
                    )
                }
            }
        }
    }

    if (fullScreen && preview !is PreviewState.Empty) {
        FullScreenPreview(state = preview, onClose = { fullScreen = false })
    }
}

@Composable
private fun FilterBar(showSvg: Boolean, showXml: Boolean, onChange: (Boolean, Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Show:", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(8.dp))
        FilterChipBox("SVG", showSvg) { onChange(it, showXml) }
        Spacer(Modifier.width(8.dp))
        FilterChipBox("XML", showXml) { onChange(showSvg, it) }
    }
}

@Composable
private fun FilterChipBox(label: String, checked: Boolean, onCheck: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheck)
        Text(label)
    }
}

@Composable
private fun TreeRowItem(
    row: TreeRow,
    isSelected: Boolean,
    onTapDir: () -> Unit,
    onTapFile: () -> Unit,
    onToggleSelect: () -> Unit,
) {
    val node = row.node
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { if (node.isDirectory) onTapDir() else onTapFile() }
            .padding(start = (12 + row.depth * 16).dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val glyph = when (node.kind) {
            FileKind.Directory -> if (row.expanded) "▼" else "▶"
            FileKind.Svg -> "◆"
            FileKind.Xml -> "◇"
            FileKind.Other -> "·"
        }
        Text(glyph, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(10.dp))
        Text(
            node.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        // Only SVG files are selectable for batch (convert accepts SVG).
        if (node.kind == FileKind.Svg) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelect() })
        }
    }
}

@Composable
private fun PreviewPane(
    state: PreviewState,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    compact: Boolean,
) {
    Column(Modifier.fillMaxWidth().heightIn(min = 120.dp, max = if (compact) 220.dp else 600.dp).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                when (state) {
                    is PreviewState.SvgImage -> state.name
                    is PreviewState.XmlDrawable -> state.name
                    is PreviewState.Failed -> state.name
                    else -> "Preview"
                },
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onExpand) { Text("Expand") }
            TextButton(onClick = onClose) { Text("Close") }
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            PreviewContent(state)
        }
    }
}

@Composable
private fun FullScreenPreview(state: PreviewState, onClose: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onClose) {
        Surface {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Preview", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    TextButton(onClick = onClose) { Text("Close") }
                }
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 600.dp), contentAlignment = Alignment.Center) {
                    PreviewContent(state)
                }
            }
        }
    }
}

@Composable
private fun PreviewContent(state: PreviewState) {
    when (state) {
        is PreviewState.Loading -> CircularProgressIndicator()
        is PreviewState.SvgImage -> {
            val bmp = remember(state.png) { BitmapFactory.decodeByteArray(state.png, 0, state.png.size) }
            if (bmp != null) Image(bmp.asImageBitmap(), contentDescription = state.name)
            else Text("Could not decode preview")
        }
        is PreviewState.XmlDrawable -> XmlDrawablePreview(state.xml)
        is PreviewState.Failed -> Text(state.message, color = MaterialTheme.colorScheme.error)
        PreviewState.Empty -> {}
    }
}

/** Render an Android VectorDrawable XML via the platform inflater. */
@Composable
private fun XmlDrawablePreview(xml: String) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val bitmap = remember(xml) {
        runCatching { com.watermelon.converter.util.DrawableRenderer.renderVectorXml(ctx, xml, 512) }.getOrNull()
    }
    if (bitmap != null) {
        Image(bitmap.asImageBitmap(), contentDescription = "drawable preview")
    } else {
        Text("Can't preview this XML as a drawable.\nIt may not be an Android VectorDrawable.",
            style = MaterialTheme.typography.bodyLarge)
    }
}
