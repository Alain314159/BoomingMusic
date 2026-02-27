# Booming Music - Project Context for AI Assistants

> **Última actualización:** 27 de febrero de 2026  
> **Versión actual:** 1.2.1 (Stable)  
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

| Categoría | Tecnologías |
|-----------|-------------|
| **Motor de Audio** | Media3 ExoPlayer 1.9.2 |
| **Base de Datos** | Room 2.8.4 |
| **Inyección de Dependencias** | Koin 4.1.1 |
| **UI** | Android Views + Jetpack Compose (híbrido) |
| **Diseño** | Material 3 / Material You |
| **Navegación** | Navigation Component 2.9.7 |
| **Imagen** | Coil 3.3.0 |
| **Red** | Ktor 3.4.0, OkHttp |
| **Async** | Kotlin Coroutines 1.10.2, Flow |
| **Work Manager** | WorkManager 2.10.2 |
| **Lenguaje** | Kotlin 2.3.10 |

---

## 📦 Dependencias Principales

### Agregadas Recientemente (Febrero 2026)
```kotlin
// WorkManager - Agregado el 27/02/2026
implementation(libs.androidx.work)  // work-runtime-ktx:2.10.2
```

### Bundles Principales
```kotlin
kotlinx = [datetime, coroutines-android, coroutines-guava]
lifecycle = [common, runtime, livedata, viewmodel, viewmodel-compose]
media3 = [session, exoplayer, exoplayer-midi, ui-compose, cast]
navigation = [common, runtime, fragment, ui]
koin = [core, android, compose]
coil = [coil, coil-compose, coil-network]
ktor = [core, okhttp, json, content-negotiation, encoding]
```

---

## 🔧 Configuración de Build

### Version Catalog (`gradle/libs.versions.toml`)
- **AGP:** 8.13.2
- **Kotlin:** 2.3.10
- **KSP:** 2.3.5

### Product Flavors
```kotlin
flavorDimensions += "version"
productFlavors {
    create("normal") { dimension = "version" }
    create("fdroid") { dimension = "version" }
}
```

### Signing
- Requiere `keystore.properties` para builds de release
- Keys necesarias: `keyAlias`, `keyPassword`, `storePassword`, `storeFile`

---

## 🗄️ Base de Datos (Room)

### Entidades
1. `PlaylistEntity` - Listas de reproducción
2. `SongEntity` - Canciones en playlists
3. `HistoryEntity` - Historial de reproducción
4. `PlayCountEntity` - Conteo de reproducciones
5. `QueueEntity` - Cola de reproducción
6. `InclExclEntity` - Inclusión/Exclusión de carpetas
7. `LyricsEntity` - Letras descargadas
8. `CanvasEntity` - Canvas de Spotify
9. `ScannedMediaCache` - Cache del scanner independiente (v5)

### Migraciones Activas
```kotlin
MIGRATION_1_2  // custom_cover_uri, description en PlaylistEntity
MIGRATION_2_3  // QueueEntity table
MIGRATION_3_4  // CanvasEntity table
MIGRATION_4_5  // ScannedMediaCache table (nueva)
```

---

## 📡 Servicios Remotos

### APIs Externas
| Servicio | Propósito | API Key Requerida |
|----------|-----------|-------------------|
| **Deezer** | Búsqueda de música | No (pública) |
| **LastFM** | Información de artistas/álbumes | Sí |
| **LRCLib** | Letras sincronizadas | No (pública) |
| **GitHub** | Actualizaciones | No (pública) |

### Servicios Locales
- `LyricsDownloadService` - Descarga de letras
- `GitHubService` - Verificación de actualizaciones

---

## 🎯 Features Principales

### Reproducción
- ✅ Gapless playback
- ✅ Soporte de capítulos (ID3/MP4)
- ✅ ReplayGain
- ✅ Ecualizador de 15 bandas
- ✅ Balance de audio
- ✅ Sleep timer

### Biblioteca
- ✅ Scanner independiente (sin MediaStore)
- ✅ Cache de archivos escaneados
- ✅ Blacklist/Whitelist de carpetas
- ✅ Búsqueda por múltiples criterios

### Letras
- ✅ Descarga automática
- ✅ Sincronización palabra por palabra
- ✅ Soporte TTML/LRC
- ✅ Traducciones

### UI/UX
- ✅ Material You dinámico
- ✅ Múltiples temas (Normal, Full, Gradient, etc.)
- ✅ Widgets
- ✅ Android Auto

---

## 🔐 Permisos

### Requeridos
```xml
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### Niveles de Acceso (StorageAccessLevel)
```kotlin
enum class StorageAccessLevel {
    NONE,       // Sin permisos
    LEGACY,     // READ_EXTERNAL_STORAGE
    SAF,        // Storage Access Framework
    MANAGE_ALL // MANAGE_EXTERNAL_STORAGE
}
```

---

## 🧪 Testing

### Dependencias de Test
```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
testImplementation("io.mockk:mockk:1.13.14")
testImplementation("androidx.arch.core:core-testing:2.2.0")
androidTestImplementation("androidx.test:runner:1.6.2")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
```

---

## 🚀 GitHub Actions

### Workflows Disponibles

#### 1. `android.yml` - CI Principal
- **Trigger:** Push/PR a `master` o `stable`
- **Jobs:**
  - `check` - Lint (falla en warnings)
  - `build` - Build Debug (normal + fdroid)

#### 2. `release.yml` - Build de Release
- **Trigger:** Push de tags `v*`
- **Jobs:**
  - `build` - Build Release APK
  - `publish-release` - GitHub Release

#### 3. `strict-ci.yml` - Quality Gate
- **Trigger:** Push/PR a `master` o `stable`
- **Jobs:**
  - `kotlin-compile-check` - Compilación estricta
  - `lint-strict` - Lint sin errores
  - `build-debug` - Build Debug
  - `code-quality` - Vulnerabilidades y reporte
  - `build-release` - Build Release unsigned

### Secrets Requeridos
```
SIGNING_KEY         # Keystore en base64
SIGNING_PROPERTIES  # keystore.properties en base64
```

---

## 🐛 Bugs Conocidos y Fixes Recientes

### Fixes del 27/02/2026

#### 1. Errores de Compilación fdroidDebug
**Problema:** Múltiples `Unresolved reference`

**Solución aplicada:**
- ✅ Agregado WorkManager dependency
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

---

## 📝 Convenciones de Código

### Estilo
- **Indentación:** 4 espacios
- **Líneas:** Máximo 120 caracteres
- **Naming:** CamelCase para clases, snake_case para archivos

### Patrones
- **Repository Pattern:** Todos los datos pasan por repositories
- **Koin:** Inyección de dependencias en todos los módulos
- **Flow/LiveData:** Reactivo en ViewModels

### Imports
- Ordenados automáticamente
- Sin wildcard imports (`import .*`)
- Imports específicos para clases anidadas (`PermissionManager.StorageAccessLevel`)

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
    networkModule,    // HTTP clients, APIs
    mainModule,       // Singletons generales
    roomModule,       // Database, DAOs
    dataModule,       // Repositories, scanners
    viewModule        // ViewModels
)
```

### Activity Principal
```kotlin
// MainActivity.kt
// Maneja navegación y estado global de la UI
```

---

## 🎨 Temas Disponibles

### Modos de Color
- **Normal** - Material You estándar
- **Full** - Color completo
- **Gradient** - Degradados
- **Plain** - Plano
- **M3** - Material 3 puro
- **Expressive** - Expresivo
- **Peek** - Peek color

### Modos Oscuros
- Claro
- Oscuro
- Automático (sigue sistema)

---

## 📊 Estadísticas del Proyecto (Feb 2026)

- **Archivos Kotlin:** ~417
- **Líneas de Código:** ~50,000+
- **Dependencias:** ~62 únicas
- **Tests:** Unitarios + Instrumentados básicos

---

## 🚧 Roadmap Actual

### Pendiente
- [ ] Librería independiente para scanner
- [ ] Soporte multi-artista
- [ ] Mejora en manejo de géneros
- [ ] Integración Last.fm (import/export)
- [ ] Páginas de artista mejoradas (álbumes vs singles)
- [ ] Integración Jellyfin/Navidrome

---

## 📞 Contacto y Soporte

### Desarrollador
- **Nombre:** Christians Martínez Alvarado
- **GitHub:** @Alain314159 (fork actual)
- **Original:** ProjectOrbital/BoomingMusic

### Comunidad
- **Telegram:** https://t.me/mardousdev
- **Crowdin (Traducciones):** https://crowdin.com/project/booming-music

### Licencia
**GPL-3.0** - Ver `LICENSE.txt`

---

## 🧭 Guía Rápida para IA

### Si el usuario reporta un error de compilación:
1. Revisar logs de GitHub Actions
2. Buscar `Unresolved reference` - generalmente es import faltante
3. Verificar dependencias en `libs.versions.toml`
4. Chequear circularidad en inyección Koin

### Si el usuario quiere agregar feature:
1. Identificar capa (data/ui/playback)
2. Seguir patrón existente (Repository → ViewModel → UI)
3. Agregar tests si es lógica compleja
4. Actualizar este archivo

### Si hay error en CI:
1. Revisar workflow específico en `.github/workflows/`
2. Verificar si es lint, compilación o test
3. Los warnings de lint cuentan como error en `strict-ci.yml`

### Si hay problema con Room:
1. Verificar migraciones en `BoomingDatabase.kt`
2. Incrementar versión de DB si hay cambios
3. Agregar migración o `fallbackToDestructiveMigration()`

---

## 📌 Notas Importantes

### NO HACER
- ❌ No modificar `Song` class sin actualizar todos los mappers
- ❌ No cambiar nombres de DAO methods sin actualizar repositories
- ❌ No remover migraciones existentes
- ❌ No cambiar targetSdk sin verificar permisos

### SIEMPRE HACER
- ✅ Ejecutar `./gradlew lint` antes de commit
- ✅ Probar ambos flavors (normal y fdroid)
- ✅ Actualizar este archivo con cambios mayores
- ✅ Verificar que Koin modules estén actualizados

---

## 🔍 Comandos Útiles

```bash
# Build debug
./gradlew assembleNormalDebug
./gradlew assembleFdroidDebug

# Build release
./gradlew assembleNormalRelease
./gradlew assembleFdroidRelease

# Lint
./gradlew lint

# Tests
./gradlew test
./gradlew connectedAndroidTest

# Clean
./gradlew clean

# Dependencias
./gradlew dependencies
./gradlew dependencyUpdates
```

---

**FIN DEL DOCUMENTO DE CONTEXTO**

> Para actualizar este archivo, agrega una nueva sección o modifica la fecha de última actualización.
