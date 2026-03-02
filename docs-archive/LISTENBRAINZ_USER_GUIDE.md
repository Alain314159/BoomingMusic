# ListenBrainz Integration - Guía para Usuarios

> **Fecha:** 28 de febrero de 2026  
> **Estado:** Implementado  
> **Configuración requerida:** ¡NINGUNA! 🎉

---

## 🎯 ¿QUÉ ES LISTENBRAINZ?

**ListenBrainz** es un servicio **open source** para registrar tu historial de música (scrobbling).

- ✅ **Gratis** para siempre
- ✅ **Open source** (como Booming Music)
- ✅ **Sin anuncios**
- ✅ **Respeta tu privacidad**
- ✅ **Parte de MusicBrainz** (metabrainz.org)

---

## 🚀 CÓMO USAR (MUY FÁCIL)

### Paso 1: Crear cuenta en ListenBrainz (2 minutos)

1. Ve a https://listenbrainz.org
2. Toca "Sign In" (arriba a la derecha)
3. Inicia sesión con:
   - MusicBrainz (recomendado)
   - Google
   - Facebook
   - O crea cuenta nueva

### Paso 2: Obtener tu User Token

1. Una vez logueado, ve a https://listenbrainz.org/settings
2. Baja hasta la sección **"User Token"**
3. Toca el botón **"Copy"** para copiar tu token

Tu token se ve así: `a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8`

### Paso 3: Poner el token en Booming Music

1. Abre Booming Music
2. Ve a **Settings** → **ListenBrainz**
3. Toca **"Connect"**
4. Pega tu token
5. ¡Listo! ✅

---

## ✨ ¿QUÉ PASA DESPUÉS?

### Automáticamente:

- 🎵 **Cada canción que escuches** se registra en ListenBrainz
- 📊 **Tu perfil** muestra tu historial musical
- 🎨 **Estadísticas** de tus artistas y géneros favoritos
- 📱 **Compatible** con otras apps (Spotify, VLC, etc.)

### Cuando hay internet:

- Los scrobbles se envían **inmediatamente**

### Cuando NO hay internet:

- Los scrobbles se **guardan en la app**
- Se envían **automáticamente** cuando recuperes conexión

---

## 🔐 PRIVACIDAD Y SEGURIDAD

### ¿Qué guardamos?

- ✅ Tu **user token** (encriptado en el dispositivo)
- ✅ Tu **username** (opcional)

### ¿Qué NO guardamos?

- ❌ Tu password de ListenBrainz
- ❌ Tu password de Google/Facebook
- ❌ Datos personales adicionales

### ¿Dónde se guarda?

- En **EncryptedSharedPreferences** (seguro, encriptado)
- **Nunca** sale de tu dispositivo
- **Nunca** se comparte con terceros

---

## 🔄 DESACTIVAR / ELIMINAR

### Desconectar ListenBrainz:

1. Settings → ListenBrainz
2. Toca **"Disconnect"**
3. Tu token se elimina del dispositivo

### Eliminar cuenta ListenBrainz:

- Ve a https://listenbrainz.org/settings
- Baja hasta **"Delete Account"**
- Sigue las instrucciones

---

## 📊 VER TUS DATOS

### En la web:

- Tu perfil: https://listenbrainz.org/user/TU_USUARIO
- Estadísticas: https://listenbrainz.org/stats/TU_USUARIO

### En la app:

- Settings → ListenBrainz → "View Profile"

---

## ❓ PREGUNTAS FRECUENTES

### ¿Es gratis?

**Sí**, 100% gratis, sin límites.

### ¿Necesito API Key?

**No**, solo tu token personal de usuario.

### ¿Puedo usarlo sin cuenta?

**No**, necesitas cuenta para que se registren tus scrobbles.

### ¿Qué pasa si cambio de dispositivo?

- Instala Booming Music en el nuevo dispositivo
- Pon el **mismo token** de ListenBrainz
- Tu historial se sincroniza automáticamente

### ¿Puedo exportar mis datos?

**Sí**, ListenBrainz permite exportar todo tu historial en JSON.

---

## 🔗 ENLACES ÚTILES

| Recurso | Enlace |
|---------|--------|
| **ListenBrainz Web** | https://listenbrainz.org |
| **Documentación API** | https://listenbrainz.readthedocs.io |
| **Tu Perfil** | https://listenbrainz.org/user/TU_USUARIO |
| **Estadísticas** | https://listenbrainz.org/stats/TU_USUARIO |
| **MusicBrainz** | https://musicbrainz.org |

---

## 💡 COMPARACIÓN CON LAST.FM

| Característica | Last.fm | ListenBrainz |
|----------------|---------|--------------|
| **Gratis** | ✅ Sí | ✅ Sí |
| **Open Source** | ❌ No | ✅ Sí |
| **API Key** | ❌ Requiere | ✅ No requiere |
| **Privacidad** | ⚠️ Regular | ✅ Mejor |
| **Estadísticas** | ✅ Sí | ✅ Sí |
| **Social** | ✅ Mejor | ⚠️ Básico |

---

*Documento creado para Booming Music - 28 de febrero de 2026*
