# 🗺️ Booming Music - Roadmap 2026

> **Última actualización:** 2 de marzo de 2026  
> **Versión actual:** 1.2.1 (Stable)  
> **Estado:** En desarrollo activo

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
| **Multi-artist** | 0% | ❌ Pendiente |

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

### 🎨 Multi-Artist Support (25%) - 🚧 EN PROGRESO
**Versión:** v1.4.0  
**Estimado:** 4-6 semanas

**Problema actual:** Una canción solo puede tener UN artista

**✅ COMPLETADO (Fase 1 - Database):**
- [x] `SongArtistEntity` - Entidad para relación N:M
- [x] `SongArtistDao` - DAO con todas las operaciones CRUD
- [x] `BoomingDatabase v7` - DB actualizada
- [x] `MIGRATION_6_7` - Migración de datos existentes
- [x] Documentación completa

**⏳ PENDIENTE:**
- [ ] **Fase 2: Repository** (8h)
  - [ ] Actualizar `SongRepository`
  - [ ] Actualizar `ArtistRepository`
  - [ ] `Song` model: `artistName` → `List<String> artists`
  - [ ] Mappers actualizados

- [ ] **Fase 3: UI** (12h)
  - [ ] Player UI - mostrar múltiples artistas
  - [ ] Artist library - usar nueva tabla
  - [ ] Tag editor - editar múltiples artistas
  - [ ] Search - búsqueda por cualquier artista

- [ ] **Fase 4: Testing** (6h)
  - [ ] Unit tests para DAO
  - [ ] Integration tests
  - [ ] Migration tests

**Impacto:** ALTO - Cambia estructura de datos fundamental  
**Riesgo:** MEDIO - Migración automática preserva datos

**Criterios de aceptación:**
- ✅ Una canción puede tener 2+ artistas
- ✅ UI muestra "Artista 1, Artista 2, ..."
- ✅ Click en artista → ArtistDetail
- ✅ Búsqueda por cualquier artista funciona
- ✅ Backward compatible (canciones existentes)

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

## 📋 BACKLOG (Ideas para considerar)

### Funcionalidades Menores
- [ ] Timer de inicio (despertar con música)
- [ ] Crossfade entre canciones
- [ ] Normalización de volumen (ya está ReplayGain)
- [ ] Soporte para podcasts
- [ ] Modo coche mejorado
- [ ] Gestos personalizables en player
- [ ] Temas personalizados (colores manuales)
- [ ] Exportar biblioteca a CSV/JSON
- [ ] Backup/restore de configuración
- [ ] Modo fiesta (crossfade + shuffle)

### UI/UX
- [ ] Animaciones de transición mejoradas
- [ ] Gestos de deslizar en miniplayer
- [ ] Vista de letras en pantalla de bloqueo
- [ ] Widgets personalizables (tamaños adicionales)
- [ ] Modo lectura (ocultar UI temporalmente)
- [ ] Vista de cola mejorada (arrastrar para reorder)

### Performance
- [ ] Lazy loading en listas grandes (1000+ items)
- [ ] Cache de imágenes más agresivo
- [ ] Precarga de siguiente canción
- [ ] Reducir uso de memoria en background
- [ ] Optimizar scanner para bibliotecas enormes

### Accesibilidad
- [ ] Soporte completo para TalkBack
- [ ] Textos más grandes (dynamic type)
- [ ] Alto contraste
- [ ] Navegación por teclado (Android TV)

---

## 🎯 PRIORIDADES 2026

### Q1 2026 (Ene - Mar)
1. ✅ **ListenBrainz Integration** - v1.3.0 (EN PROGRESO)
2. 🔄 **Multi-artist Support** - v1.4.0 (PLANIFICADO)

### Q2 2026 (Abr - Jun)
1. 📋 **Improved Genre Handling** - v1.4.1
2. 📋 **Enhanced Artist Pages** - v1.5.0

### Q3 2026 (Jul - Sep)
1. 🔮 **Jellyfin Integration** - v2.0.0-beta
2. 📋 **Navidrome Integration** - v2.0.0

### Q4 2026 (Oct - Dic)
1. 📊 **Estadísticas Avanzadas** - v2.1.0
2. 🎨 **Pulido general y bugs** - v2.1.x

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
