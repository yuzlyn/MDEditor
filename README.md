# MDEditor

[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-latest-blueviolet.svg)](https://developer.android.com/develop/ui/compose)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A modern, beautifully crafted Markdown note-taking app for Android, built entirely with Jetpack Compose and Material 3 (Material You).

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="128" alt="MDEditor Icon">
</p>

## Features

- **Full Markdown Support** — Powered by [commonmark-java](https://github.com/commonmark/commonmark-java). Write in pure Markdown with live preview: headings, paragraphs, code blocks, blockquotes, tables, bullet & numbered lists, bold, italic, underline, strikethrough, links, math blocks and more.

- **Material You Design** — Full Material 3 theming with dynamic Monet color extraction from your wallpaper, plus 9 hand-picked custom background colors for each note.

- **Dark Mode** — Adaptive dark/light themes with proper card elevation, text readability, and color palettes for every note background.

- **Notebook Organization** — Group notes into notebooks for structured workflows.

- **Archive & Trash** — Soft-delete with 7-day auto-cleanup. Archive notes to keep them out of sight without deleting. Restore or permanently delete anytime.

- **Batch Operations** — Long-press to enter selection mode. Multi-select notes for batch pin, color, archive, or delete with smooth exit animations (scale + fade).

- **Local-First File Storage** — Uses Android's Storage Access Framework (SAF). Notes are saved as `.md` files in a user-chosen folder. Your data, your control.

- **Multi-Language** — English, 简体中文, 繁體中文, Français. Switch on-the-fly in Settings.

- **Search & Sort** — Instant search across all notes. Sort by custom order, creation date, or last modified.

- **Layout Toggle** — Switch between staggered grid and compact list views.

- **Animated Everything** — Card entrance animations, deletion transitions, smooth navigation, and a themed splash screen.

## Screenshots

> *Coming soon*

## Tech Stack

| Category | Technology |
|---|---|
| Language | [Kotlin](https://kotlinlang.org) 2.2.10 |
| UI Framework | [Jetpack Compose](https://developer.android.com/develop/ui/compose) + Material 3 |
| Navigation | [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) |
| Architecture | MVVM with `ViewModel` + `StateFlow` |
| Markdown Parsing | [commonmark-java](https://github.com/commonmark/commonmark-java) 0.21.0 + GFM Tables extension |
| File Access | [SAF](https://developer.android.com/guide/topics/providers/document-provider) (DocumentFile) |
| Build System | Gradle with [Version Catalog](https://docs.gradle.org/current/userguide/platforms.html) |

## Project Structure

```
app/src/main/java/com/yuzlyn/mdeditor/
├── MainActivity.kt              # Entry point, splash background, navigation host
├── data/
│   ├── NoteStorage.kt           # .md file read/write via SAF
│   ├── ThemeConfig.kt           # Theme state management (System/Monet/Custom)
│   ├── MonetPalette.kt          # 9-color palette definitions (light + dark)
│   └── profile/
│       └── AuthorProfile.kt     # Author info & social links
└── ui/
    ├── theme/
    │   └── MDEditorTheme.kt     # Material 3 dynamic color theme
    ├── screen/
    │   ├── MainScreen.kt        # Home, notebooks, archive, trash, search
    │   ├── EditorScreen.kt      # Markdown editor + insert/format sheets
    │   ├── MarkdownRenderer.kt  # AST-to-Compose renderer (commonmark)
    │   ├── SettingsScreen.kt    # Theme, language settings
    │   └── AboutScreen.kt       # About page with social links
    └── viewmodel/
        └── FileViewModel.kt     # Central state: notes, folders, selection
```

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17+
- Gradle 9.x

### Build & Run

```bash
git clone https://github.com/yuzlyn/MDEditor.git
cd MDEditor
./gradlew installDebug
```

Open the project in Android Studio, sync Gradle, and run on a device or emulator (API 26+).

### First Launch

1. Grant storage permission when prompted
2. Select a folder to store your Markdown notes
3. Tap **+** to create your first note

## License

```
MIT License

Copyright (c) 2025 Yuzlyn

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<p align="center">
  <sub>Built with ❤️ by <a href="https://github.com/yuzlyn">Yuzlyn</a></sub>
</p>
