# 🍉 WVGC - Watermelon Vector Graphics Converter

**SVG → Android VectorDrawable Converter**  
*Offline · Multiplatform · Source-Available*

[![Rust](https://img.shields.io/badge/Rust-2021-orange?logo=rust)](https://www.rust-lang.org/)
[![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE.md)
[![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20Windows%20%7C%20Linux-brightgreen)]()
[![Version](https://img.shields.io/badge/version-1.5-red)]()
[![Source Available](https://img.shields.io/badge/source-available-blue)]()

---

## Overview

WVGC is a high-performance, completely offline application that converts SVG vector graphics into native Android VectorDrawable (VD) XML format. Built around a single Rust core shared across Android and Desktop, it delivers consistent, reliable conversion with visual preview and batch processing.

**Key Features:**
- ✅ Convert single SVG files or entire ZIP archives (500+ files in <30s)
- 🖼️ Dual approximate preview: original SVG vs. generated VectorDrawable side-by-side
- 📦 Export as individual `.xml` files or a new ZIP archive
- 🚫 100% offline – zero network calls, zero telemetry
- 🔒 No permissions required (Android SAF, native dialogs)
- 🎨 Watermelon-themed native UI (Material 3 on Android, Tauri on Desktop)

---

# Watermelon Vector Graphics Converter

> **Copyright © 2026 Suhail Muzaffari. All rights reserved.**
> This is **proprietary, source-available** software. It is **not** open source.
> See [`LICENSE.md`](./LICENSE.md). The right to commercialize this software
> belongs exclusively to the copyright holder.

---

## Highlights

- **Offline-first.** No telemetry, no network, no external dependencies at runtime. Android builds ship without the `INTERNET` permission.
- **One shared core.** A single high-performance Rust engine (`svg-converter-core`) drives every platform, so conversion behaves identically everywhere.
- **Batch at scale.** Convert 500+ SVGs from a ZIP in seconds via parallel processing, with live progress and cancellation.
- **Dual preview.** See the original SVG and the generated VectorDrawable side by side (both rendered approximately via resvg).
- **Multiplatform.** Native Android (Jetpack Compose) and native-feeling desktop (Tauri) for Windows and Linux.

## Platforms

| Platform | UI | Status |
|----------|----|--------|
| Android (8.0+) | Jetpack Compose + Material 3 | Native |
| Windows 10/11 | Tauri (web frontend) | Desktop |
| Linux | Tauri (web frontend) | Desktop |

## Architecture

A single Rust core is shared across all platforms; thin platform layers
bridge to it.

```
svg-converter-core (Rust)   ← parsing, conversion, rendering, batch
        │
        ├── JNI bridge       → Android (Kotlin + Jetpack Compose)
        └── Tauri commands   → Desktop (Windows / Linux)
```

The project is built and documented using the **Interface-First Execution
Methodology**: frozen interface contracts are the governing artifact, with
modules built in parallel against those contracts and verified automatically
in CI. See `docs/` for the full handover package.

## Repository Layout

```
WVGC/
├── svg-converter-core/   Rust core (parser, converter, preview, batch, FFI)
├── android/              Android app (Compose UI, ViewModels, SAF, JNI)
├── tauri/                Desktop app (Tauri backend + web frontend)
├── docs/                 Contracts, ADRs, handover package, developer handbook
├── ci/                   Interface verification, test harness, packaging
└── .github/workflows/    CI pipeline
```

## Status

This repository is published in **source-available** form for reference and
transparency. It is under active development and is not accepting external
contributions or redistribution at this time.

## License

Proprietary and source-available — see [`LICENSE.md`](./LICENSE.md).

- You may **view** the source.
- You may **not** use, copy, modify, distribute, compile, run, or
  commercialize it without prior written permission.
- Third-party dependencies retain their own licenses; see
  [`THIRD_PARTY_NOTICES.md`](./THIRD_PARTY_NOTICES.md).

For commercial licensing or any other use, contact **Suhail Muzaffari** —
so.muzaff@gmail.com.
