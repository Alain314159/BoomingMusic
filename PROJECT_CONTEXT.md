# Booming Music - Project Context for AI Assistants

> **Última actualización:** 27 de febrero de 2026 (Actualizado por IA)
> **Versión actual:** 1.2.1 (Stable) - versionCode: 1210300
> **Estado:** En desarrollo activo

---

## 📋 Resumen Ejecutivo

**Booming Music** es un reproductor de música Android open-source desarrollado en Kotlin con Material 3 / Material You.

### Información Clave
- **Package:** `com.mardous.booming`
- **Min SDK:** 28 (Android 9)
- **Target SDK:** 36
- **Compile SDK:** 36
- **JVM Toolchain:** 21
- **Flavors:** `normal`, `fdroid`

---

## 🏗️ Arquitectura del Proyecto

### Patrón Principal
**MVVM + Repository Pattern** con Clean Architecture principles

### Capas
```
app/src/main/java/com/mardous/booming/
├── core/                    # Núcleo: Database, modelos base, utilidades
├── data/                    # Capa de datos
│   ├── local/              # Room, Repositorios, DAOs
│   ├── mapper/             # Mapeo entre entidades y modelos
│   ├── model/              # Modelos de dominio
│   ├── remote/             # Servicios API (Deezer, LastFM, etc.)
│   └── scanner/            # Scanner de archivos independiente
├── playback/                # Motor de reproducción
│   ├── cast/               # Soporte Chromecast
│   ├── equalizer/          # Ecualizador
│   └── processor/          # Procesadores de audio
├── ui/                      # Capa de UI
│   ├── screen/             # Pantallas por feature
│   ├── component/          # Componentes reutilizables
│   └── theme/              # Temas y diseño
├── util/                    # Utilidades generales
└── extensions/              # Extensiones de Kotlin
```

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnologías | Versión |
|-----------|-------------|---------|
| **Motor de Audio** | Media3 ExoPlayer | 1.9.2 |
| **Base de Datos** | Room | 2.8.4 |
| **Inyección de Dependencias** | Koin | 4.1.1 |
| **UI** | Android Views + Jetpack Compose | Híbrido |
| **Diseño** | Material 3 / Material You | 1.5.0-alpha14 |
| **Navegación** | Navigation Component | 2.9.7 |
| **Imagen** | Coil | 3.3.0 |
| **Red** | Ktor + OkHttp | 3.4.0 |
| **Async** | Kotlin Coroutines + Flow | 1.10.2 |
| **Work Manager** | WorkManager | 2.10.2 |
| **Lenguaje** | Kotlin | 2.3.10 |
| **Build** | AGP + KSP | 8.13.2 + 2.3.5 |

---

## 📦 Dependencias Principales

### Bundles Principales (libs.versions.toml)
```kotlin
kotlinx = [datetime, coroutines-android, coroutines-guava]
lifecycle = [common, runtime, livedata, viewmodel, viewmodel-compose]
media3 = [session, exoplayer, exoplayer-midi, ui-compose, cast]
navigation = [common, runtime, fragment, ui]
koin = [core, android, compose]
coil = [coil, coil-compose, coil-network]
ktor = [core, okhttp, json, content-negotiation, encoding]
glance = [glance-appwidget, glance-appwidget-preview, glance-material3]
markwon = [markwon-core, markwon-html, markwon-linkify]
```

### Dependencias Clave
- **Material Components:** 1.14.0-alpha09
- **Room:** 2.8.4 (con KSP)
- **Media3:** 1.9.2 (exoplayer, session, cast, ui-compose)
- **Compose BOM:** 2026.02.00
- **Balloon:** 1.7.3 (tooltips)
- **TagLib:** 1.0.5 (edición de tags)
- **JAudioTagger:** 2.3.15 (metadatos de audio)

---

## 🔧 Configuración de Build

### Version Catalog (`gradle/libs.versions.toml`)
- **AGP:** 8.13.2
- **Kotlin:** 2.3.10
- **KSP:** 2.3.5
- **JVM Toolchain:** 21

### Product Flavors
```kotlin
flavorDimensions += "version"
productFlavors {
    create("normal") { dimension = "version" }
    create("fdroid") { dimension = "version" }
}
```

### Versioning
```kotlin
// Versión actual: 1.2.1 (Stable)
// Version Code: 1210300
val currentVersion = Version.Stable(1, 2, 1)
```

### Signing
- Requiere `keystore.properties` para builds de release
- Keys necesarias: `keyAlias`, `keyPassword`, `storePassword`, `storeFile`
- Release build: minifyEnabled = true, shrinkResources = true

### Build Types
- **debug:** applicationIdSuffix = ".debug", versionNameSuffix = " DEBUG"
- **release:** ProGuard habilitado, shrinkResources activado

---

## 🗄️ Base de Datos (Room)

### Configuración
- **Clase:** `BoomingDatabase.kt`
- **Versión:** 5
- **Export Schema:** false

### Entidades (9)

| Entidad | Tabla | Descripción |
|---------|-------|-------------|
| `PlaylistEntity` | Playlist | Listas de reproducción del usuario |
| `SongEntity` | PlaylistSong | Canciones en playlists (relación N:M) |
| `HistoryEntity` | History | Historial de reproducción |
| `PlayCountEntity` | PlayCount | Conteo de reproducciones por canción |
| `QueueEntity` | Queue | Cola de reproducción actual |
| `InclExclEntity` | InclExclEntity | Carpetas incluidas/excluidas |
| `LyricsEntity` | Lyrics | Letras descargadas (synced/plain) |
| `CanvasEntity` | Canvas | Canvas de Spotify (videos cortos) |
| `ScannedMediaCache` | scanned_media_cache | **Cache del scanner independiente** |

### DAOs
- `PlaylistDao`, `PlayCountDao`, `HistoryDao`, `QueueDao`
- `InclExclDao`, `LyricsDao`, `CanvasDao`, `ScannedMediaCacheDao`

### Migraciones Activas
```kotlin
MIGRATION_1_2  // custom_cover_uri, description en PlaylistEntity
MIGRATION_2_3  // QueueEntity table
MIGRATION_3_4  // CanvasEntity table
MIGRATION_4_5  // ScannedMediaCache table (scanner independiente)
```

### Schema: ScannedMediaCache (v5)
```kotlin
@Entity(tableName = "scanned_media_cache")
data class ScannedMediaCache(
    @PrimaryKey(autoGenerate = true) val cacheId: Long,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val lastModified: Long,
    val title: String?,
    val artist: String?,
    val album: String?,
    val albumArtist: String?,
    val genre: String?,
    val year: Int?,
    val trackNumber: Int?,
    val duration: Int?,
    val bitrate: Int?,
    val sampleRate: Int?,
    val scanTimestamp: Long,
    val mediaStoreId: Long?,
    val isValid: Boolean = true
)
```

---

## 📡 Servicios Remotos

### APIs Externas

| Servicio | Propósito | API Key | Estado |
|----------|-----------|---------|--------|
| **Deezer** | Búsqueda de música | No (pública) | ✅ Activo |
| **LastFM** | Info de artistas/álbumes | Sí | ✅ Activo |
| **LRCLib** | Letras sincronizadas | No (pública) | ✅ Activo |
| **GitHub** | Verificación de actualizaciones | No (pública) | ✅ Activo |
| **Better Lyrics** | Letras con fallback | No | ✅ Activo |

### Servicios Locales
- `LyricsDownloadService` - Descarga de letras (LRCLib + fallbacks)
- `GitHubService` - Verificación de nuevas versiones
- `DeezerService` - Búsqueda y metadata
- `LastFmService` - Información de artistas y álbumes

---

## 🎯 Features Principales

### 🎵 Reproducción
- ✅ Gapless playback (sin interrupciones entre canciones)
- ✅ Soporte de capítulos (ID3/MP4 chapters)
- ✅ ReplayGain (normalización de volumen)
- ✅ Ecualizador de 15 bandas con perfiles personalizables
- ✅ Balance de audio (izquierda/derecha)
- ✅ Sleep timer (temporizador de apagado)
- ✅ Soporte Chromecast
- ✅ Android Auto
- ✅ Bluetooth/headset controls

### 📚 Biblioteca
- ✅ **Scanner independiente** (sin dependencia de MediaStore) - **COMPLETADO**
- ✅ Cache de archivos escaneados (ScannedMediaCache)
- ✅ Blacklist/Whitelist de carpetas
- ✅ Búsqueda por múltiples criterios
- ✅ Navegación por carpetas
- ✅ Escaneo periódico en background (WorkManager)

### 🎤 Letras
- ✅ Descarga automática desde LRCLib
- ✅ Sincronización palabra por palabra (word-by-word)
- ✅ Soporte TTML/LRC
- ✅ Traducciones de letras
- ✅ Editor de letras integrado
- ✅ Fallback a múltiples proveedores

### 🎨 UI/UX
- ✅ Material You dinámico (Monet)
- ✅ 7 temas de reproductor (Normal, Full, Gradient, Plain, M3, Expressive, Peek)
- ✅ Widgets (lock screen + home screen)
- ✅ Android Auto
- ✅ Modo oscuro/claro/automático

---

## 🔐 Permisos

### Permisos Requeridos

```xml
<!-- Almacenamiento -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- Sistema -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />

<!-- Opcionales -->
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
```

### Niveles de Acceso (StorageAccessLevel)

```kotlin
enum class StorageAccessLevel {
    NONE,       // Sin permisos - solo muestra UI de permisos
    LEGACY,     // READ_EXTERNAL_STORAGE (Android < 13)
    SAF,        // Storage Access Framework (acceso a carpetas específicas)
    MANAGE_ALL  // MANAGE_EXTERNAL_STORAGE (acceso total a archivos)
}
```

### Gestión de Permisos
- `PermissionManager.kt` - Gestiona niveles de acceso
- `PermissionsActivity.kt` - UI de solicitud de permisos
- Preferencia por defecto: SAF (más privado)

---

## 🧪 Testing

### Dependencias de Test
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

### Tests Existentes
- `FileScannerTest.kt` - Tests para el scanner de archivos
- Tests de ViewModels (básicos)
- Tests de UI con Espresso (mínimos)

### Cobertura Actual
- Tests unitarios: Limitados a componentes críticos
- Tests de UI: Básicos (espresso-core)
- **Recomendación:** Expandir cobertura en repositories y ViewModels

---

## 🚀 GitHub Actions

### Workflows Disponibles

#### 1. `android.yml` - CI Principal
**Trigger:** Push/PR a `master` o `stable`

| Job | Descripción | Timeout |
|-----|-------------|---------|
| `check` | Lint (falla en warnings) | 20 min |
| `build` | Build Debug (normal + fdroid) | 30 min |

**Artefactos:**
- Reportes lint (HTML + XML)
- APKs debug (14 días)

#### 2. `release.yml` - Build de Release
**Trigger:** Push de tags `v*`

| Job | Descripción |
|-----|-------------|
| `build` | Build Release APK (normal + fdroid) |
| `publish-release` | GitHub Release con APKs |

**Secrets Requeridos:**
- `SIGNING_KEY` - Keystore en base64
- `SIGNING_PROPERTIES` - keystore.properties en base64

**Pre-release detection:**
- Tags con `alpha`, `beta`, `rc` → pre-release
- Tags sin sufijo → release estable

#### 3. `strict-ci.yml` - Quality Gate
**Trigger:** Push/PR a `master` o `stable`

| Job | Descripción | Timeout |
|-----|-------------|---------|
| `kotlin-compile-check` | Compilación estricta | 20 min |
| `lint-strict` | Lint sin errores | 30 min |
| `build-debug` | Build Debug APK | 30 min |
| `code-quality` | Vulnerabilidades + reporte | 20 min |
| `build-release` | Build Release unsigned | 30 min |
| `quality-gate-summary` | Resumen final | - |

**Notas importantes:**
- ⚠️ Los warnings de lint cuentan como **error**
- ⚠️ Usa `compileNormalDebugKotlin compileFdroidDebugKotlin` (no `compileDebugKotlin`)
- ⚠️ Todos los jobs deben pasar para que el quality gate sea exitoso

---

## 🐛 Bugs Conocidos y Fixes Recientes

### ✅ Fixes del 27/02/2026 (COMPLETADOS)

#### 1. Errores de Compilación fdroidDebug
**Problema:** Múltiples `Unresolved reference`

**Solución aplicada:**
- ✅ Agregado WorkManager dependency (`libs.androidx.work`)
- ✅ Fix `MediaRepository`: Inyección directa de DAO (evita circularidad)
- ✅ Fix `MediaScannerManager`: Inyección directa de DAO
- ✅ Fix `CastManager`: `currentMediaItem` en lugar de `currentMediaItems`
- ✅ Fix `LibraryViewModel`: Import faltante de `toSongs()`
- ✅ Fix `FolderSelectionActivity`: Import de `StorageAccessLevel`

**Archivos modificados:**
- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/java/com/mardous/booming/MainModule.kt`
- `app/src/main/java/com/mardous/booming/data/local/repository/MediaRepository.kt`
- `app/src/main/java/com/mardous/booming/data/scanner/MediaScannerManager.kt`
- `app/src/main/java/com/mardous/booming/playback/cast/CastManager.kt`
- `app/src/main/java/com/mardous/booming/ui/screen/library/LibraryViewModel.kt`
- `app/src/main/java/com/mardous/booming/ui/screen/settings/FolderSelectionActivity.kt`

#### 2. Tarea Ambigua en CI
**Problema:** `compileDebugKotlin` es ambiguo con multi-flavor

**Solución:** Cambiar a `compileNormalDebugKotlin compileFdroidDebugKotlin`

**Archivo:** `.github/workflows/strict-ci.yml`

### 📝 Scanner Independiente (COMPLETADO)

**Estado:** ✅ **IMPLEMENTADO**

**Componentes:**
- `FileScanner.kt` - Escaneo de archivos de audio
- `MediaScannerManager.kt` - Gestión de escaneo en background
- `FolderSelectionManager.kt` - Selección de carpetas
- `PermissionManager.kt` - Gestión de permisos (4 niveles)
- `ScannedMediaCache` - Entidad Room para cache

**Características:**
- Sin dependencia de MediaStore
- Cache persistente en Room
- Soporte SAF (Storage Access Framework)
- Escaneo periódico con WorkManager
- UI de progreso en tiempo real

**Migración:** MIGRATION_4_5 (v4 → v5)

---

## 📝 Convenciones de Código

### Estilo
- **Indentación:** 4 espacios
- **Líneas:** Máximo 120 caracteres
- **Naming:**
  - CamelCase para clases y objetos
  - snake_case para archivos Kotlin
  - Prefijos para interfaces (opcional)

### Patrones de Diseño
- **Repository Pattern:** Todos los datos pasan por repositories
- **Koin:** Inyección de dependencias en todos los módulos
- **Flow/LiveData:** Reactivo en ViewModels
- **MVVM:** Separación clara entre UI y lógica

### Imports
- Ordenados automáticamente (Android Studio)
- Sin wildcard imports (`import .*`)
- Imports específicos para clases anidadas: `PermissionManager.StorageAccessLevel`

### Estructura de Paquetes
```
com.mardous.booming/
├── core/           # Núcleo (Database, utilidades base)
├── data/           # Capa de datos
│   ├── local/     # Room, repositories
│   ├── mapper/    # Mapeo entre capas
│   ├── model/     # Modelos de dominio
│   ├── remote/    # APIs externas
│   └── scanner/   # Scanner independiente
├── playback/       # Motor de audio
├── ui/            # Capa de UI
├── util/          # Utilidades generales
└── extensions/    # Extensiones de Kotlin
```

---

## 🔑 Puntos de Entrada Clave

### Application
```kotlin
// BoomingMusicApp.kt
class BoomingMusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BoomingMusicApp)
            modules(appModules)
        }
    }
}
```

### MainModule (Koin)
```kotlin
val appModules = listOf(
    networkModule,    // HTTP clients, APIs (Deezer, LastFM, GitHub, Lyrics)
    mainModule,       // Singletons generales (Database, Repositories, Managers)
    roomModule,       // Database, DAOs
    dataModule,       // Repositories, scanners
    viewModule        // ViewModels
)
```

### Actividad Principal
```kotlin
// MainActivity.kt
// Maneja navegación con Navigation Component
// Estado global de la UI
// MiniPlayer persistente
```

### Componentes Críticos
- `BoomingDatabase.kt` - Configuración de Room + migraciones
- `MainModule.kt` - Inyección de dependencias (368 líneas)
- `MediaRepository.kt` - Repository principal para medios
- `MediaScannerManager.kt` - Scanner independiente
- `PlayerViewModel.kt` - Estado del reproductor

---

## 🎨 Temas Disponibles

### Modos de Color (7)
- **Normal** - Material You estándar
- **Full** - Color completo
- **Gradient** - Degradados
- **Plain** - Plano
- **M3** - Material 3 puro
- **Expressive** - Expresivo
- **Peek** - Peek color

### Modos Oscuros
- **Claro** - Tema claro
- **Oscuro** - Tema oscuro
- **Automático** - Sigue el sistema

### Personalización
- Monet (Material You) dinámico según wallpaper
- Colores personalizados por modo
- Vista previa en tiempo real

---

## 📊 Estadísticas del Proyecto (Febrero 2026)

| Métrica | Valor |
|---------|-------|
| **Archivos Kotlin** | ~417 |
| **Líneas de Código** | ~50,000+ |
| **Dependencias únicas** | ~62 |
| **Tests** | Unitarios + Instrumentados básicos |
| **Entidades Room** | 9 |
| **Migraciones** | 4 (1→2, 2→3, 3→4, 4→5) |
| **ViewModels** | ~15 |
| **Pantallas** | ~30+ |
| **Flavors** | 2 (normal, fdroid) |

---

## 🚧 Roadmap Actual

### ✅ Completado
- [x] 📦 **Independent library scanner** - Scanner independiente sin MediaStore (v5)

### ⏳ Pendiente
- [ ] 🎨 **Multi-artist support** - Soporte para múltiples artistas por canción (split & index properly)
- [ ] 🎵 **Improved genre handling** - Mejora en manejo de géneros musicales
- [ ] 🔁 **Last.fm integration** - Integración Last.fm (import/export playback data, scrobbling)
- [ ] 💿 **Enhanced artist pages** - Páginas de artista mejoradas (separate albums and singles visually)
- [ ] 🌐 **Jellyfin & Navidrome integration** - Integración con servidores Jellyfin/Navidrome para streaming

### 🎯 Prioridades Sugeridas
1. **Last.fm integration** - Muy pedido por usuarios, permite scrobbling y exportar estadísticas
2. **Multi-artist support** - Limitación actual del proyecto, necesario para álbumes colaborativos
3. **Jellyfin/Navidrome** - Abre posibilidad de streaming desde servidores propios

---

## 📞 Contacto y Soporte

### Desarrollador
- **Nombre:** Christians Martínez Alvarado
- **GitHub:** @Alain314159 (fork actual)
- **Original:** ProjectOrbital/BoomingMusic

### Comunidad
- **Telegram:** https://t.me/mardousdev
- **Crowdin (Traducciones):** https://crowdin.com/project/booming-music

### Distribución
- **GitHub Releases:** https://github.com/ProjectOrbital/BoomingMusic/releases
- **F-Droid:** https://f-droid.org/packages/com.mardous.booming/
- **IzzyOnDroid:** https://apt.izzysoft.de/packages/com.mardous.booming/
- **OpenAPK:** https://www.openapk.net/boomingmusic/com.mardous.booming/
- **Obtainium:** https://apps.obtainium.imranr.dev/

### Licencia
**GPL-3.0** - Ver `LICENSE.txt`

### Soporte al Desarrollo
- **Ko-fi:** https://ko-fi.com/christiaam

---

## 🧭 Guía Rápida para IA

### Si el usuario reporta un error de compilación:
1. Revisar logs de GitHub Actions (pestaña Actions en GitHub)
2. Buscar `Unresolved reference` - generalmente es import faltante
3. Verificar dependencias en `gradle/libs.versions.toml`
4. Chequear circularidad en inyección Koin
5. Usar `compileNormalDebugKotlin compileFdroidDebugKotlin` (no `compileDebugKotlin`)

### Si el usuario quiere agregar feature:
1. Identificar capa (data/ui/playback)
2. Seguir patrón existente (Repository → ViewModel → UI)
3. Agregar tests si es lógica compleja
4. Actualizar este archivo con los cambios

### Si hay error en CI:
1. Revisar workflow específico en `.github/workflows/`
2. Verificar si es lint, compilación o test
3. ⚠️ Los warnings de lint cuentan como error en `strict-ci.yml`
4. Verificar timeouts (algunos jobs tardan >20 min)

### Si hay problema con Room:
1. Verificar migraciones en `BoomingDatabase.kt`
2. Incrementar versión de DB si hay cambios (actual: v5)
3. Agregar migración o `fallbackToDestructiveMigration()` (no recomendado en producción)
4. Actualizar `ScannedMediaCache` es la entidad más reciente (v5)

### Si hay problema con el scanner:
1. Revisar `MediaScannerManager.kt` y `FileScanner.kt`
2. Verificar permisos (StorageAccessLevel)
3. Chequear ruta de carpetas en `FolderSelectionManager.kt`
4. La cache está en `app/build/databases/booming.db`

### Comandos de diagnóstico:
```bash
# Ver logs del scanner
adb logcat | grep -i "scanner\|media"

# Verificar APK
find app/build/outputs/apk -name "*.apk"

# Lint rápido
./gradlew lintNormalDebug
```

---

## 📌 Notas Importantes

### NO HACER
- ❌ No modificar `Song` class sin actualizar todos los mappers
- ❌ No cambiar nombres de métodos DAO sin actualizar repositories
- ❌ No remover migraciones existentes
- ❌ No cambiar targetSdk sin verificar permisos
- ❌ No usar `fallbackToDestructiveMigration()` en producción
- ❌ No modificar `BoomingDatabase` sin crear migración

### SIEMPRE HACER
- ✅ Ejecutar `./gradlew lint` antes de commit
- ✅ Probar ambos flavors (normal y fdroid)
- ✅ Actualizar este archivo con cambios mayores
- ✅ Verificar que Koin modules estén actualizados
- ✅ Incrementar versión de DB en Room si hay cambios de schema
- ✅ Actualizar `versionCode` y `versionName` en nuevas versiones

### Recordatorios para IA
- El scanner independiente ya está implementado (v5)
- Usar `compileNormalDebugKotlin compileFdroidDebugKotlin` en CI
- WorkManager está disponible desde 27/02/2026
- GitHub token configurado en MCP servers

---

## 🔍 Comandos Útiles

### Build
```bash
# Build debug por flavor
./gradlew assembleNormalDebug
./gradlew assembleFdroidDebug

# Build release (requiere signing)
./gradlew assembleNormalRelease
./gradlew assembleFdroidRelease

# Build e install en dispositivo
./gradlew installNormalDebug
./gradlew installFdroidDebug
```

### Testing y Calidad
```bash
# Lint (falla en warnings)
./gradlew lint
./gradlew lintNormalDebug
./gradlew lintFdroidDebug

# Tests unitarios
./gradlew test
./gradlew testNormalDebugUnitTest
./gradlew testFdroidDebugUnitTest

# Tests instrumentados
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

### Dependencias
```bash
# Ver árbol de dependencias
./gradlew dependencies
./gradlew app:dependencies

# Buscar actualizaciones
./gradlew dependencyUpdates

# Reporte de dependencias
./gradlew projectReport
```

### Compilación
```bash
# Compilación estricta (ambos flavors)
./gradlew compileNormalDebugKotlin compileFdroidDebugKotlin

# KSP (Room, etc.)
./gradlew kspNormalDebugKotlin
./gradlew kspFdroidDebugKotlin
```

### Diagnóstico
```bash
# Ver logs de la app
adb logcat -s BoomingMusic:*

# Ver logs del scanner
adb logcat | grep -i "scanner\|media"

# Listar APKs generados
find app/build/outputs/apk -name "*.apk"

# Verificar signing
apksigner verify --verbose app/build/outputs/apk/normal/release/*.apk
```

---

**FIN DEL DOCUMENTO DE CONTEXTO**

> **Última actualización:** 27 de febrero de 2026
> **Actualizado por:** IA Assistant
> **Próxima actualización sugerida:** Cuando se implemente alguna feature del roadmap

---

## 📖 Índice Rápido

| Sección | Descripción |
|---------|-------------|
| [Resumen Ejecutivo](#-resumen-ejecutivo) | Información clave del proyecto |
| [Arquitectura](#-arquitectura-del-proyecto) | Capas y estructura |
| [Stack Tecnológico](#-stack-tecnológico) | Tecnologías y versiones |
| [Dependencias](#-dependencias-principales) | Librerías principales |
| [Configuración de Build](#-configuración-de-build) | Gradle, flavors, signing |
| [Base de Datos](#-base-de-datos-room) | Room entities y migraciones |
| [Servicios Remotos](#-servicios-remotos) | APIs externas |
| [Features](#-features-principales) | Funcionalidades |
| [Permisos](#-permisos) | Permisos Android |
| [Testing](#-testing) | Tests y cobertura |
| [GitHub Actions](#-github-actions) | CI/CD workflows |
| [Roadmap](#-roadmap-actual) | Estado y pendientes |
| [Guía para IA](#-guía-rápida-para-ia) | Troubleshooting |
| [Comandos](#-comandos-útiles) | Comandos Gradle |
