/*
 * Copyright (c) 2022-2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.control.data

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommygeenexus.usbdonglecontrol.INTENT_ACTION_SHORTCUT_PROFILE
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.suspendRunCatching
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class ProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val profileDao: ProfileDao
) {

    fun getProfilesForUsbDongleWith(vendorId: Int, productId: Int) =
        profileDao.getProfiles(vendorId, productId)

    suspend fun exportProfile(profile: Profile): Result<Unit> {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                profileDao.upsert(profile)
                Result.success(Unit)
            }
        }.getOrElse { exception ->
            Timber.e(exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteProfile(profile: Profile): Result<Unit> {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                profileDao.delete(profile)
                Result.success(Unit)
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun addProfileShortcut(profile: Profile): Result<Unit> {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val intent = context
                    .packageManager
                    .getLaunchIntentForPackage(context.packageName)
                    ?.apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(INTENT_ACTION_SHORTCUT_PROFILE, profile.toPersistableBundle())
                    }
                    ?: error("getLaunchIntentForPackage()")
                val icon = IconCompat.createWithResource(context, R.drawable.ic_shortcut_profile)
                val shortcut = ShortcutInfoCompat.Builder(context, profile.id.toString())
                    .setShortLabel(profile.name)
                    .setLongLabel(profile.name)
                    .setIcon(icon)
                    .setIntent(intent)
                    .build()
                if (!ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)) {
                    error("pushDynamicShortcut()")
                }
                Result.success(Unit)
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun removeProfileShortcut(profile: Profile): Result<Unit> {
        return withContext(dispatcherIo) {
            val shortcut = listOf(profile.id.toString())
            coroutineContext.suspendRunCatching {
                ShortcutManagerCompat.removeDynamicShortcuts(context, shortcut)
                ShortcutManagerCompat.disableShortcuts(context, shortcut, null)
                Result.success(Unit)
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }
}
