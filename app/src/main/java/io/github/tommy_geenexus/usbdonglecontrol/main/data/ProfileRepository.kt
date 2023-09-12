/*
 * Copyright (c) 2022-2023, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommy_geenexus.usbdonglecontrol.main.data

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_ACTION_SHORTCUT_PROFILE
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.di.DispatcherIo
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5ProfileDao
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.data.db.MoondropDawnProfile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.data.db.MoondropDawnProfileDao
import io.github.tommy_geenexus.usbdonglecontrol.suspendRunCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val fiioKa5ProfileDao: FiioKa5ProfileDao,
    private val moondropDawnProfileDao: MoondropDawnProfileDao
) {

    private companion object {

        const val PROFILES_MAX = 100
    }

    suspend fun getProfiles(usbDongle: UsbDongle?): List<Profile> {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                when (usbDongle) {
                    is FiioKa5 -> {
                        fiioKa5ProfileDao.getProfiles()
                    }
                    is MoondropDawn44 -> {
                        moondropDawnProfileDao.getProfiles()
                    }
                    else -> {
                        emptyList()
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                emptyList()
            }
        }
    }

    suspend fun upsertProfile(profile: Profile): Result<List<Profile>> {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                if (profile is FiioKa5Profile) {
                    if (fiioKa5ProfileDao.getProfileCount() < PROFILES_MAX) {
                        Result.success(fiioKa5ProfileDao.upsertAndGetProfiles(profile))
                    } else {
                        error("Profile limit reached")
                    }
                } else if (profile is MoondropDawnProfile) {
                    if (moondropDawnProfileDao.getProfileCount() < PROFILES_MAX) {
                        Result.success(moondropDawnProfileDao.upsertAndGetProfiles(profile))
                    } else {
                        error("Profile limit reached")
                    }
                } else {
                    error("Invalid profile")
                }
            }
        }.getOrElse { exception ->
            Timber.e(exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteProfile(profile: Profile): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                when (profile) {
                    is FiioKa5Profile -> {
                        fiioKa5ProfileDao.delete(profile)
                        true
                    }
                    is MoondropDawnProfile -> {
                        moondropDawnProfileDao.delete(profile)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun addProfileShortcut(profile: Profile): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val intent = context
                    .packageManager
                    .getLaunchIntentForPackage(context.packageName)
                    ?.apply {
                        putExtra(INTENT_ACTION_SHORTCUT_PROFILE, profile.toPersistableBundle())
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    ?: return@suspendRunCatching false
                val icon = IconCompat.createWithResource(context, R.drawable.ic_shortcut_profile)
                val shortcut = ShortcutInfoCompat.Builder(context, profile.id.toString())
                    .setShortLabel(profile.name)
                    .setLongLabel(profile.name)
                    .setIcon(icon)
                    .setIntent(intent)
                    .build()
                ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun removeProfileShortcut(profile: Profile): Boolean {
        return withContext(dispatcherIo) {
            val shortcut = listOf(profile.id.toString())
            coroutineContext.suspendRunCatching {
                ShortcutManagerCompat.removeDynamicShortcuts(context, shortcut)
                ShortcutManagerCompat.disableShortcuts(context, shortcut, null)
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }
}
