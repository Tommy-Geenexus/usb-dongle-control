/*
 * Copyright (c) 2024-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY,WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.tommygeenexus.usbdonglecontrol.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.tommygeenexus.usbdonglecontrol.core.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.core.extension.suspendRunCatching
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:DispatcherIo private val dispatcherIo: CoroutineDispatcher
) {

    private val keyMaximizeVolume = booleanPreferencesKey("maximize_volume")

    suspend fun isMaximizeVolumeEnabled(): Boolean = dataStore
        .data
        .catch { exception -> Timber.e(exception) }
        .map { preferences -> preferences[keyMaximizeVolume] }
        .firstOrNull() == true

    suspend fun putMaximizeVolume(value: Boolean): Result<Unit> = withContext(dispatcherIo) {
        coroutineContext.suspendRunCatching {
            dataStore.edit { preferences -> preferences[keyMaximizeVolume] = value }
            Result.success(Unit)
        }.getOrElse { exception ->
            Timber.e(exception)
            Result.failure(exception)
        }
    }
}
