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

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Carpeta a escanear.
 */
data class ScanFolder(
    val path: String,
    val uri: String?, // null para carpetas del sistema, URI string para SAF
    val isDefault: Boolean,
    val isEnabled: Boolean = true,
    val displayName: String = path.substringAfterLast('/')
)

/**
 * Gestiona las carpetas seleccionadas para escaneo.
 * Soporta carpetas predeterminadas y carpetas SAF seleccionadas por el usuario.
 */
class FolderSelectionManager : KoinComponent {

    private val context: Context by inject()

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("scanner_folders", Context.MODE_PRIVATE)
    }

    companion object {
        private val DEFAULT_MUSIC_FOLDERS = listOf(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        ).filter { it.exists() }

        private const val KEY_USER_FOLDERS = "user_folders"
        private const val KEY_ENABLED_PREFIX = "enabled_"
    }

    /**
     * Obtiene todas las carpetas a escanear.
     * Incluye las predeterminadas más las seleccionadas por el usuario.
     */
    fun getScanFolders(): List<ScanFolder> {
        val folders = mutableListOf<ScanFolder>()

        // Agregar carpetas predeterminadas
        DEFAULT_MUSIC_FOLDERS.forEach { folder ->
            folders.add(ScanFolder(
                path = folder.absolutePath,
                uri = null, // Es una carpeta del sistema
                isDefault = true,
                isEnabled = isFolderEnabled(folder.absolutePath)
            ))
        }

        // Agregar carpetas seleccionadas por el usuario (SAF)
        getUserSelectedFolders().forEach { folderData ->
            try {
                val (uriString, displayName) = folderData.split("||")
                val uri = Uri.parse(uriString)
                val documentFile = DocumentFile.fromTreeUri(context, uri)
                if (documentFile != null && documentFile.exists()) {
                    folders.add(ScanFolder(
                        path = documentFile.name ?: "Unknown",
                        uri = uriString,
                        isDefault = false,
                        isEnabled = isFolderEnabled(uriString),
                        displayName = displayName
                    ))
                }
            } catch (e: Exception) {
                // Ignorar URIs inválidos o corruptos
            }
        }

        return folders
    }

    /**
     * Agrega una carpeta usando SAF (Storage Access Framework)
     */
    fun addUserSelectedFolder(uri: Uri, displayName: String? = null) {
        val currentFolders = getUserSelectedFolders().toMutableList()
        val uriString = uri.toString()
        
        // Guardar con display name para referencia futura
        val folderData = displayName?.let { "$uriString||$it" } ?: uriString
        
        if (!currentFolders.any { it.startsWith(uriString) }) {
            currentFolders.add(folderData)
            prefs.edit()
                .putStringSet(KEY_USER_FOLDERS, currentFolders.toSet())
                .apply()
        }
    }

    /**
     * Remueve una carpeta seleccionada por el usuario
     */
    fun removeUserSelectedFolder(uri: Uri) {
        val currentFolders = getUserSelectedFolders().toMutableList()
        val uriString = uri.toString()
        currentFolders.removeAll { it.startsWith(uriString) }
        prefs.edit()
            .putStringSet(KEY_USER_FOLDERS, currentFolders.toSet())
            .apply()
        
        // También remover preferencia de enabled
        prefs.edit().remove("${KEY_ENABLED_PREFIX}_$uriString").apply()
    }

    /**
     * Habilita/deshabilita una carpeta específica
     */
    fun setFolderEnabled(pathOrUri: String, enabled: Boolean) {
        prefs.edit()
            .putBoolean("${KEY_ENABLED_PREFIX}_$pathOrUri", enabled)
            .apply()
    }

    /**
     * Verifica si una carpeta está habilitada
     */
    fun isFolderEnabled(pathOrUri: String): Boolean {
        return prefs.getBoolean("${KEY_ENABLED_PREFIX}_$pathOrUri", true)
    }

    /**
     * Obtiene todas las carpetas seleccionadas por el usuario
     */
    fun getUserSelectedFolders(): List<String> {
        return prefs.getStringSet(KEY_USER_FOLDERS, emptySet())?.toList() ?: emptyList()
    }

    /**
     * Verifica si hay carpetas SAF seleccionadas
     */
    fun hasSAFFolders(): Boolean {
        return getUserSelectedFolders().isNotEmpty()
    }

    /**
     * Obtiene solo carpetas habilitadas
     */
    fun getEnabledFolders(): List<ScanFolder> {
        return getScanFolders().filter { it.isEnabled }
    }

    /**
     * Verifica si tiene acceso a todas las carpetas predeterminadas
     */
    fun hasAccessToDefaultFolders(): Boolean {
        return DEFAULT_MUSIC_FOLDERS.all { it.canRead() }
    }

    /**
     * Actualiza el display name de una carpeta SAF
     */
    fun updateFolderDisplayName(uri: Uri, newDisplayName: String) {
        val currentFolders = getUserSelectedFolders().toMutableList()
        val uriString = uri.toString()
        
        val index = currentFolders.indexOfFirst { it.startsWith(uriString) }
        if (index != -1) {
            currentFolders[index] = "$uriString||$newDisplayName"
            prefs.edit()
                .putStringSet(KEY_USER_FOLDERS, currentFolders.toSet())
                .apply()
        }
    }

    /**
     * Limpia todas las carpetas de usuario
     */
    fun clearUserFolders() {
        prefs.edit()
            .remove(KEY_USER_FOLDERS)
            .apply()
        
        // Limpiar también las preferencias de enabled
        prefs.all.keys
            .filter { it.startsWith(KEY_ENABLED_PREFIX) }
            .forEach { key ->
                prefs.edit().remove(key).apply()
            }
    }
}
