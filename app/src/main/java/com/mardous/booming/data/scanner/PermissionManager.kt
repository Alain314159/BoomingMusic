/*
 * Copyright (c) 2024 Christians Martínez Alvarado
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mardous.booming.data.scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Nivel de acceso a almacenamiento disponible.
 */
enum class StorageAccessLevel {
    /** Sin permisos */
    NONE,

    /** READ_EXTERNAL_STORAGE o READ_MEDIA_AUDIO (Android 9-) */
    LEGACY,

    /** Storage Access Framework (carpetas seleccionadas por usuario, Android 10+) */
    SAF,

    /** Acceso completo (Android 11+, NO RECOMENDADO para music players) */
    MANAGE_ALL
}

/**
 * Gestiona permisos de almacenamiento para el scanner.
 * 
 * ESTRATEGIA RECOMENDADA:
 * - Android 9 y anteriores: READ_EXTERNAL_STORAGE
 * - Android 10+: SAF (Storage Access Framework) para carpetas externas
 * - NO usar MANAGE_EXTERNAL_STORAGE (Google Play lo rechaza para music players)
 */
class PermissionManager : KoinComponent {

    private val context: Context by inject()

    companion object {
        private const val TAG = "PermissionManager"
    }

    /**
     * Retorna el nivel de acceso disponible.
     */
    fun getAccessLevel(): StorageAccessLevel {
        return when {
            // Android 10 y anterior - acceso directo con permisos legacy
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                if (hasLegacyPermission()) StorageAccessLevel.LEGACY else StorageAccessLevel.NONE
            }

            // Android 11+ - varias opciones
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                when {
                    // Verificar SAF primero (preferido)
                    hasSAFPermission() -> StorageAccessLevel.SAF
                    // Luego legacy
                    hasLegacyPermission() -> StorageAccessLevel.LEGACY
                    // MANAGE_ALL solo como último recurso (no recomendado)
                    hasManageAllPermission() -> StorageAccessLevel.MANAGE_ALL
                    else -> StorageAccessLevel.NONE
                }
            }

            else -> StorageAccessLevel.NONE
        }
    }

    /**
     * Verifica si tiene permisos legacy (READ_EXTERNAL_STORAGE o READ_MEDIA_AUDIO)
     */
    fun hasLegacyPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ usa READ_MEDIA_AUDIO
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 y anteriores usan READ_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Verifica si hay permisos SAF activos.
     * Verifica si hay carpetas SAF guardadas con permisos persistentes.
     */
    fun hasSAFPermission(): Boolean {
        // Verificar si hay carpetas SAF guardadas
        val prefs = context.getSharedPreferences("scanner_folders", Context.MODE_PRIVATE)
        val safFolders = prefs.getStringSet("user_folders", emptySet())
        
        if (safFolders.isNullOrEmpty()) {
            return false
        }

        // Verificar que al menos una carpeta tenga permisos válidos
        safFolders.forEach { folderData ->
            try {
                val uriString = folderData.split("||").firstOrNull() ?: folderData
                val uri = Uri.parse(uriString)
                val documentFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, uri)
                if (documentFile != null && documentFile.canRead()) {
                    return true
                }
            } catch (e: Exception) {
                // URI inválido o permisos revocados
            }
        }

        return false
    }

    /**
     * Verifica si tiene permiso MANAGE_EXTERNAL_STORAGE.
     * NOTA: No usar esto como estrategia principal.
     */
    fun hasManageAllPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            false
        }
    }

    /**
     * Obtiene los permisos legacy que se deben solicitar.
     */
    fun getLegacyPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Crea el intent para solicitar acceso completo (NO RECOMENDADO).
     * Solo usar como fallback extremo.
     */
    fun createManageAllAccessIntent(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        } else {
            null
        }
    }

    /**
     * Crea el intent para seleccionar carpeta con SAF.
     * El resultado debe manejarse en Activity/Fragment con registerForActivityResult.
     */
    fun createSAFPickerIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            // Sugerir carpeta de música como punto de partida
            val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            putExtra("android.provider.extra.INITIAL_URI", musicDir.toURI().toString())
        }
    }

    /**
     * Verifica si se necesitan permisos adicionales.
     */
    fun needsAdditionalPermissions(): Boolean {
        val accessLevel = getAccessLevel()
        return when (accessLevel) {
            StorageAccessLevel.NONE -> true
            StorageAccessLevel.LEGACY -> {
                // Con legacy solo puede acceder a carpetas predeterminadas
                // Si quiere carpetas custom, necesita SAF
                true
            }
            StorageAccessLevel.SAF -> false
            StorageAccessLevel.MANAGE_ALL -> false
        }
    }

    /**
     * Obtiene mensaje explicativo para el usuario.
     */
    fun getPermissionExplanation(): String {
        return when (getAccessLevel()) {
            StorageAccessLevel.NONE -> "Se necesitan permisos para acceder a tu música"
            StorageAccessLevel.LEGACY -> "Permisos básicos obtenidos. Para carpetas personalizadas, selecciona una carpeta."
            StorageAccessLevel.SAF -> "Acceso completo configurado correctamente"
            StorageAccessLevel.MANAGE_ALL -> "Acceso completo concedido (no recomendado)"
        }
    }

    /**
     * Verifica si puede acceder a una URI específica.
     */
    fun canAccessUri(uri: Uri): Boolean {
        return try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            true
        } catch (e: SecurityException) {
            false
        }
    }
}
