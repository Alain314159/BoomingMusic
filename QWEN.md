# QWEN.md - Configuración del Proyecto BoomingMusic

> **Creado:** 2 de marzo de 2026
> **Proyecto:** Booming Music v1.2.1
> **Stack:** Android (Kotlin) + Material 3

---

## 📋 Información del Proyecto

Ver `PROJECT_CONTEXT.md` para documentación completa del proyecto.

---

## 🤖 Subagentes Especializados

### Subagente: Security Review

```yaml
# .claude/agents/security-review.md
name: security-review
description: Auditoría de seguridad para aplicación Android
version: 1.0.0
triggers:
  - "revisar seguridad"
  - "buscar vulnerabilidades"
  - "auditoría de seguridad"
  - "security review"
  - "security audit"
  - "check vulnerabilities"
tools:
  - read_file
  - grep_search
  - run_shell_command
  - glob
system_prompt: |
  Eres un experto en seguridad de aplicaciones Android.
  
  Áreas de análisis:
  1. Hardcoded credentials (API keys, tokens, passwords)
  2. Insecure data storage (SharedPreferences, archivos sin encriptar)
  3. Missing encryption (datos sensibles en texto plano)
  4. Vulnerable dependencies (dependencias desactualizadas)
  5. Injection vulnerabilities (SQL, command injection)
  6. Network security (cleartext traffic, SSL pinning)
  7. Permission overuse (permisos innecesarios)
  8. Exported components (activities, services, receivers)
  
  Para cada hallazgo:
  - Describe la vulnerabilidad
  - Clasifica por severidad (Critical/High/Medium/Low)
  - Ubicación exacta (archivo:línea)
  - Proporciona remediation code
  - Referencia OWASP Mobile Top 10 si aplica
```

---

## 🛠️ Configuración de MCP Servers

```json
{
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
      "args": ["lintNormalDebug"],
      "cwd": "/workspaces/BoomingMusic"
    }
  }
}
```

---

## ⚙️ Preferencias del Proyecto

### Build Commands
```bash
# Build debug
./gradlew assembleNormalDebug
./gradlew assembleFdroidDebug

# Lint (crítico - warnings = error)
./gradlew lintNormalDebug
./gradlew lintFdroidDebug

# Tests
./gradlew testNormalDebugUnitTest
./gradlew testFdroidDebugUnitTest

# Compilación estricta
./gradlew compileNormalDebugKotlin compileFdroidDebugKotlin
```

### Paths Importantes
```
app/src/main/java/com/mardous/booming/
├── core/           # Núcleo
├── data/           # Capa de datos
├── playback/       # Motor de audio
├── ui/             # UI
├── util/           # Utilidades
└── extensions/     # Extensiones
```

### Archivos Clave
- `PROJECT_CONTEXT.md` - Documentación completa
- `gradle/libs.versions.toml` - Catálogo de dependencias
- `app/build.gradle.kts` - Configuración de build
- `.github/workflows/` - CI/CD pipelines

---

## 📝 Convenciones de Código

### Estilo Kotlin
- **Indentación:** 4 espacios
- **Líneas:** Máximo 120 caracteres
- **Naming:** CamelCase para clases, snake_case para archivos
- **Imports:** Sin wildcards, específicos siempre

### Patrones
- **MVVM + Repository Pattern**
- **Koin** para inyección de dependencias
- **Flow/LiveData** para reactividad
- **Room** para base de datos

### Reglas Importantes
- ✅ Ejecutar `./gradlew lint` antes de commit
- ✅ Probar ambos flavors (normal y fdroid)
- ✅ No usar `fallbackToDestructiveMigration()` en producción
- ✅ Incrementar versión de DB en Room si hay cambios de schema

---

## 🔍 Comandos de Diagnóstico

```bash
# Ver logs de la app
adb logcat -s BoomingMusic:*

# Ver logs del scanner
adb logcat | grep -i "scanner\|media"

# Listar APKs generados
find app/build/outputs/apk -name "*.apk"

# Verificar dependencias
./gradlew dependencies
./gradlew app:dependencies
```

---

## 🚨 Troubleshooting Rápido

### Errores de Compilación
1. `Unresolved reference` → Verificar imports faltantes
2. `Circular dependency en Koin` → Inyectar DAO directamente
3. `Ambiguous task` → Usar `compileNormalDebugKotlin` en lugar de `compileDebugKotlin`

### Errores de Lint
- ⚠️ **Los warnings cuentan como ERROR** en `strict-ci.yml`
- Ejecutar `./gradlew lintNormalDebug` para ver detalles
- Revisar reportes en `app/build/reports/lint/`

### Errores de Room
- Verificar migraciones en `BoomingDatabase.kt`
- Incrementar versión de DB si hay cambios de schema
- Actual: **v5** (ScannedMediaCache)

---

## 📊 Estado del Proyecto

| Componente | Estado | Versión |
|------------|--------|---------|
| **App** | ✅ Stable | 1.2.1 |
| **Scanner** | ✅ Independiente | v5 |
| **Database** | ✅ Room | v5 |
| **CI/CD** | ✅ GitHub Actions | - |
| **Tests** | ⚠️ Básico | - |

### Roadmap Pendiente
- [ ] Multi-artist support
- [ ] Last.fm integration (scrobbling)
- [ ] Jellyfin/Navidrome streaming
- [ ] Enhanced genre handling

---

## 🔐 Seguridad - Checklist

### Verificaciones Automáticas
- [ ] No hardcoded credentials en código
- [ ] API keys en BuildConfig o variables de entorno
- [ ] Network security config (no cleartext)
- [ ] SharedPreferences con MODE_PRIVATE
- [ ] Dependencias actualizadas
- [ ] Components no exportados innecesariamente

### Archivo: `app/src/main/res/xml/network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

---

## 📞 Contacto

- **GitHub:** @Alain314159
- **Telegram:** https://t.me/mardousdev
- **Crowdin:** https://crowdin.com/project/booming-music

---

**Última actualización:** 2 de marzo de 2026
