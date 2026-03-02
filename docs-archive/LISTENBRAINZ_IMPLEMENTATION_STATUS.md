# ListenBrainz Integration - Estado de Implementación

> **Fecha:** 28 de febrero de 2026  
> **Estado:** Fase 1 completada ✅  
> **Configuración requerida:** ¡NINGUNA! 🎉

---

## ✅ COMPLETADO (Fase 1 - Estructura)

### Archivos Creados

#### 1. Modelos de Datos (1 archivo)
- ✅ `ListenBrainzModels.kt`
  - `ListenBrainzCredentials` - Credenciales de usuario
  - `ListenBrainzScrobble` - Request para scrobble
  - `ListenBrainzScrobbleResponse` - Respuesta de API
  - `ListenBrainzUserResponse` - Validación de token

#### 2. API Client (1 archivo)
- ✅ `ListenBrainzApi.kt`
  - `validateToken()` - Validar token de usuario
  - `scrobble()` - Enviar scrobble individual
  - `submitListens()` - Enviar batch de scrobbles
  - `updateNowPlaying()` - Actualizar "escuchando ahora"

#### 3. Room - Entidades (2 archivos)
- ✅ `ListenBrainzCredentialsEntity.kt` - Entidad para credenciales
- ✅ `ListenBrainzScrobbleQueueEntity.kt` - Cola de scrobbles pendientes

#### 4. Room - DAOs (2 archivos)
- ✅ `ListenBrainzCredentialsDao.kt` - DAO para credenciales
- ✅ `ListenBrainzScrobbleQueueDao.kt` - DAO para cola de scrobbles

#### 5. Base de Datos (1 archivo modificado)
- ✅ `BoomingDatabase.kt`
  - Versión actualizada: 5 → 6
  - Entidades agregadas: 2 nuevas tablas
  - Migración `MIGRATION_5_6` creada
  - DAOs registrados

#### 6. Build Configuration (1 archivo modificado)
- ✅ `app/build.gradle.kts`
  - Eliminata configuración de Last.fm
  - Agregado comentario de ListenBrainz (no requiere API key)

#### 7. Documentación (2 archivos)
- ✅ `docs/LISTENBRAINZ_USER_GUIDE.md` - Guía para usuarios
- ✅ `docs/LISTENBRAINZ_IMPLEMENTATION_STATUS.md` - Este archivo

---

## ⏳ PENDIENTE (Fase 2 - Implementación)

### Scrobbling Observer
- [ ] `ListenBrainzScrobbleObserver.kt` - Observer del player
  - Detectar inicio/fin de reproducción
  - Calcular duración de playback
  - Encolar scrobble si cumple requisitos (>30s o >50%)

### Scrobble Service
- [ ] `ListenBrainzScrobbleService.kt` - Servicio de scrobbling
  - Submit de scrobbles a ListenBrainz API
  - Manejo de cola offline
  - Reintentos automáticos
  - Now playing updates

### WorkManager Integration
- [ ] `ListenBrainzSyncWorker.kt` - Worker para sync en background
  - Procesa cola de scrobbles pendientes
  - Reintentos con backoff exponencial
  - Notificaciones de estado

### UI
- [ ] `ListenBrainzSettingsActivity.kt` - Pantalla de configuración
- [ ] `ListenBrainzLoginScreen.kt` - UI para poner token (Compose)
- [ ] `ListenBrainzStatusCard.kt` - Card de estado (Compose)
- [ ] `ListenBrainzSettingsViewModel.kt` - ViewModel para settings

### Repository Integration
- [ ] `ListenBrainzRepository.kt` - Repository para ListenBrainz
  - Validar token
  - Guardar credenciales
  - Gestionar cola de scrobbles
  - Match de tracks con MusicBrainz IDs

### Koin DI Module
- [ ] `ListenBrainzModule.kt` - Módulo de inyección para ListenBrainz
  - Proveer ListenBrainzApi
  - Proveer Repository
  - Proveer ViewModels

### AndroidManifest
- [ ] Agregar permiso de internet (ya debería estar)
- [ ] Opcional: Deep link para abrir perfil de ListenBrainz

---

## 🎯 VENTAJAS DE LISTENBRAINZ VS LAST.FM

| Ventaja | Descripción |
|---------|-------------|
| **✅ Sin API Key** | No necesitas registrar la app ni configurar nada |
| **✅ Open Source** | Parte del ecosistema MusicBrainz |
| **✅ Más privado** | No vende datos de usuarios |
| **✅ Sin límites** | Scrobbles ilimitados, sin restricciones |
| **✅ Usuario controla** | Cada usuario usa SU token personal |
| **✅ Fácil para usuarios** | Solo copian y pegan su token |

---

## 📝 CÓMO FUNCIONA PARA EL USUARIO

### Flujo de Conexión

```
1. Usuario abre Booming Music
2. Va a Settings → ListenBrainz
3. Toca "Connect"
4. Abre enlace a listenbrainz.org/settings
5. Copia su User Token
6. Pega el token en la app
7. Toca "Validate & Connect"
8. ✅ Conectado!
```

### Flujo de Scrobbling

```
1. Usuario reproduce una canción
2. App detecta inicio de reproducción
3. App detecta fin de reproducción
4. Si duración > 30s (o > 50% del track):
   - Crea ListenBrainzScrobble
   - Si hay internet → envía inmediatamente
   - Si NO hay internet → guarda en cola
5. Cuando hay internet → envía cola pendiente
```

---

## 🔧 CONFIGURACIÓN REQUERIDA

### Para Desarrolladores (NOSOTROS):

**¡NINGUNA! 🎉**

- ❌ No necesitas API Key
- ❌ No necesitas GitHub Secrets
- ❌ No necesitas configurar nada
- ✅ Solo implementar el código

### Para Usuarios:

1. Cuenta en listenbrainz.org (gratis)
2. Copiar token de settings
3. Pegar en la app

---

## 📊 ARQUITECTURA

```
ui/screen/settings/
└── ListenBrainzSettingsActivity.kt
    └── ListenBrainzSettingsViewModel
        └── ListenBrainzRepository
            ├── ListenBrainzApi (Ktor)
            ├── ListenBrainzCredentialsDao (Room)
            └── ListenBrainzScrobbleQueueDao (Room)

playback/
└── ListenBrainzScrobbleObserver
    └── Escucha eventos del player
        └── Encola scrobbles

workers/
└── ListenBrainzSyncWorker
    └── Procesa cola pendiente
```

---

## 🧪 TESTING

### Tests a Crear

```kotlin
// Unit tests
class ListenBrainzApiTest
class ListenBrainzRepositoryTest
class ListenBrainzScrobbleObserverTest
class ListenBrainzCredentialsDaoTest

// Integration tests
class ListenBrainzAuthIntegrationTest
class ListenBrainzScrobbleIntegrationTest
```

---

## 📚 RECURSOS

- **API Docs:** https://listenbrainz.readthedocs.io/
- **API Endpoint:** https://api.listenbrainz.org/1
- **User Token:** https://listenbrainz.org/settings
- **GitHub:** https://github.com/metabrainz/listenbrainz-server

---

## 🚀 PRÓXIMOS PASOS

### Inmediato:
1. Implementar `ListenBrainzScrobbleObserver.kt`
2. Implementar `ListenBrainzScrobbleService.kt`
3. Crear UI básica de settings
4. Agregar a Koin DI module

### Después:
1. WorkManager para sync en background
2. Notificaciones de estado
3. Estadísticas en la app
4. Integración con MusicBrainz

---

*Documento de seguimiento de implementación*
