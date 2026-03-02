# QWEN.md - Booming Music AI Assistant Configuration

> **Created:** March 2, 2026
> **Project:** Booming Music v1.2.1 (Stable)
> **Stack:** Android (Kotlin) + Material 3 + Room + Koin + Media3 ExoPlayer

---

## 📋 Project Overview

**Booming Music** is an open-source Android music player built with Kotlin and Material 3 / Material You design.

### Key Information
- **Package:** `com.mardous.booming`
- **Min SDK:** 28 (Android 9)
- **Target SDK:** 36
- **Compile SDK:** 36
- **JVM Toolchain:** 21
- **Build:** Single flavor (normal) - simplified from multi-flavor
- **License:** GPL-3.0

### Core Features
- 🎵 Gapless playback with chapter support
- 🎤 Auto-downloading synced lyrics (word-by-word)
- 🎚️ 15-band equalizer with AutoEq profiles
- 📚 Independent library scanner (no MediaStore dependency)
- 🎨 Material You dynamic theming (7 player themes)
- 🌐 ListenBrainz scrobbling integration
- 🚗 Android Auto + Chromecast support
- ⏰ Sleep timer + Bluetooth controls

---

## 🏗️ Architecture

### Pattern
**MVVM + Repository Pattern** with Clean Architecture principles

### Directory Structure
```
app/src/main/java/com/mardous/booming/
├── core/                    # Core: Database, models, utilities
│   ├── appwidgets/         # Glance widgets
│   ├── audio/              # Audio utilities
│   ├── model/              # Base models
│   ├── palette/            # Color palette extraction
│   ├── sort/               # Sorting logic
│   └── BoomingDatabase.kt  # Room database (v7)
├── data/                    # Data layer
│   ├── local/              # Room, repositories, DAOs
│   ├── mapper/             # Entity ↔ Domain model mappers
│   ├── model/              # Domain models
│   ├── remote/             # External APIs (Deezer, LastFM, LRCLib)
│   └── scanner/            # Independent file scanner
├── di/                      # Dependency injection (Koin modules)
├── playback/                # Audio engine
│   ├── cast/               # Chromecast support
│   ├── equalizer/          # 15-band EQ
│   ├── listenbrainz/       # Scrobbling integration
│   ├── processor/          # Audio processors
│   ├── progress/           # Playback progress
│   └── shuffle/            # Shuffle algorithms
├── ui/                      # UI layer
│   ├── screen/             # Feature screens
│   ├── component/          # Reusable components
│   └── theme/              # Theming
├── util/                    # Utilities
├── work/                    # WorkManager workers
├── coil/                    # Coil image loading customization
└── extensions/              # Kotlin extensions
```

### Key Files
- `App.kt` - Application class, Koin initialization, Coil setup
- `MainModule.kt` - Koin dependency injection modules (368 lines)
- `BoomingDatabase.kt` - Room database with migrations
- `PlaybackService.kt` - Media3 ExoPlayer service
- `MediaScannerManager.kt` - Independent library scanner

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Kotlin | 2.3.10 |
| **Audio Engine** | Media3 ExoPlayer | 1.9.2 |
| **Database** | Room | 2.8.4 |
| **Dependency Injection** | Koin | 4.1.1 |
| **UI** | Android Views + Jetpack Compose (hybrid) | - |
| **Design** | Material 3 / Material You | 1.5.0-alpha14 |
| **Navigation** | Navigation Component | 2.9.7 |
| **Image Loading** | Coil | 3.3.0 |
| **Networking** | Ktor + OkHttp | 3.4.0 |
| **Async** | Kotlin Coroutines + Flow | 1.10.2 |
| **Work Manager** | WorkManager | 2.10.2 |
| **Build** | AGP + KSP | 8.13.2 + 2.3.5 |

---

## 📦 Building and Running

### Prerequisites
- JDK 21
- Android SDK 36
- Gradle 8.x

### Build Commands
```bash
# Build debug APK
./gradlew assembleNormalDebug

# Build release APK (requires signing)
./gradlew assembleNormalRelease

# Install on device
./gradlew installNormalDebug

# Clean build
./gradlew clean
```

### Testing
```bash
# Run lint (warnings = errors in CI)
./gradlew lintNormalDebug

# Unit tests
./gradlew testNormalDebugUnitTest

# Instrumented tests
./gradlew connectedAndroidTest
```

### Compilation (Strict Mode)
```bash
# Use specific flavor task (not ambiguous compileDebugKotlin)
./gradlew compileNormalDebugKotlin
```

### Diagnostic Commands
```bash
# View app logs
adb logcat -s BoomingMusic:*

# View scanner logs
adb logcat | grep -i "scanner\|media"

# List generated APKs
find app/build/outputs/apk -name "*.apk"

# Check dependencies
./gradlew dependencies
./gradlew app:dependencies
```

---

## 🗄️ Database (Room)

### Configuration
- **Class:** `BoomingDatabase.kt`
- **Current Version:** 7 (Multi-artist support)
- **Export Schema:** false

### Entities (11 total)

| Entity | Table | Description |
|--------|-------|-------------|
| `PlaylistEntity` | Playlist | User playlists |
| `SongEntity` | PlaylistSong | Songs in playlists (N:M relation) |
| `HistoryEntity` | History | Playback history |
| `PlayCountEntity` | PlayCount | Play counts per song |
| `QueueEntity` | Queue | Current playback queue |
| `InclExclEntity` | InclExclEntity | Included/excluded folders |
| `LyricsEntity` | Lyrics | Downloaded lyrics (synced/plain) |
| `CanvasEntity` | Canvas | Spotify canvas (short videos) |
| `ScannedMediaCache` | scanned_media_cache | Independent scanner cache (v5) |
| `ListenBrainzCredential` | listenbrainz_credentials | ListenBrainz auth tokens (v6) |
| `SongArtistEntity` | song_artist | Multi-artist relation (v7) |

### Active Migrations
```kotlin
MIGRATION_1_2  // custom_cover_uri, description in PlaylistEntity
MIGRATION_2_3  // QueueEntity table
MIGRATION_3_4  // CanvasEntity table
MIGRATION_4_5  // ScannedMediaCache table (independent scanner)
MIGRATION_5_6  // ListenBrainz integration (scrobbling)
MIGRATION_6_7  // Multi-artist support (song_artist table)
```

### Important Rules
- ✅ Always increment DB version when adding entities
- ✅ Create migration for schema changes
- ❌ Never use `fallbackToDestructiveMigration()` in production
- ✅ Test migrations with existing data

---

## 🌐 External Services

| Service | Purpose | API Key Required | Status |
|---------|---------|------------------|--------|
| **Deezer** | Music search, metadata | No (public API) | ✅ Active |
| **LastFM** | Artist/album info | Yes | ✅ Active |
| **LRCLib** | Synced lyrics | No (public) | ✅ Active |
| **ListenBrainz** | Scrobbling | No (user token) | ✅ Active (v1.3.0) |
| **GitHub** | Update checks | No (public) | ✅ Active |
| **Better Lyrics** | Lyrics fallback | No | ✅ Active |

---

## 🔐 Permissions

### Required Permissions
```xml
<!-- Storage (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />

<!-- System -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.WAKE_LOCK" />

<!-- Optional -->
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### Storage Access Levels
```kotlin
enum class StorageAccessLevel {
    NONE,       // No permissions - show permission UI
    LEGACY,     // READ_EXTERNAL_STORAGE (Android < 13)
    SAF,        // Storage Access Framework (folder-specific)
    MANAGE_ALL  // MANAGE_EXTERNAL_STORAGE (full access)
}
```

**Default:** SAF (most private)

---

## 🧪 Testing

### Test Dependencies
```kotlin
// Unit tests
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
testImplementation("io.mockk:mockk:1.13.14")
testImplementation("androidx.arch.core:core-testing:2.2.0")

// Instrumented tests
androidTestImplementation("androidx.test:runner:1.6.2")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
```

### Existing Tests
- `FileScannerTest.kt` - File scanner tests
- Basic ViewModel tests
- Minimal Espresso UI tests

### Coverage Status
- Unit tests: Limited to critical components
- UI tests: Basic (espresso-core)
- **Recommendation:** Expand coverage in repositories and ViewModels

---

## 🚀 GitHub Actions CI/CD

### Workflows

#### 1. `android.yml` - Main CI
**Trigger:** Push/PR to `master` or `stable`

| Job | Description | Timeout |
|-----|-------------|---------|
| `check` | Lint (fails on warnings) | 30 min |
| `build` | Build Debug APK | 30 min |

**Artifacts:**
- Lint reports (HTML + XML) - 7 days
- Debug APKs - 14 days

#### 2. `strict-ci.yml` - Quality Gate
**Trigger:** Push/PR to `master` or `stable`

| Job | Description | Timeout |
|-----|-------------|---------|
| `kotlin-compile-check` | Strict compilation | 30 min |
| `lint-strict` | Lint without errors | 30 min |
| `build-debug` | Build Debug APK | 30 min |
| `code-quality` | Vulnerability check + report | 20 min |
| `build-release` | Build Release (unsigned) | 30 min |
| `quality-gate-summary` | Final summary | - |

**⚠️ Important:**
- Lint warnings count as **errors**
- Use `compileDebugKotlin` (not flavor-specific in CI)
- All jobs must pass for quality gate success

#### 3. `release.yml` - Release Build
**Trigger:** Tag push `v*`

| Job | Description |
|-----|-------------|
| `build` | Build Release APK |
| `publish-release` | GitHub Release with APKs |

**Secrets Required:**
- `SIGNING_KEY` - Keystore in base64
- `SIGNING_PROPERTIES` - keystore.properties in base64

**Pre-release Detection:**
- Tags with `alpha`, `beta`, `rc` → pre-release
- Tags without suffix → stable release

---

## 📝 Development Conventions

### Kotlin Style
- **Indentation:** 4 spaces
- **Line Length:** Max 120 characters
- **Naming:**
  - CamelCase for classes and objects
  - snake_case for Kotlin files
  - Prefixes for interfaces (optional)

### Design Patterns
- **Repository Pattern:** All data flows through repositories
- **Koin:** Dependency injection in all modules
- **Flow/LiveData:** Reactive in ViewModels
- **MVVM:** Clear separation between UI and logic

### Imports
- Auto-organized (Android Studio)
- No wildcard imports (`import .*`)
- Specific imports for nested classes: `PermissionManager.StorageAccessLevel`

### Code Review Checklist
- [ ] Run `./gradlew lint` before commit
- [ ] Test both flavors if applicable (now single flavor)
- [ ] Update QWEN.md with major changes
- [ ] Verify Koin modules are updated
- [ ] Increment DB version for Room schema changes
- [ ] Update `versionCode` and `versionName` for releases

---

## 🎯 Key Entry Points

### Application Class
```kotlin
// App.kt
class App : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            workManagerFactory()
            modules(appModules)
        }
    }
}
```

### Koin Modules (MainModule.kt)
```kotlin
val appModules = listOf(
    networkModule,    // HTTP clients, APIs (Deezer, LastFM, GitHub, Lyrics)
    mainModule,       // General singletons (Database, Repositories, Managers)
    roomModule,       // Database, DAOs
    dataModule,       // Repositories, scanners
    viewModule        // ViewModels
)
```

### Main Activity
```kotlin
// MainActivity.kt
// Handles Navigation Component navigation
// Global UI state
// Persistent MiniPlayer
```

### Critical Components
- `BoomingDatabase.kt` - Room configuration + migrations
- `MainModule.kt` - Dependency injection (368 lines)
- `MediaRepository.kt` - Main media repository
- `MediaScannerManager.kt` - Independent scanner
- `PlayerViewModel.kt` - Player state management

---

## 🎨 Themes Available

### Color Modes (7)
- **Normal** - Standard Material You
- **Full** - Full color
- **Gradient** - Gradients
- **Plain** - Flat
- **M3** - Pure Material 3
- **Expressive** - Expressive
- **Peek** - Peek color

### Dark Modes
- **Light** - Light theme
- **Dark** - Dark theme
- **Auto** - Follows system

### Customization
- Dynamic Material You (Monet) from wallpaper
- Custom colors per mode
- Real-time preview

---

## 📊 Project Statistics (March 2026)

| Metric | Value |
|--------|-------|
| **Kotlin Files** | ~417 |
| **Lines of Code** | ~50,000+ |
| **Unique Dependencies** | ~62 |
| **Tests** | Unit + Instrumented (basic) |
| **Room Entities** | 11 |
| **Migrations** | 6 (1→2, 2→3, 3→4, 4→5, 5→6, 6→7) |
| **ViewModels** | ~15 |
| **Screens** | ~30+ |
| **Flavors** | 1 (normal - simplified) |

---

## 🧭 Roadmap Status

### ✅ Completed (v1.2.1)
- [x] Independent library scanner (no MediaStore)
- [x] CI/CD optimization (3GB local, 4GB CI)
- [x] Single flavor build (simplified)
- [x] Enhanced database (Room v7)
- [x] Material You design (7 player themes)
- [x] ListenBrainz integration (v1.3.0 - 100%)

### ⏳ In Progress (v1.3.0-v1.4.0)
- [ ] Multi-artist support (25% - Database ready)
- [ ] Improved genre handling (planned)
- [ ] Enhanced artist pages (planned)

### 🔜 Future (v2.0.0+)
- [ ] Jellyfin & Navidrome streaming
- [ ] Advanced statistics dashboard
- [ ] Voice search
- [ ] AI-based recommendations

---

## 🐛 Known Issues & Recent Fixes

### ✅ Recent Fixes (COMPLETED)

#### 1. F-Droid Flavor Removal
**Problem:** Multi-flavor project no longer needed

**Solution Applied:**
- ✅ Removed `fdroid` flavor from `build.gradle.kts`
- ✅ Removed `app/src/fdroid` directory
- ✅ Updated GitHub Actions workflows
- ✅ Simplified to single flavor build
- ✅ Fixed `LibraryViewModel`: Missing `toSongs()` import
- ✅ Fixed `FolderSelectionActivity`: Missing `StorageAccessLevel` import

**Files Modified:**
- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/java/com/mardous/booming/MainModule.kt`
- `app/src/main/java/com/mardous/booming/data/local/repository/MediaRepository.kt`
- `app/src/main/java/com/mardous/booming/data/scanner/MediaScannerManager.kt`
- `app/src/main/java/com/mardous/booming/playback/cast/CastManager.kt`
- `app/src/main/java/com/mardous/booming/ui/screen/library/LibraryViewModel.kt`
- `app/src/main/java/com/mardous/booming/ui/screen/settings/FolderSelectionActivity.kt`

#### 2. Ambiguous Task in CI
**Problem:** `compileDebugKotlin` is ambiguous with multi-flavor

**Solution:** Changed to `compileNormalDebugKotlin compileFdroidDebugKotlin`

**File:** `.github/workflows/strict-ci.yml`

---

## 🔍 Troubleshooting Guide

### If User Reports Compilation Error:
1. Check GitHub Actions logs (Actions tab on GitHub)
2. Look for `Unresolved reference` - usually missing import
3. Verify dependencies in `gradle/libs.versions.toml`
4. Check Koin injection circularity
5. Use `compileNormalDebugKotlin` (not `compileDebugKotlin`)

### If User Wants to Add Feature:
1. Identify layer (data/ui/playback)
2. Follow existing pattern (Repository → ViewModel → UI)
3. Add tests for complex logic
4. Update QWEN.md with changes

### If CI Error:
1. Check specific workflow in `.github/workflows/`
2. Verify if lint, compilation, or test error
3. ⚠️ Lint warnings count as errors in `strict-ci.yml`
4. Check timeouts (some jobs take >20 min)

### If Room Problem:
1. Check migrations in `BoomingDatabase.kt`
2. Increment DB version for schema changes (current: v7)
3. Add migration or `fallbackToDestructiveMigration()` (not recommended in production)
4. Latest entity: `SongArtistEntity` (v7 - multi-artist)

### If Scanner Problem:
1. Check `MediaScannerManager.kt` and `FileScanner.kt`
2. Verify permissions (StorageAccessLevel)
3. Check folder paths in `FolderSelectionManager.kt`
4. Cache is at `app/build/databases/booming.db`

---

## ⚠️ Important Rules

### DO NOT
- ❌ Modify `Song` class without updating all mappers
- ❌ Change DAO method names without updating repositories
- ❌ Remove existing migrations
- ❌ Change targetSdk without verifying permissions
- ❌ Use `fallbackToDestructiveMigration()` in production
- ❌ Modify `BoomingDatabase` without creating migration

### ALWAYS
- ✅ Run `./gradlew lint` before commit
- ✅ Test on real device
- ✅ Update QWEN.md with major changes
- ✅ Verify Koin modules are updated
- ✅ Increment DB version for Room schema changes
- ✅ Update `versionCode` and `versionName` for releases

---

## 📞 Contact & Support

### Developer
- **Name:** Christians Martínez Alvarado
- **GitHub:** @Alain314159 (current fork)
- **Original:** ProjectOrbital/BoomingMusic

### Community
- **Telegram:** https://t.me/mardousdev
- **Crowdin (Translations):** https://crowdin.com/project/booming-music

### Distribution
- **GitHub Releases:** https://github.com/ProjectOrbital/BoomingMusic/releases
- **F-Droid:** https://f-droid.org/packages/com.mardous.booming/
- **IzzyOnDroid:** https://apt.izzysoft.de/packages/com.mardous.booming/
- **OpenAPK:** https://www.openapk.net/boomingmusic/com.mardous.booming/
- **Obtainium:** https://apps.obtainium.imranr.dev/

### License
**GPL-3.0** - See `LICENSE.txt`

### Support Development
- **Ko-fi:** https://ko-fi.com/christiaam

---

## 📚 Additional Documentation

- `PROJECT_CONTEXT.md` - Complete project documentation
- `ROADMAP.md` - Detailed roadmap and action plan
- `CONTRIBUTING.md` - Contribution guidelines
- `CODE_OF_CONDUCT.md` - Code of conduct
- `PERMISSIONS.md` - Permission details
- `docs/` - Future documentation directory (currently empty)

---

**Last Updated:** March 2, 2026
**Version:** 1.2.1 (Stable)
**Next Update Suggested:** When roadmap features are implemented
