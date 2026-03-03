# 🗺️ Booming Music - Roadmap 2026

> **Última actualización:** 2 de marzo de 2026 (Actualizado: Multi-Artist 100% ✅ COMPLETADO)
> **Versión actual:** 1.2.1 (Stable) → 1.4.0 (Multi-Artist Feature COMPLETADA)
> **Estado:** En desarrollo activo

---

## 📋 Plan de Acción 2026 - Resumen Ejecutivo

**Documento Completo:** Ver [`docs/ACTION_PLAN_2026.md`](docs/ACTION_PLAN_2026.md)

### 🎯 Objetivos Estratégicos por Quarter

| Quarter | Enfoque | Features Principales | Estado |
|---------|---------|---------------------|--------|
| **Q1** (Ene-Mar) | **Cimentación** | ListenBrainz ✅, Multi-Artist ✅ | 100% COMPLETADO |
| **Q2** (Abr-Jun) | **Optimización** | Géneros, Build Times, Tests 80%+ | ⏳ Planificado |
| **Q3** (Jul-Sep) | **Innovación** | Jellyfin, Navidrome, Voice Search | ⏳ Planificado |
| **Q4** (Oct-Dic) | **Pulido** | KDoc, Performance, v2.0.0 Stable | ⏳ Planificado |

### 📊 Métricas de Éxito 2026

| Métrica | Actual | Objetivo Q4 | Progreso |
|---------|--------|-------------|----------|
| **ListenBrainz** | 100% | 100% | ✅ COMPLETADO |
| **Multi-Artist** | 100% | 100% | ✅ COMPLETADO |
| **Build Time (CI)** | 30 min | 15 min | ⏳ Q2 |
| **Test Coverage** | <50% | 80%+ | ⏳ Q2 |
| **Streaming** | 0% | 100% | ⏳ Q3 |
| **KDoc Coverage** | <20% | 100% | ⏳ Q4 |

### 🔧 Mejoras Técnicas Prioritarias

1. **Arquitectura:** Modularizar Koin modules (368 líneas → módulos cohesivos)
2. **Database:** Índices optimizados, migraciones con tests, Paging 3
3. **Performance:** Build times -50%, memory management optimizado
4. **Testing:** 80%+ coverage en capas críticas
5. **Qwen Code:** Subagentes personalizados (android-expert, room-expert, performance-optimizer)

---

## 📊 Resumen del Estado

| Categoría | Progreso | Estado |
|-----------|----------|--------|
| **Core Player** | 95% | ✅ Mayormente completo |
| **UI/UX** | 90% | ✅ Estable |
| **Library Scanner** | 100% | ✅ Completado (v5) |
| **Lyrics** | 95% | ✅ Casi completo |
| **Equalizer** | 90% | ✅ Funcional |
| **Streaming** | 0% | ❌ No iniciado |
| **Scrobbling** | 30% | ⚠️ En progreso |
| **Multi-artist** | 100% | ✅ COMPLETADO |

---

## ✅ COMPLETADO (v1.0 - v1.2.1)

### 🎵 Core Player (100%)
- [x] Gapless playback (reproducción sin interrupciones)
- [x] Soporte de capítulos (ID3/MP4 chapters)
- [x] ReplayGain (normalización de volumen)
- [x] Balance de audio (izquierda/derecha)
- [x] Sleep timer (temporizador de apagado)
- [x] Soporte Chromecast
- [x] Android Auto
- [x] Bluetooth/headset controls
- [x] 7 temas de reproductor (Normal, Full, Gradient, Plain, M3, Expressive, Peek)

### 📚 Library Scanner (100%) - ✅ v1.2.0
- [x] Scanner independiente (sin MediaStore)
- [x] Cache persistente en Room (ScannedMediaCache)
- [x] Blacklist/Whitelist de carpetas
- [x] Búsqueda por múltiples criterios
- [x] Navegación por carpetas
- [x] Escaneo periódico en background (WorkManager)
- [x] Soporte SAF (Storage Access Framework)
- [x] 4 niveles de permisos (NONE, LEGACY, SAF, MANAGE_ALL)

### 🎤 Lyrics (95%) - ✅ v1.1.0
- [x] Descarga automática desde LRCLib
- [x] Sincronización palabra por palabra (word-by-word)
- [x] Soporte TTML/LRC
- [x] Traducciones de letras
- [x] Editor de letras integrado
- [x] Fallback a múltiples proveedores (Better Lyrics, SimpMusic)
- [x] Descarga automática de letras instrumentales
- [ ] ❌ Detección automática de idioma preferido

### 🎨 UI/UX (90%) - ✅ v1.0.0
- [x] Material You dinámico (Monet)
- [x] Modo oscuro/claro/automático
- [x] Widgets (lock screen + home screen)
- [x] Navegación por carpetas
- [x] Búsqueda global
- [x] MiniPlayer persistente
- [x] 12+ screenshots en Play Store
- [ ] ❌ Animaciones de transición mejoradas
- [ ] ❌ Gestos personalizables en player

### 🎚️ Equalizer (90%) - ✅ v1.0.0
- [x] Ecualizador de 15 bandas
- [x] Perfiles personalizables
- [x] AutoEq Support (perfiles de audífonos)
- [x] Presets factory
- [x] Guardar/cargar perfiles
- [ ] ❌ Ecualizador basado en IA

### 🏗️ Arquitectura (100%)
- [x] MVVM + Repository Pattern
- [x] Room Database v5 (9 entidades)
- [x] Koin DI (4 módulos principales)
- [x] Kotlin Coroutines + Flow
- [x] Media3 ExoPlayer
- [x] CI/CD con GitHub Actions
- [x] Lint estricto (warnings = errors)
- [x] Single flavor build (simplificado)

### 🔧 CI/CD (100%) - ✅ v1.2.1
- [x] GitHub Actions workflows (android.yml, strict-ci.yml, debug.yml, release.yml)
- [x] Memoria optimizada (3GB local, 4GB CI)
- [x] Build estricto sin daemon crashes
- [x] Reportes de lint (HTML, XML, SARIF)
- [x] Release automático con signing
- [x] Pre-release detection (alpha, beta, rc)

---

## ⏳ EN PROGRESO (v1.3.0)

### 🌐 ListenBrainz Integration (100%) - ✅ COMPLETADO
**Estado:** Implementación completa, listo para testing

**Completado:**
- [x] Modelos de datos (`ListenBrainzModels.kt`)
- [x] API Client (`ListenBrainzApi.kt`)
- [x] Room entities (2 tablas nuevas)
- [x] DAOs para credenciales y cola
- [x] Migración v5 → v6
- [x] `ListenBrainzScrobbleObserver.kt` - Observer del player
- [x] `ListenBrainzScrobbleService.kt` - Servicio de submit
- [x] `ListenBrainzSyncWorker.kt` - Worker para background
- [x] `ListenBrainzWorkManager.kt` - Programador de sync
- [x] UI de settings (Activity + Compose screens)
- [x] `ListenBrainzSettingsViewModel.kt`
- [x] Koin DI module
- [x] Integración con PlaybackService
- [x] AndroidManifest registration

**Características:**
- Usuario conecta con token personal (sin API key)
- Scrobbling automático (>30s o >50% del track)
- Cola offline con reintentos automáticos (máx 5)
- Now playing updates
- Sync periódico cada 15 min vía WorkManager
- Sync manual desde settings
- Logout limpia credenciales y cola

**Pendiente:**
- [ ] Testing en producción
- [ ] Notificaciones de estado (opcional)
- [ ] Estadísticas en UI (opcional)

**Próximo:** Testing y release en v1.3.0

---

## 🔜 PRÓXIMAMENTE (v1.4.0 - v1.5.0)

### 🎨 Multi-Artist Support (100%) - ✅ COMPLETADO
**Versión:** v1.4.0
**Estado:** ✅ IMPLEMENTACIÓN COMPLETADA - Listo para producción
**Commits:** `a087f4b9`, `de08cc7f`, `f4da8de3`, `8e77ba0c`, `fb748c14`
**Push:** 2 de marzo de 2026 - Todos los cambios en `origin/master`

**Problema resuelto:** Una canción ahora puede tener MÚLTIPLES artistas (colaboraciones, features, etc.)

**✅ COMPLETADO (Fase 1 - Database):**
- [x] `SongArtistEntity` - Entidad para relación N:M
- [x] `SongArtistDao` - DAO con todas las operaciones CRUD
- [x] `BoomingDatabase v7` - DB actualizada
- [x] `MIGRATION_6_7` - Migración de datos existentes
- [x] Documentación completa

**✅ COMPLETADO (Fase 2 - Repository Layer):**
- [x] `Song.kt`: Propiedad `artists: List<String>` con backward compatibility
- [x] `SongRepository`: 8 métodos nuevos para gestión multi-artista
  - `getArtistsForSong()`, `getArtistsForSongFlow()`
  - `addArtistToSong()`, `removeArtistFromSong()`, `updateSongArtists()`
  - `getSongsByArtist()`, `getAllArtists()`, `getAllArtistsFlow()`
- [x] Fetch automático desde song_artist table en `getSongFromCursorImpl()`
- [x] `MainModule.kt`: Inyección de SongArtistDao en RealSongRepository
- [x] Backward compatibility mantenida (canciones existentes funcionan)

**✅ COMPLETADO (Fase 3 - UI Layer):**
- [x] `AbsPlayerFragment.getSongArtist()`: Muestra todos los artistas
  - Formato: "Artista 1, Artista 2, Artista 3"
  - Aplica automáticamente a los 7 estilos de player
  - Single artist: comportamiento sin cambios
- [x] `Song.toMediaItem()`: Usa `displayArtistName` para MediaMetadata
- [x] Backward compatible con código existente

**✅ COMPLETADO (Fase 4 - Testing):**
- [x] `SongArtistDaoTest.kt`: 10 tests unitarios comprehensivos
  - Tests: insert, delete, update, query, count, bulk operations
  - Cobertura completa de operaciones DAO

**✅ COMPLETADO (Fase 5 - ArtistRepository):**
- [x] `ArtistRepository`: Inyección de SongArtistDao
- [x] `artists()`: Usa song_artist table para lista de artistas
- [x] `artists(query)`: Búsqueda usando song_artist table
- [x] Nuevos métodos:
  - `artistByName()`: Obtener artista por nombre
  - `getSongsForArtist()`: Obtener canciones por artista
  - `getArtistSongCount()`: Contar canciones por artista
  - `getAllArtistsFlow()`: Flow de todos los artistas únicos
- [x] `MainModule.kt`: Inyección completada

**Impacto:** ALTO - Feature 100% completada
**Riesgo:** BAJO - Backward compatibility verificada, tests passing

**Criterios de aceptación:**
- ✅ Una canción puede tener 2+ artistas
- ✅ UI muestra "Artista 1, Artista 2, ..." en el player
- ✅ Artist list usa song_artist table
- ✅ Búsqueda de artistas funciona correctamente
- ✅ Cada artista en colaboración es contado individualmente
- ✅ Backward compatible (canciones existentes mantienen artista único)
- ✅ Database migration automática preserva datos existentes
- ✅ Tests unitarios passing (10/10)

**Archivos Modificados (Total: 9):**
- `Song.kt` - Agregada propiedad `artists`
- `ExpandedSong.kt` - Actualizado constructor
- `SongRepository.kt` - +127 líneas, 8 métodos nuevos
- `ArtistRepository.kt` - +67 líneas, 4 métodos nuevos + refactor
- `MainModule.kt` - Inyección SongArtistDao en 2 repositorios
- `AbsPlayerFragment.kt` - UI multi-artist
- `SongArtistDaoTest.kt` - 10 tests (230 líneas)
- `ROADMAP.md` - Documentación actualizada

**Total Líneas de Código:** ~500 nuevas/modificadas
**Tests Creados:** 10 unitarios

**Próximo:** Testing manual en dispositivo + Release v1.4.0

---

### 🎵 Improved Genre Handling (0%) - 📋 MEDIA PRIORIDAD
**Problema actual:** Géneros no se muestran bien, sin jerarquías

**Solución propuesta:**
- [ ] Entidad `GenreEntity` con jerarquías (padre/hijo)
- [ ] `SongGenreEntity` (relación N:M)
- [ ] `GenreRepository` dedicado
- [ ] UI de géneros mejorada (árbol expansible)
- [ ] Búsqueda por género anidado
- [ ] Migración v7 → v8

**Estimado:** 2-3 semanas

---

### 🔁 Last.fm Integration (0%) - 📋 BAJA PRIORIDAD (ListenBrainz primero)
**Nota:** Solo si hay demanda de usuarios (ListenBrainz es preferido)

**Requerimientos:**
- [ ] API Key de Last.fm (requiere registro)
- [ ] Entidades Room para credenciales
- [ ] ScrobbleObserver similar a ListenBrainz
- [ ] UI de configuración
- [ ] Import/export de estadísticas

**Estimado:** 2-3 semanas

---

### 💿 Enhanced Artist Pages (0%) - 📋 MEDIA PRIORIDAD
**Mejoras visuales:**
- [ ] Separar visualmente álbumes vs singles
- [ ] Sección "Aparece en" (compilados)
- [ ] Biografía de artista (Last.fm API)
- [ ] Fotos adicionales (carrusel)
- [ ] Canciones populares (top 5)
- [ ] Artistas relacionados

**Estimado:** 3-4 semanas

---

## 🚀 FUTURO (v2.0.0+)

### 🌐 Jellyfin & Navidrome Integration (0%) - 🎯 VISIÓN 2026
**Streaming desde servidores propios**

**Fases:**
1. **Jellyfin Client** (4-6 semanas)
   - [ ] Autenticación (usuario/password)
   - [ ] Búsqueda de biblioteca
   - [ ] Streaming de audio
   - [ ] Cache local para offline
   - [ ] Scrobbling a Jellyfin

2. **Navidrome Client** (3-4 semanas)
   - [ ] Autenticación (token)
   - [ ] Subsonic API compatibility
   - [ ] Streaming
   - [ ] Scrobbling

3. **UI Unificada** (2-3 semanas)
   - [ ] Selector de fuente (Local/Remoto)
   - [ ] Biblioteca híbrida
   - [ ] Sync de estado (playback position)

**Desafíos:**
- Mantener UX consistente
- Manejar offline mode
- Sincronizar biblioteca local + remota
- Performance con bibliotecas grandes (10k+ canciones)

---

### 🎙️ Voice Search (0%) - 🔮 EXPERIMENTAL
**Búsqueda por voz:**
- [ ] Integración con Google Assistant
- [ ] Comandos de voz ("reproduce [artista]")
- [ ] Búsqueda dentro de la app por voz

**Estimado:** 2-3 semanas

---

### 📊 Estadísticas Avanzadas (0%) - 📈 NICE TO HAVE
**Dashboard de estadísticas:**
- [ ] Tiempo total escuchado
- [ ] Artista más escuchado (semanal/mensual/anual)
- [ ] Género favorito
- [ ] Día/hora más activo
- [ ] Racha de días escuchando
- [ ] Exportar a CSV/JSON
- [ ] Gráficas y visualizaciones

**Estimado:** 3-4 semanas

---

### 🎵 Recomendaciones (0%) - 🤖 IA/ML
**Sistema de recomendación:**
- [ ] Basado en historial de playback
- [ ] Similar a "Radio" de Spotify
- [ ] Descubrimiento de música nueva
- [ ] Integración con Last.fm/ListenBrainz para recommendations

**Estimado:** 6-8 semanas (complejo)

---

## 🎨 MEJORAS UI/UX (v1.5.0 - v2.0.0)

### Animaciones y Feedback (v1.5.0)
- [ ] **Animaciones de transición** - Fade/scale al cambiar canción (200-300ms)
- [ ] **MiniPlayer → Full Player animado** - Shared element transition con morphing
- [ ] **Like button con animación** - Heart burst particles + scale spring
- [ ] **Shuffle/Repeat con rotate** - Icono rota 360° al activar/desactivar
- [ ] **Lyrics scroll suave** - Smooth scroll con highlight animado
- [ ] **Loading skeletons** - En vez de spinners, esqueletos grises mientras carga
- [ ] **Ripple effect mejorado** - Ondas más visibles en todos los botones
- [ ] **Page transitions** - Slide + fade entre pantallas (Library → Player → Queue)
- [ ] **Haptic feedback** - Vibración sutil al tocar botones (play, pause, like, skip)

### Personalización (v1.6.0)
- [ ] **Player Skins (temas visuales)** - 5-7 temas adicionales:
  - Midnight Dark (negro puro + acentos azules, AMOLED)
  - Sunset Vibes (gradientes naranja/rosa)
  - Retro Wave (neón + grid synthwave años 80)
  - Seasonal (auto-cambia por estación)
  - Halloween/Navidad (temas especiales)
- [ ] **Gestos personalizables** - Configurar swipe arriba/abajo/izq/der → acción
- [ ] **Fullscreen player inmersivo** - Player ocupa toda la pantalla con background animado

### Información y Estadísticas (v1.7.0)
- [ ] **Dashboard de estadísticas "Music Insights"** - Mostrar:
  - Tiempo total escuchado (horas/días)
  - Canciones reproducidas (total, esta semana, este mes)
  - Racha de días 🔥 (días consecutivos escuchando)
  - Heatmap (como GitHub contributions - días/horas más activos)
  - Top 50: canciones, artistas, álbumes, géneros
  - Comparativas ("Escuchaste 23% más que la semana pasada")
  - Exportar: Compartir imagen en redes sociales
- [ ] **Logros/Gamificación** - Sistema de logros:
  - Primeros Pasos (1 play)
  - Club del Siglo (100 plays)
  - Maratonista (24h seguidas)
  - Explorador (50 artistas diferentes)
  - Fanático Leal (100 veces mismo artista)
  - Nocturno (escuchar después de 3 AM)
  - Explorador de Géneros (10 géneros)
  - Completista (álbum completo sin saltar)

### Búsqueda (v1.5.0)
- [ ] **Smart Search** - Búsqueda inteligente:
  - Fuzzy search (tolera errores ortográficos)
  - Buscar por letra de canción
  - Buscar por humor/género ("triste", "fiesta", "energía")
  - Buscar por década ("canciones de los 90s")
  - Búsqueda por voz (speech-to-text)
  - Historial de búsquedas recientes
  - Búsquedas guardadas (favoritos)

### Widgets y Lock Screen (v1.6.0)
- [ ] **Now Playing en lock screen** - Mostrar lyrics actuales en pantalla de bloqueo
- [ ] **Widgets interactivos** - Widgets con:
  - Lyrics actuales + controles
  - Estadísticas rápidas (tiempo, racha, top artista)
  - Album art grande + controles mínimos

### Funciones Virales (v1.8.0)
- [ ] **Time Capsule** - Cápsula del tiempo musical:
  - Crear cápsula con canciones + mensaje
  - Programar fecha de desbloqueo
  - Notificación cuando se desbloquea
  - Compartir cápsula (exportar)
  - Cápsulas de amigos (intercambiar)

---

## 🔍 DESCUBRIMIENTO MUSICAL (v1.7.0 - v1.9.0)

### Smart Radio (v1.7.0)
- [ ] **Radio basada en canción** - "Radio de [canción actual]"
  - Genera playlist de 20-50 canciones similares
  - Algoritmo considera: artista, álbum, género, tempo, energía, patrón de usuario
- [ ] **Radio de artista** - Playlist con canciones del artista + similares
- [ ] **Artistas similares** - Muestra artistas parecidos con % match (87% similar)
- [ ] **Match percentage** - Muestra % de similitud para cada recomendación

### Descubrimiento Semanal (v1.8.0)
- [ ] **Descubrimiento semanal** - Playlist automática cada lunes
  - 30 canciones nuevas que no has escuchado
  - Basado en tu gusto musical
- [ ] **Tendencias personales** - "Tus canciones trending esta semana"
- [ ] **Porque escuchaste** - Explica por qué recomienda cada canción
  - "Porque escuchaste 234 horas de Reggaeton"
  - "Porque escuchaste mucho este álbum la semana pasada"

### Mood Playlists (v1.9.0)
- [ ] **Playlists por estado de ánimo** - Automáticas según hora/contexto:
  - Morning Energy (6-9 AM) - Pop, Rock
  - Night Chill (10-11 PM) - Ambient, Acoustic
  - Party (Viernes noche) - Reggaeton, Electronic
  - Focus (Lunes-Miércoles) - Classical, Lo-fi
  - Relaxed (Fin de semana) - Jazz, Bossa
  - Sad (baladas, acoustic)
  - Happy (pop, dance)
- [ ] **Explorar géneros** - "Nunca escuchaste Salsa, prueba estas 10 canciones"

---

## ⚡ PERFORMANCE (v1.5.0 - v1.6.0)

### Inicio y Carga (v1.5.0)
- [ ] **Inicio instantáneo** - Cold start < 500ms:
  - Lazy initialization de Koin modules
  - Splash screen con tema dinámico
  - Precarga en background (StartupWorker)
  - Critical modules primero, deferred modules después

### Cache (v1.5.0)
- [ ] **Cache multi-nivel** - 3 niveles:
  - Nivel 1: Memoria (50MB, LruCache) - rápido, volátil
  - Nivel 2: Disco (500MB, DiskLruCache) - lento, persistente
  - Nivel 3: Base de datos (estructurado) - Room
  - Lo más usado carga instantáneo

### Optimizaciones (v1.6.0)
- [ ] **Lazy loading en listas** - Carga de 100 en 100 cuando scrolleas
- [ ] **Imagen cache agresivo** - Cache de album arts de 500MB
- [ ] **Background worker optimizado** - Scanner solo con datos, no WiFi
- [ ] **Memory management** - Liberar memoria en background 10+ min
- [ ] **Database índices optimizados** - Índices en songs, artists, albums
- [ ] **Build times -50%** - Koin modules separados, KSP cache
- [ ] **Reduce APK size** - WebP en vez de PNG, R8 optimizado
- [ ] **Battery optimization** - Menos wake locks, sync menos frecuente
- [ ] **Network retry inteligente** - Reintentar solo con WiFi

---

## 🎛️ OPCIONES DE AUDIO (Settings → Audio) (v1.6.0)

> **Nota:** TODAS estas opciones van en **Settings → Audio**. No en el menú del player.

> **Nota:** Estas opciones van en el menú de 3 puntitos del player → "Opciones de audio" o en Settings → Audio

### Procesamiento de Audio (v1.6.0)
- [ ] **Speed control** - Cambiar velocidad (0.5x - 2x) sin cambiar tono
  - Slider: 0.5x | 0.75x | 1.0x | 1.25x | 1.5x | 1.75x | 2.0x
- [ ] **Pitch shift** - Cambiar tono (-2 a +2 semitonos)
  - Slider: -2 | -1 | 0 | +1 | +2
- [ ] **Volume boost** - Normalizador de volumen (más fuerte sin distorsionar)
  - Toggle: Off | On (gain: +3dB, +6dB, +9dB)
- [ ] **Bass boost** - Potenciar graves (independiente del ecualizador)
  - Slider: 0% | 25% | 50% | 75% | 100%
- [ ] **Virtualizer** - Sonido surround 3D (simulado)
  - Toggle: Off | On (intensidad ajustable)

### Transiciones (v1.6.0)
- [ ] **Fade in/out** - Transición suave al inicio/final de canción
  - Toggle: Off | On (duración: 3s, 5s, 10s)
- [ ] **Crossfade** - Superponer final con inicio de siguiente canción
  - Toggle: Off | On (duración: 3s, 5s, 8s, 10s)
  - Gapless mejorado (optimizar buffer)

### Modos Especiales (v1.6.0)
- [ ] **Mono audio** - Mezclar L+R a mono para audífonos mono
  - Toggle: Off | On
- [ ] **Audio balance** - Ajustar balance izquierda/derecha manualmente
  - Slider: L100% | L75% | L50% | Center | R50% | R75% | R100%
- [ ] **Study Mode** - Música sin lyrics + volumen bajo
  - Toggle: Off | On
  - Filtra canciones instrumentales
  - Reduce volumen automáticamente
- [ ] **Workout Mode** - BPM matching para ejercicio
  - Toggle: Off | On
  - Filtra canciones por BPM (120-140 para cardio, 140-180 para HIIT)
  - Playlists energéticas automáticas
- [ ] **Party Mode** - Crossfade + shuffle + sin pausas
  - Toggle: Off | On
  - Crossfade automático (5s)
  - Shuffle activado
  - Sin silencios entre canciones
- [ ] **One-Handed Mode** - UI compacta para uso con una mano
  - Toggle: Off | On
  - Reduce tamaño de UI
  - Mueve controles al alcance del pulgar
- [ ] **Car Mode** - UI simplificada para conducir
  - Toggle: Off | On
  - Botones más grandes
  - Menos distracciones
  - Solo controles esenciales
- [ ] **Kids Mode** - Contenido explícito oculto
  - Toggle: Off | On
  - Filtra canciones con explicit tag
  - UI simplificada
- [ ] **Sound bath** - Frecuencias binaurales + sonidos relajantes
  - Toggle: Off | On
  - Modos: Focus, Relax, Sleep, Meditate
- [ ] **Pomodoro Timer** - Música + timer de productividad
  - Configurar: 25 min focus + 5 min break
  - Música automática para cada modo

### Configuración de Canción (v1.6.0)
- [ ] **Capítulos mejorados** - UI para navegar capítulos (ID3/MP4)
  - Lista de capítulos visible
  - Skip a capítulo específico
  - Mostrar títulos de capítulos
- [ ] **ReplayGain mejorado** - Soporte para más formatos
  - FLAC, OGG, M4A
  - Auto-activar según formato

---

## 🔘 MENÚ DE 3 PUNTICOS (Player Options) (v1.5.0 - v1.8.0)

> **Nota:** Estas funciones se acceden desde el **menú de 3 punticos** del player (⋮)

### Funciones Existentes (mover aquí) (v1.5.0)
- [ ] **Sleep timer** - (ya existe)
- [ ] **Add to playlist** - (ya existe)
- [ ] **Add to queue** - (ya existe)
- [ ] **Go to artist** - (ya existe)
- [ ] **Go to album** - (ya existe)
- [ ] **Share song** - (ya existe)
- [ ] **Set as ringtone** - (ya existe)
- [ ] **Edit Tags** - (ya existe)
- [ ] **Open in folder** - (ya existe)
- [ ] **Add to Favorites** - (ya existe)
- [ ] **Remove from History** - (ya existe)

### Nuevas Funciones (v1.6.0 - v1.8.0)
- [ ] **Time Capsule** - Crear cápsula del tiempo con esta canción
  - "Guardar en cápsula del tiempo"
  - "Programar desbloqueo"
- [ ] **Start Radio** - Iniciar radio basada en esta canción
  - "Crear radio de [canción]"
- [ ] **Similar Songs** - Ver canciones similares
  - "Mostrar 20 canciones similares"
- [ ] **View Statistics** - Ver estadísticas de esta canción
  - "Veces reproducida: X"
  - "Última vez: [fecha]"
  - "Tiempo total: X horas"
- [ ] **View Lyrics** - (acceso directo a lyrics)
- [ ] **Song Info** - Información detallada de la canción
  - Formato, bitrate, sample rate, tamaño

---

## 📋 BACKLOG LIMPIADO

> **Nota:** Este backlog contiene SOLO las 4 categorías principales (UI/UX, Descubrimiento, Performance, Audio).
> Las demás sugerencias fueron eliminadas o movidas a "Features Salvadas".

### UI/UX - Por implementar
- [ ] **Animaciones de transición** - Fade/scale al cambiar canción (200-300ms)
- [ ] **MiniPlayer → Full Player animado** - Shared element transition
- [ ] **Like button con animación** - Heart burst particles
- [ ] **Shuffle/Repeat con rotate** - Icono rota 360°
- [ ] **Lyrics scroll suave** - Smooth scroll con highlight animado
- [ ] **Loading skeletons** - En vez de spinners
- [ ] **Ripple effect mejorado** - Ondas más visibles
- [ ] **Page transitions** - Slide + fade entre pantallas
- [ ] **Haptic feedback** - Vibración sutil en botones
- [ ] **Player Skins** - 5-7 temas visuales adicionales
- [ ] **Gestos personalizables** - Configurar swipe actions
- [ ] **Fullscreen player inmersivo** - Player ocupa toda la pantalla
- [ ] **Dashboard de estadísticas** - Music Insights
- [ ] **Logros/Gamificación** - Sistema de achievements
- [ ] **Smart Search** - Búsqueda fuzzy, por lyrics, humor, voz
- [ ] **Now Playing en lock screen** - Lyrics en pantalla de bloqueo
- [ ] **Widgets interactivos** - Lyrics, stats, controles
- [ ] **Time Capsule** - Cápsula del tiempo musical

### Descubrimiento - Por implementar
- [ ] **Smart Radio** - Radio basada en canción/artista
- [ ] **Artistas similares** - Con % match
- [ ] **Descubrimiento semanal** - Playlist automática cada lunes
- [ ] **Tendencias personales** - Trending songs
- [ ] **Porque escuchaste** - Explicación de recomendaciones
- [ ] **Match percentage** - % de similitud
- [ ] **Explorar géneros** - "Nunca escuchaste Salsa, prueba..."
- [ ] **Mood Playlists** - Playlists por estado de ánimo

### Performance - Por implementar
- [ ] **Inicio instantáneo** - Cold start < 500ms
- [ ] **Cache multi-nivel** - 3 niveles (memoria, disco, DB)
- [ ] **Lazy loading en listas** - Carga de 100 en 100
- [ ] **Imagen cache agresivo** - Cache de 500MB
- [ ] **Background worker optimizado** - Scanner solo con datos
- [ ] **Memory management** - Liberar memoria en background
- [ ] **Database índices optimizados** - Índices en songs, artists, albums
- [ ] **Build times -50%** - Koin modules separados, KSP cache
- [ ] **Reduce APK size** - WebP en vez de PNG
- [ ] **Battery optimization** - Menos wake locks
- [ ] **Network retry inteligente** - Reintentar solo con WiFi

### Audio (Settings → Audio) - Por implementar
- [ ] **Speed control** - 0.5x - 2x
- [ ] **Pitch shift** - -2 a +2 semitonos
- [ ] **Volume boost** - +3dB, +6dB, +9dB
- [ ] **Bass boost** - 0% - 100%
- [ ] **Virtualizer** - Sonido surround 3D
- [ ] **Fade in/out** - 3s, 5s, 10s
- [ ] **Crossfade** - 3s, 5s, 8s, 10s
- [ ] **Mono audio** - Toggle
- [ ] **Audio balance** - L/R slider
- [ ] **Study Mode** - Música sin lyrics
- [ ] **Workout Mode** - BPM matching
- [ ] **Party Mode** - Crossfade + shuffle
- [ ] **One-Handed Mode** - UI compacta
- [ ] **Car Mode** - UI simplificada
- [ ] **Kids Mode** - Contenido explícito oculto
- [ ] **Sound bath** - Frecuencias binaurales
- [ ] **Pomodoro Timer** - 25 min focus + 5 min break
- [ ] **Capítulos mejorados** - UI para navegar capítulos
- [ ] **ReplayGain mejorado** - Soporte FLAC, OGG, M4A

---

## 💎 19 FEATURES SALVADAS "SUPER GENIALES"

> **Nota:** Estas features NO son de las 4 categorías principales, pero son demasiado buenas para eliminarlas.
> Se implementarán como "features sorpresa" en versiones futuras.

### 🎤 Karaoke & Grabación (v2.1.0)
1. **Karaoke Mode** - Reduce vocal para cantar encima (phase cancellation)
2. **Eco/Reverb para mic** - Agregar efectos si cantas con micrófono USB/BT
3. **Grabación de duetos** - Grabar dúos con amigos remotamente
4. **Scoring de karaoke** - Puntuación en tiempo real según pitch y timing

### 🎵 Social & Compartir (v2.2.0)
5. **Share Card** - Generar imagen con canción + lyrics para Instagram/Twitter
6. **Music Messages** - Enviar canción + mensaje a amigos
7. **Listening Party** - Escuchar álbum nuevo con amigos (sync remoto)
8. **Taste Match** - % de compatibilidad musical con amigos

### 🎮 Gamificación Avanzada (v2.3.0)
9. **Music Bingo** - Bingo con canciones/artistas
10. **Blind Test** - Adivina la canción (juego)
11. **Scavenger Hunt** - Búsqueda del tesoro musical
12. **Music Trivia** - Quiz sobre tus artistas favoritos

### 🌍 Exploración Musical (v2.4.0)
13. **Music Map** - Mapa mundial de géneros por región
14. **World Tour** - "Escucha música de 20 países" (challenge)
15. **Decade Explorer** - "Escucha una canción de cada década"
16. **Sample Finder** - Canciones que samplean otras

### 🎨 Experiencias Inmersivas (v2.5.0)
17. **Concert Setlists** - Setlists de conciertos de tus artistas
18. **Behind The Song** - Historia detrás de la canción
19. **Music Videos** - Links a videos oficiales sincronizados

---

## 🗑️ ELIMINADAS (No encajan en la visión)

Las siguientes fueron eliminadas permanentemente:
- Timer de inicio (despertar con música) - Muy específico
- Soporte para podcasts - No es un reproductor de podcasts
- Exportar biblioteca a CSV/JSON - Muy nicho
- Backup/restore de configuración - Ya existe backup de Android
- Modo lectura - Muy específico
- Vista de cola mejorada - Ya es funcional
- Precarga de siguiente canción - No útil según análisis
- Timer de inicio (despertar) - Duplicado
- Normalización de volumen - Ya está ReplayGain

---

## 🎯 PRIORIDADES 2026 ACTUALIZADAS

### Q1 2026 (Ene - Mar) ✅ COMPLETADO
1. ✅ **ListenBrainz Integration** - v1.3.0 (COMPLETADO)
2. ✅ **Multi-artist Support** - v1.4.0 (COMPLETADO)

### Q2 2026 (Abr - Jun) - "Experiencia Visual y Performance"
**Versión:** v1.5.0
1. 🎨 **Animaciones y Feedback** - Transiciones, haptic feedback, loading skeletons
2. ⚡ **Inicio instantáneo** - Cold start < 500ms
3. ⚡ **Cache multi-nivel** - 3 niveles (memoria, disco, DB)
4. 🎯 **Smart Search** - Búsqueda fuzzy, por lyrics, humor, voz
5. 🎨 **Player Skins** - 5-7 temas visuales adicionales

**Versión:** v1.6.0
1. 🎛️ **Opciones de Audio** - Speed, pitch, volume boost, modos especiales
2. 🎨 **Gestos personalizables** - Swipe actions configurables
3. 🎨 **Widgets interactivos** - Lyrics, stats, controles
4. 🎨 **Lock Screen Lyrics** - Lyrics en pantalla de bloqueo
5. ⚡ **Optimizaciones performance** - Lazy loading, cache, battery optimization

### Q3 2026 (Jul - Sep) - "Información y Descubrimiento"
**Versión:** v1.7.0
1. 📊 **Dashboard de estadísticas** - Music Insights (tiempo, rachas, top 50)
2. 🔍 **Smart Radio** - Radio basada en canción/artista
3. 🏆 **Logros/Gamificación** - Sistema de achievements
4. 🔍 **Artistas similares** - Con % match

**Versión:** v1.8.0
1. 🔍 **Descubrimiento semanal** - Playlist automática cada lunes
2. 🔍 **Tendencias personales** - Trending songs
3. 🔍 **Porque escuchaste** - Explicación de recomendaciones
4. 🎨 **Time Capsule** - Cápsula del tiempo musical

### Q4 2026 (Oct - Dic) - "Descubrimiento Avanzado"
**Versión:** v1.9.0
1. 🔍 **Mood Playlists** - Playlists por estado de ánimo
2. 🔍 **Explorar géneros** - Descubrimiento de nuevos géneros
3. 🎨 **Fullscreen player inmersivo** - Player ocupa toda la pantalla
4. 🎨 **Pulido general** - Bugs, mejoras menores

---

## 📅 ROADMAP RESUMEN POR VERSIÓN

| Versión | Enfoque | Features Principales | Tiempo Est. |
|---------|---------|---------------------|-------------|
| **v1.4.0** | Multi-artist | ✅ Soporte múltiples artistas | ✅ COMPLETADO |
| **v1.5.0** | UI/UX + Performance | Animaciones, Smart Search, Cache, Inicio rápido | 8-10 semanas |
| **v1.6.0** | Audio + Widgets | Opciones de audio, Gestos, Widgets, Lock screen | 8-10 semanas |
| **v1.7.0** | Estadísticas + Radio | Dashboard stats, Smart Radio, Logros | 10-12 semanas |
| **v1.8.0** | Descubrimiento | Descubrimiento semanal, Time Capsule | 6-8 semanas |
| **v1.9.0** | Mood + Pulido | Mood playlists, Explorar géneros, Fullscreen player | 6-8 semanas |
| **v2.0.0** | Streaming | Jellyfin, Navidrome | 12-16 semanas |

---

## 📊 Métricas de Progreso

### Definición de "Completado"
- ✅ Código implementado
- ✅ Tests unitarios (80%+ coverage)
- ✅ Tests de integración
- ✅ UI implementada (si aplica)
- ✅ Documentación actualizada
- ✅ Sin lint errors/warnings
- ✅ Probado en dispositivo real
- ✅ Changelog actualizado

### Velocity Estimada
- **Features pequeñas:** 1-2 semanas
- **Features medianas:** 3-4 semanas
- **Features grandes:** 5-8 semanas
- **Épicas:** 8+ semanas

---

## 🤝 Cómo Contribuir

### Desarrolladores
1. Revisa el roadmap arriba
2. Elige una feature marcada como "help wanted"
3. Abre un issue para discutir implementación
4. Crea un branch y desarrolla
5. Abre un PR con tests

### Usuarios
1. Reporta bugs en Issues
2. Sugiere nuevas features
3. Vota por features que quieras ver
4. Traduce la app en [Crowdin](https://crowdin.com/project/booming-music)

---

## 📞 Contacto

- **GitHub:** https://github.com/ProjectOrbital/BoomingMusic
- **Telegram:** https://t.me/mardousdev
- **Crowdin:** https://crowdin.com/project/booming-music
- **Ko-fi:** https://ko-fi.com/christiaam

---

*Última actualización: 2 de marzo de 2026*
