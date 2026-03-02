# 🎨 Multi-Artist Support - Estado de Implementación

> **Fecha:** 2 de marzo de 2026  
> **Estado:** 🚧 EN PROGRESO (Fase 1 completada)  
> **Versión:** v1.4.0 (Planificado)

---

## 📊 Progreso General

| Fase | Estado | Progreso |
|------|--------|----------|
| **Fase 1: Database** | ✅ COMPLETADA | 100% |
| **Fase 2: Repository** | ⏳ PENDIENTE | 0% |
| **Fase 3: UI** | ⏳ PENDIENTE | 0% |
| **Fase 4: Testing** | ⏳ PENDIENTE | 0% |

**Progreso Total:** 25%

---

## ✅ FASE 1: Database (COMPLETADA)

### Archivos Creados

#### 1. **SongArtistEntity** ✅
**Archivo:** `data/local/room/entity/SongArtistEntity.kt`

**Estructura:**
```kotlin
@Entity(
    tableName = "song_artist",
    foreignKeys = [ForeignKey(...)],
    indices = [...]
)
data class SongArtistEntity(
    val songId: Long,        // Referencia a SongEntity
    val artistName: String,  // Nombre del artista
    val artistOrder: Int = 0 // Orden en la lista (0 = principal)
)
```

**Características:**
- ✅ Relación N:M entre canciones y artistas
- ✅ Foreign key con CASCADE DELETE
- ✅ Índice compuesto único (song_id, artist_name)
- ✅ Campo artist_order para mantener orden de features

#### 2. **SongArtistDao** ✅
**Archivo:** `data/local/room/dao/SongArtistDao.kt`

**Métodos implementados:**
```kotlin
// Consultas
getArtistsForSongFlow(songId): Flow<List<SongArtistEntity>>
getArtistsForSong(songId): List<SongArtistEntity>
getArtistNamesForSong(songId): List<String>
getSongsForArtist(artistName): List<SongArtistEntity>
getAllArtistsFlow(): Flow<List<String>>
countSongsForArtist(artistName): Int

// Inserción
insert(songArtist: SongArtistEntity)
insertAll(songArtists: List<SongArtistEntity>)

// Eliminación
delete(songArtist: SongArtistEntity)
deleteAllForSong(songId)
deleteBySongId(songId)

// Actualización
updateArtistOrder(songId, artistName, order)
```

#### 3. **BoomingDatabase** ✅
**Archivo modificado:** `core/BoomingDatabase.kt`

**Cambios:**
- ✅ Entidad agregada: `SongArtistEntity::class`
- ✅ DAO agregado: `abstract fun songArtistDao(): SongArtistDao`
- ✅ Versión incrementada: 6 → 7
- ✅ Migración `MIGRATION_6_7` creada

**Migración incluye:**
```sql
-- Crear tabla song_artist
CREATE TABLE song_artist (
    song_id INTEGER NOT NULL,
    artist_name TEXT NOT NULL,
    artist_order INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY(song_id, artist_name),
    FOREIGN KEY(song_id) REFERENCES SongEntity(song_key) ON DELETE CASCADE
)

-- Crear índices
CREATE INDEX index_song_artist_song_id ON song_artist(song_id)
CREATE INDEX index_song_artist_artist_name ON song_artist(artist_name)

-- Migrar datos existentes
INSERT INTO song_artist (song_id, artist_name, artist_order)
SELECT song_key, artist_name, 0
FROM SongEntity
WHERE artist_name IS NOT NULL AND artist_name != ''
```

---

## ⏳ FASE 2: Repository (PENDIENTE)

### Trabajo Requerido

#### 1. **Actualizar SongRepository**
**Archivo:** `data/local/repository/SongRepository.kt`

**Cambios necesarios:**
- [ ] Agregar método `getArtistsForSong(songId): List<String>`
- [ ] Agregar método `addArtistToSong(songId, artistName, order)`
- [ ] Agregar método `removeArtistFromSong(songId, artistName)`
- [ ] Agregar método `updateSongArtists(songId, List<String> artists)`
- [ ] Método para obtener canciones por artista: `getSongsByArtist(artistName)`

#### 2. **Actualizar ArtistRepository**
**Archivo:** `data/local/repository/ArtistRepository.kt`

**Cambios necesarios:**
- [ ] Usar `SongArtistDao` en lugar de buscar por `SongEntity.artistId`
- [ ] Método `getAllArtists()` que use la nueva tabla
- [ ] Método `getArtistSongCount(artistName)`
- [ ] Actualizar queries de búsqueda de artistas

#### 3. **Actualizar Mapper**
**Archivo:** `data/mapper/` (crear o actualizar)

**Necesario:**
- [ ] Mapper `SongEntity → Song` que incluya lista de artistas
- [ ] Mapper `Song → SongEntity` que guarde múltiples artistas
- [ ] Actualizar `toMediaItem()` para mostrar todos los artistas

---

## ⏳ FASE 3: UI (PENDIENTE)

### Componentes a Actualizar

#### 1. **Song Model**
**Archivo:** `data/model/Song.kt`

**Cambios:**
- [ ] Cambiar `val artistName: String` por `val artists: List<String>`
- [ ] Propiedad computada `primaryArtist: String` (primer artista)
- [ ] Propiedad `artistNames: String` (todos los artistas separados por comas)
- [ ] Actualizar `equals()`, `hashCode()`, `toString()`

#### 2. **Player UI**
**Archivos:** `ui/screen/player/`

**Cambios:**
- [ ] Mostrar múltiples artistas en player
- [ ] Click en cada artista → ArtistDetail
- [ ] Separador visual entre artistas (comas o "feat.")

#### 3. **Library - Artists View**
**Archivos:** `ui/screen/library/artists/`

**Cambios:**
- [ ] Actualizar lista de artistas para usar nueva tabla
- [ ] Mostrar count de canciones por artista
- [ ] Búsqueda de artistas

#### 4. **Song Detail / Tag Editor**
**Archivos:** `ui/screen/tageditor/`

**Cambios:**
- [ ] UI para editar múltiples artistas
- [ ] Agregar/quitar artistas
- [ ] Reordenar artistas (drag & drop)

---

## ⏳ FASE 4: Testing (PENDIENTE)

### Tests a Crear

```kotlin
// Unit tests
class SongArtistDaoTest {
    @Test fun insertArtistForSong()
    @Test fun getArtistsForSong_returnsAllArtists()
    @Test fun deleteArtistFromSong()
    @Test fun updateArtistOrder()
}

class MultiArtistMigrationTest {
    @Test fun migrateExistingData_preservesPrimaryArtist()
    @Test fun migrateData_artistOrderIsZero()
}

// Integration tests
class MultiArtistIntegrationTest {
    @Test fun addArtistToSong_updatesRepository()
    @Test fun getSongsByArtist_returnsCorrectSongs()
}
```

---

## 📋 BACKLOG DETALLADO

### Base de Datos (✅ COMPLETADO)
- [x] Crear `SongArtistEntity`
- [x] Crear `SongArtistDao`
- [x] Actualizar `BoomingDatabase` (v7)
- [x] Crear migración `MIGRATION_6_7`
- [x] Migrar datos existentes

### Repository (⏳ PENDIENTE)
- [ ] Actualizar `SongRepository`
- [ ] Actualizar `ArtistRepository`
- [ ] Crear/actualizar mappers
- [ ] Actualizar `Song` model

### UI (⏳ PENDIENTE)
- [ ] Actualizar player UI
- [ ] Actualizar artist library
- [ ] Actualizar tag editor
- [ ] Actualizar search

### Testing (⏳ PENDIENTE)
- [ ] Unit tests para DAO
- [ ] Integration tests
- [ ] Migration tests
- [ ] UI tests

### Documentación (⏳ PENDIENTE)
- [ ] Actualizar README
- [ ] Actualizar ROADMAP
- [ ] Guía de migración para usuarios

---

## 🎯 CRITERIOS DE ACEPTACIÓN

### Funcionalidad Básica
- [ ] Una canción puede tener 2+ artistas
- [ ] UI muestra "Artista 1, Artista 2, Artista 3"
- [ ] Click en artista → ArtistDetail de ese artista
- [ ] Búsqueda por cualquier artista funciona

### Backward Compatibility
- [ ] Canciones existentes mantienen su artista original
- [ ] No se pierden datos en migración
- [ ] App funciona con DB v6 y v7

### Edge Cases
- [ ] Canción sin artistas (manejar null/empty)
- [ ] Artista con 0 canciones (se puede eliminar)
- [ ] Nombres duplicados (case-insensitive)
- [ ] Orden de artistas se preserva

---

## 📊 ESTIMACIÓN DE TRABAJO

| Fase | Horas Estimadas | Estado |
|------|-----------------|--------|
| Database | 2h | ✅ COMPLETADA |
| Repository | 8h | ⏳ PENDIENTE |
| UI | 12h | ⏳ PENDIENTE |
| Testing | 6h | ⏳ PENDIENTE |
| **TOTAL** | **28h** | **25% completo** |

**Timeline estimado:** 4-6 semanas (desarrollo part-time)

---

## 🔧 NOTAS TÉCNICAS

### Decisiones de Diseño

1. **Nombre como clave (no ID separado)**
   - Simplifica el schema
   - Evita tabla Artist adicional
   - Trade-off: Duplicación de nombres, pero es aceptable

2. **artist_order para mantener orden**
   - Importante para "Artista Principal feat. Artista Invitado"
   - Permite reordenar sin perder datos

3. **CASCADE DELETE**
   - Si se elimina canción, se eliminan relaciones
   - Limpieza automática

4. **Índices compuestos**
   - Búsqueda rápida por canción
   - Búsqueda rápida por artista

### Migración

```sql
-- Migración automática preserva artista principal
INSERT INTO song_artist (song_id, artist_name, artist_order)
SELECT song_key, artist_name, 0  -- Orden 0 = artista principal
FROM SongEntity
WHERE artist_name IS NOT NULL AND artist_name != ''
```

**Post-migración:**
- `SongEntity.artist_name` se mantiene por backward compatibility
- Nuevas canciones usan solo `song_artist`
- Futuro: Remover `SongEntity.artist_name` (v8+)

---

## 🚀 PRÓXIMOS PASOS

### Inmediato (Esta semana)
1. [ ] Actualizar `SongRepository` con métodos para multi-artist
2. [ ] Actualizar `Song` model para tener `List<String> artists`
3. [ ] Actualizar mappers

### Corto Plazo (2 semanas)
1. [ ] Actualizar UI de player para mostrar múltiples artistas
2. [ ] Actualizar Artist library
3. [ ] Tests unitarios

### Medio Plazo (4 semanas)
1. [ ] Tag editor para múltiples artistas
2. [ ] Tests de integración
3. [ ] Beta testing

---

*Documento de seguimiento - 2 de marzo de 2026*
