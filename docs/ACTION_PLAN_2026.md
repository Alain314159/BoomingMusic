# 📋 Plan de Acción Completo - BoomingMusic 2026

> **Fecha:** 2 de marzo de 2026  
> **Versión:** v1.2.1 → v2.0.0  
> **Estado:** Planificación Estratégica

---

## 🎯 Visión General

Este documento consolida el análisis exhaustivo del proyecto BoomingMusic y define un plan de acción estratégico para 2026, priorizando características, optimizaciones técnicas, y mejoras de arquitectura.

---

## 📊 Estado Actual del Proyecto

### **Métricas del Proyecto**
| Métrica | Valor |
|---------|-------|
| **Archivos Kotlin** | ~428 |
| **Archivos XML** | ~370 |
| **Líneas de Código** | ~50,000+ |
| **Entidades Room** | 9 (v6) → 10 (v7) |
| **Migraciones** | 6 (v1→v7) |
| **ViewModels** | ~15 |
| **Pantallas** | ~30+ |
| **Cobertura de Tests** | Básica (<50%) |

### **Fortalezas Actuales**
- ✅ Arquitectura MVVM + Repository Pattern sólida
- ✅ Documentación excepcional (PROJECT_CONTEXT.md, QWEN.md, ROADMAP.md)
- ✅ CI/CD con GitHub Actions optimizado
- ✅ Scanner independiente sin MediaStore
- ✅ ListenBrainz integration 100% completada
- ✅ Material 3 / Material You actualizado

### **Debilidades Identificadas**
- ❌ Soporte multi-artist inexistente (0%)
- ❌ Gestión de géneros limitada (0%)
- ❌ Cobertura de tests básica
- ❌ Build times largos (30 min en CI)
- ❌ Mezcla Flow/LiveData inconsistente
- ❌ Documentación KDoc inconsistente
- ❌ Sin integración streaming (Jellyfin/Navidrome)

---

## 🗓️ Roadmap Estratégico 2026

### **Q1 2026 (Enero - Marzo) - Cimentación**

#### **Objetivo Principal:** Completar características críticas pendientes

| Feature | Estado | Prioridad | Esfuerzo | Deadline |
|---------|--------|-----------|----------|----------|
| **ListenBrainz Integration** | ✅ 100% | CRÍTICA | 20h | COMPLETADO |
| **Multi-Artist (Fase 1: DB)** | ✅ 25% | ALTA | 2h | COMPLETADO |
| **Multi-Artist (Fase 2: Repository)** | ⏳ 0% | ALTA | 8h | 15 Mar |
| **Multi-Artist (Fase 3: UI)** | ⏳ 0% | ALTA | 12h | 31 Mar |
| **Multi-Artist (Fase 4: Testing)** | ⏳ 0% | ALTA | 6h | 7 Abr |

**Entregables Q1:**
- ✅ ListenBrainz scrobbling funcional (v1.3.0)
- ⏳ Soporte multi-artist completo (v1.4.0)
- ⏳ Tests unitarios para multi-artist (80%+ coverage)

---

### **Q2 2026 (Abril - Junio) - Optimización**

#### **Objetivo Principal:** Optimizar rendimiento y deuda técnica

| Feature | Estado | Prioridad | Esfuerzo | Deadline |
|---------|--------|-----------|----------|----------|
| **Improved Genre Handling** | ⏳ 0% | MEDIA | 20h | 30 Abr |
| **Build Time Optimization** | ⏳ 0% | ALTA | 16h | 15 May |
| **Test Coverage Expansion** | ⏳ 0% | ALTA | 24h | 31 May |
| **Enhanced Artist Pages** | ⏳ 0% | MEDIA | 28h | 30 Jun |
| **Flow/LiveData Standardization** | ⏳ 0% | MEDIA | 20h | 15 Jun |

**Entregables Q2:**
- ⏳ Géneros con jerarquías (v1.4.1)
- ⏳ Build times reducidos a <15 min (v1.4.2)
- ⏳ Cobertura de tests 80%+ (v1.4.3)
- ⏳ Artist pages mejoradas (v1.5.0)

---

### **Q3 2026 (Julio - Septiembre) - Innovación**

#### **Objetivo Principal:** Características diferenciadoras

| Feature | Estado | Prioridad | Esfuerzo | Deadline |
|---------|--------|-----------|----------|----------|
| **Jellyfin Integration** | ⏳ 0% | ALTA | 40h | 31 Jul |
| **Navidrome Integration** | ⏳ 0% | ALTA | 28h | 15 Ago |
| **Voice Search** | ⏳ 0% | BAJA | 16h | 31 Ago |
| **Advanced Statistics** | ⏳ 0% | BAJA | 24h | 15 Sep |
| **Party Mode** | ⏳ 0% | BAJA | 12h | 30 Sep |

**Entregables Q3:**
- ⏳ Streaming desde servidores propios (v2.0.0-beta)
- ⏳ Búsqueda por voz con Google Assistant (v2.0.0)
- ⏳ Dashboard de estadísticas (v2.0.1)

---

### **Q4 2026 (Octubre - Diciembre) - Pulido**

#### **Objetivo Principal:** Estabilización y características menores

| Feature | Estado | Prioridad | Esfuerzo | Deadline |
|---------|--------|-----------|----------|----------|
| **Lyrics Editor Mejorado** | ⏳ 0% | BAJA | 16h | 31 Oct |
| **KDoc Documentation** | ⏳ 0% | MEDIA | 30h | 15 Nov |
| **Performance Audit** | ⏳ 0% | ALTA | 20h | 30 Nov |
| **Bug Fixes & Stability** | ⏳ 0% | CRÍTICA | 40h | 15 Dic |
| **v2.0.0 Stable Release** | ⏳ 0% | CRÍTICA | - | 20 Dic |

**Entregables Q4:**
- ⏳ Editor de letras avanzado (v2.0.2)
- ⏳ Documentación KDoc completa (v2.0.3)
- ⏳ Release estable v2.0.0

---

## 🔧 Mejoras Técnicas Detalladas

### **1. Arquitectura y Patrones**

#### **1.1 Modularización de Koin Modules**
**Problema:** MainModule.kt tiene 368 líneas - demasiado monolítico

**Solución:**
```kotlin
// Actual
val appModules = listOf(networkModule, mainModule, roomModule, dataModule, viewModule)

// Propuesto
val appModules = listOf(
    // Capa de Datos
    networkModule,
    databaseModule,
    repositoryModule,
    scannerModule,
    
    // Capa de Dominio
    useCaseModule,
    
    // Capa de UI
    viewModelModule,
    uiModule,
    
    // Features Específicos
    playbackModule,
    equalizerModule,
    lyricsModule,
    listenBrainzModule
)
```

**Beneficios:**
- Mejores tiempos de compilación (incremental)
- Mejor separación de responsabilidades
- Testing más sencillo

**Esfuerzo:** 16h  
**Prioridad:** MEDIA  
**Quarter:** Q2

---

#### **1.2 Estandarización Flow vs LiveData**
**Problema:** Mezcla inconsistente de Flow y LiveData en ViewModels

**Solución:**
```kotlin
// Estandarizar a Flow (recomendado)
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()
    
    // Para UI que requiere LiveData (interop)
    val stateLiveData: LiveData<UiState> = _state.asLiveData()
}
```

**Archivos a actualizar:** ~15 ViewModels  
**Esfuerzo:** 20h  
**Prioridad:** MEDIA  
**Quarter:** Q2

---

#### **1.3 Implementación de Use Cases**
**Problema:** ViewModels llaman directamente a repositories

**Solución:**
```kotlin
// Actual
class PlayerViewModel(private val repository: Repository) : ViewModel() {
    fun playSong(songId: Long) {
        viewModelScope.launch {
            repository.getSong(songId).collect { ... }
        }
    }
}

// Propuesto (Clean Architecture)
class GetSongUseCase(private val repository: Repository) {
    suspend operator fun invoke(songId: Long): Flow<Song> {
        return repository.getSong(songId)
    }
}

class PlayerViewModel(private val getSongUseCase: GetSongUseCase) : ViewModel() {
    fun playSong(songId: Long) {
        viewModelScope.launch {
            getSongUseCase(songId).collect { ... }
        }
    }
}
```

**Esfuerzo:** 40h (refactorización gradual)  
**Prioridad:** BAJA  
**Quarter:** Q3-Q4

---

### **2. Base de Datos y Persistencia**

#### **2.1 Estrategia de Migraciones Robustas**
**Problema:** Migraciones actuales no tienen tests automatizados

**Solución:**
```kotlin
// Test de migración v6 → v7
@Test
fun migrate6to7_preservesData() {
    // 1. Crear DB con v6
    val dbV6 = SupportSQLiteDatabase.create(...)
    
    // 2. Insertar datos de prueba
    dbV6.execSQL("INSERT INTO SongEntity ...")
    
    // 3. Ejecutar migración
    MIGRATION_6_7.migrate(dbV6)
    
    // 4. Validar datos migrados
    val cursor = dbV6.query("SELECT * FROM song_artist WHERE song_id = 1")
    assert(cursor.count == 1)
}
```

**Esfuerzo:** 12h  
**Prioridad:** ALTA  
**Quarter:** Q2

---

#### **2.2 Índices de Búsqueda Optimizados**
**Problema:** Queries lentas en bibliotecas grandes (10,000+ canciones)

**Solución:**
```kotlin
@Entity(
    indices = [
        Index(value = ["artist_name"]),
        Index(value = ["album_name"]),
        Index(value = ["genre_name"]),
        Index(value = ["title"]),
        Index(value = ["date_added"]),
        Index(value = ["date_modified"]),
        Index(value = ["artist_name", "album_name"]), // Búsqueda compuesta
        Index(value = ["genre_name", "artist_name"])  // Búsqueda compuesta
    ]
)
data class ScannedMediaCache(...)
```

**Esfuerzo:** 4h  
**Prioridad:** MEDIA  
**Quarter:** Q2

---

#### **2.3 Paging 3 para Listas Grandes**
**Problema:** Listas con 1000+ elementos consumen mucha memoria

**Solución:**
```kotlin
// Repository
fun getSongsPaged(): Flow<PagingData<Song>> {
    return Pager(
        config = PagingConfig(pageSize = 50, prefetchDistance = 20),
        pagingSourceFactory = { songDao.getSongsPaged() }
    ).flow
}

// ViewModel
val songs: Flow<PagingData<Song>> = repository.getSongsPaged()
    .cachedIn(viewModelScope)

// UI (Compose)
val lazyListItems = songs.collectAsLazyPagingItems()
LazyColumn {
    items(lazyListItems) { song ->
        SongItem(song)
    }
}
```

**Esfuerzo:** 16h  
**Prioridad:** MEDIA  
**Quarter:** Q2

---

### **3. Optimización de Rendimiento**

#### **3.1 Gradle Build Optimization**
**Problema:** Build times de 30 minutos en CI

**Solución:**

**gradle.properties:**
```properties
# Build cache
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.vfs.watch=true

# Daemon
org.gradle.daemon=true
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m

# Kotlin
kotlin.caching.enabled=true
kotlin.incremental=true
kotlin.parallel.tasks.in.project=true
```

**build.gradle.kts:**
```kotlin
android {
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
    
    // Deshabilitar features no usadas
    lint {
        checkDependencies = false  // Solo check del módulo actual
    }
}
```

**Esfuerzo:** 8h  
**Prioridad:** ALTA  
**Quarter:** Q2  
**Impacto:** Reducción de 30min → 15min

---

#### **3.2 Memory Management**
**Problema:** Picos de memoria en background

**Solución:**
```kotlin
class App : Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            TRIM_MEMORY_RUNNING_MODERATE -> {
                // Liberar cache de imágenes
                ImageLoader.instance.memoryCache?.clear()
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                // Liberar recursos no críticos
                ReplayGainTagExtractor.clearCache()
            }
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                // Liberar TODO lo posible
                Coil.imageLoader(applicationContext).memoryCache?.clear()
                Coil.imageLoader(applicationContext).diskCache?.clear()
            }
        }
    }
}
```

**Esfuerzo:** 4h  
**Prioridad:** ALTA  
**Quarter:** Q2

---

#### **3.3 Lazy Loading de Imágenes**
**Problema:** Carga de imágenes bloquea UI

**Solución:**
```kotlin
// Coil configuration optimizada
val imageLoader = ImageLoader.Builder(context)
    .crossfade(true)
    .allowHardware(false)  // Evita crashes en algunos dispositivos
    .memoryCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .respectCacheHeaders(false)
    .maxSizePercent(0.25)  // Máximo 25% de memoria disponible
    .build()
```

**Esfuerzo:** 2h (ya implementado, solo auditar)  
**Prioridad:** BAJA  
**Quarter:** Q4

---

### **4. Testing y Calidad**

#### **4.1 Test Coverage Expansion**
**Problema:** Cobertura de tests básica (<50%)

**Objetivo:** 80%+ coverage en capas críticas

**Plan:**
```kotlin
// Repositories (24 tests)
class SongRepositoryTest {
    @Test fun getAllSongs_returnsAllSongs()
    @Test fun getSongsByArtist_returnsCorrectSongs()
    @Test fun searchSongs_byTitle_returnsMatches()
    @Test fun searchSongs_byArtist_returnsMatches()
    // ... 20 tests más
}

// ViewModels (18 tests)
class PlayerViewModelTest {
    @Test fun playSong_updatesCurrentSong()
    @Test fun pauseSong_updatesPlaybackState()
    @Test fun skipToNext_updatesQueue()
    // ... 15 tests más
}

// Use Cases (16 tests)
class GetSongUseCaseTest {
    @Test fun invoke_returnsSongFlow()
    @Test fun invoke_withInvalidId_returnsEmpty()
    // ... 14 tests más
}
```

**Esfuerzo:** 24h  
**Prioridad:** ALTA  
**Quarter:** Q2

---

#### **4.2 Migration Testing**
**Problema:** Migraciones no tienen tests

**Solución:**
```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    
    @Test
    fun migrate6to7_multiArtistDataPreserved() {
        // Setup DB v6
        val db = Room.databaseBuilder(
            context,
            BoomingDatabase::class.java,
            "test.db"
        )
        .addMigrations(MIGRATION_6_7)
        .build()
        
        // Insertar datos en v6
        db.songDao().insert(SongEntity(...))
        
        // Ejecutar migración
        db.openHelper.writableDatabase
        
        // Validar datos en v7
        val artists = db.songArtistDao().getArtistsForSong(1)
        assert(artists.size == 1)
        assert(artists[0].artistName == "Artista Original")
    }
}
```

**Esfuerzo:** 8h  
**Prioridad:** ALTA  
**Quarter:** Q2

---

#### **4.3 UI Testing con Compose**
**Problema:** Sin tests de UI

**Solución:**
```kotlin
@RunWith(AndroidJUnit4::class)
class PlayerScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun playerScreen_displaysSongTitle() {
        composeTestRule.setContent {
            PlayerScreen(viewModel = fakeViewModel)
        }
        
        composeTestRule
            .onNodeWithText("Song Title")
            .assertIsDisplayed()
    }
    
    @Test
    fun playerScreen_playButton_togglesPlayback() {
        composeTestRule.setContent {
            PlayerScreen(viewModel = fakeViewModel)
        }
        
        composeTestRule
            .onNodeWithContentDescription("Play")
            .performClick()
        
        composeTestRule
            .onNodeWithContentDescription("Pause")
            .assertIsDisplayed()
    }
}
```

**Esfuerzo:** 16h  
**Prioridad:** MEDIA  
**Quarter:** Q3

---

### **5. Qwen Code Extensions**

#### **5.1 Configuración de Extensiones**

**QWEN.md - Sección de Extensiones:**
```json
{
  "extensions": {
    "marketplace": [
      "android-dev-assistant",
      "code-reviewer",
      "testing-expert",
      "gradle-build-optimizer"
    ],
    "mcpServers": {
      "github": {
        "command": "npx",
        "args": ["-y", "@modelcontextprotocol/server-github"],
        "env": {
          "GITHUB_TOKEN": "${GITHUB_TOKEN}"
        }
      },
      "filesystem": {
        "command": "npx",
        "args": ["-y", "@modelcontextprotocol/server-filesystem"],
        "env": {
          "ALLOWED_PATHS": "/workspaces/BoomingMusic"
        }
      },
      "android-lint": {
        "command": "./gradlew",
        "args": ["lintNormalDebug", "--xml"]
      }
    }
  }
}
```

**Esfuerzo:** 2h  
**Prioridad:** ALTA  
**Quarter:** Q1

---

#### **5.2 Subagentes Personalizados**

**QWEN.md - Subagentes:**
```yaml
# Android Architecture Expert
- name: android-architecture-expert
  description: Especialista en arquitectura Android y patrones de diseño
  triggers: ["arquitectura", "patrón", "refactorizar", "clean architecture"]
  system_prompt: |
    Eres experto en arquitectura Android (MVVM, MVI, Clean Architecture).
    Conoces los patrones de BoomingMusic y sugieres mejoras manteniendo consistencia.

# Room Database Expert
- name: room-expert
  description: Especialista en Room database y migraciones
  triggers: ["room", "database", "migración", "dao", "entity"]
  system_prompt: |
    Eres experto en Room database.
    Siempre consideras backward compatibility en migraciones.
    Sugieres índices para optimizar queries.

# Performance Optimizer
- name: performance-optimizer
  description: Analista de rendimiento y optimización
  triggers: ["optimizar", "performance", "memoria", "build time"]
  system_prompt: |
    Eres experto en optimización de Android.
    Identificas bottlenecks y sugieres soluciones prácticas.

# Compose UI Expert
- name: compose-expert
  description: Especialista en Jetpack Compose
  triggers: ["compose", "ui", "composable", "modifier"]
  system_prompt: |
    Eres experto en Jetpack Compose y Material 3.
    Sigues las convenciones de BoomingMusic.
```

**Esfuerzo:** 4h  
**Prioridad:** ALTA  
**Quarter:** Q1

---

### **6. Documentación**

#### **6.1 KDoc Documentation**
**Problema:** Documentación de código inconsistente

**Solución:**
```kotlin
/**
 * Repository principal para operaciones con canciones.
 *
 * Proporciona métodos para obtener, buscar y gestionar canciones
 * desde múltiples fuentes (base de datos, scanner, APIs externas).
 *
 * @property songDao DAO para operaciones de base de datos
 * @property mediaScanner Scanner de archivos independiente
 * @see SongEntity
 * @see ScannedMediaCache
 */
interface SongRepository {
    /**
     * Obtiene todas las canciones como Flow.
     *
     * @return Flow con lista de canciones ordenadas por título
     * @see getSongsByArtist
     * @see searchSongs
     */
    fun getAllSongs(): Flow<List<Song>>
    
    /**
     * Busca canciones por título, artista o álbum.
     *
     * @param query Término de búsqueda (case-insensitive)
     * @return Flow con resultados coincidentes
     */
    fun searchSongs(query: String): Flow<List<Song>>
}
```

**Esfuerzo:** 30h (gradual)  
**Prioridad:** MEDIA  
**Quarter:** Q4

---

#### **6.2 API Documentation**
**Problema:** APIs públicas sin documentación

**Solución:**
```kotlin
/**
 * **API PÚBLICA** - Método para obtener canciones por artista.
 *
 * Este método es utilizado por:
 * - [ArtistDetailViewModel] para mostrar canciones del artista
 * - [LibraryViewModel] para filtrar por artista
 * - [SearchViewModel] para búsqueda
 *
 * Ejemplo de uso:
 * ```kotlin
 * val songsByArtist = repository.getSongsByArtist("Artist Name")
 * songsByArtist.collect { songs ->
 *     // Mostrar canciones
 * }
 * ```
 *
 * @param artistName Nombre exacto del artista
 * @return Flow con lista de canciones ordenadas por año
 * @throws IllegalArgumentException si artistName es vacío
 */
fun getSongsByArtist(artistName: String): Flow<List<Song>>
```

**Esfuerzo:** 20h (gradual)  
**Prioridad:** BAJA  
**Quarter:** Q4

---

## 📈 Métricas de Éxito

### **Q1 2026**
- ✅ ListenBrainz: 100% completado
- ✅ Multi-Artist: 100% completado
- ✅ Tests: 60%+ coverage

### **Q2 2026**
- ✅ Build Time: 30min → 15min (-50%)
- ✅ Tests: 80%+ coverage
- ✅ Géneros: Implementados
- ✅ Flow/LiveData: 100% estandarizado

### **Q3 2026**
- ✅ Jellyfin/Navidrome: Beta funcional
- ✅ Voice Search: Implementado
- ✅ UI Tests: 20+ tests

### **Q4 2026**
- ✅ v2.0.0 Stable Release
- ✅ KDoc: 100% APIs públicas
- ✅ Performance Audit: Completado

---

## 🎯 Checklist de Mejoras Inmediatas

| Categoría | Ítem | Prioridad | Esfuerzo | Quarter |
|-----------|------|-----------|----------|---------|
| **Docs** | Añadir subagentes a QWEN.md | ALTA | 2h | Q1 |
| **Docs** | Actualizar ROADMAP con este plan | ALTA | 4h | Q1 |
| **Build** | Configurar Gradle Build Cache | ALTA | 4h | Q2 |
| **Tests** | Tests para repositories críticos | ALTA | 12h | Q2 |
| **Perf** | Índices de búsqueda en Room | MEDIA | 4h | Q2 |
| **UI** | Auditoría de recursos no utilizados | BAJA | 4h | Q4 |
| **CI** | Reducir tiempos de workflow | ALTA | 8h | Q2 |
| **Code** | Estandarizar Flow vs LiveData | MEDIA | 20h | Q2 |
| **Code** | KDoc en APIs públicas | MEDIA | 30h | Q4 |
| **Arch** | Modularizar Koin modules | MEDIA | 16h | Q2 |

---

## 📞 Contacto y Seguimiento

- **GitHub:** https://github.com/Alain314159/BoomingMusic
- **Telegram:** https://t.me/mardousdev
- **Crowdin:** https://crowdin.com/project/booming-music
- **Ko-fi:** https://ko-fi.com/christiaam

---

*Documento generado el 2 de marzo de 2026*  
*Próxima revisión: 31 de marzo de 2026*
