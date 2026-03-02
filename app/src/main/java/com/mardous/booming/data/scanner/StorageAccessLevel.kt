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
