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

package io.github.tommy_geenexus.usbdonglecontrol

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.FiioKa5UsbCommunicationRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.VolumeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class UsbService : Service() {

    private companion object {

        const val REQUEST_CODE = 0
        const val ID_NOTIFICATION = 0
        const val ID_NOTIFICATION_CHANNEL = TOP_LEVEL_PACKAGE_NAME + "NOTIFICATION_CHANNEL"
        const val INTENT_ACTION_VOLUME_UP = TOP_LEVEL_PACKAGE_NAME + "VOLUME_UP"
        const val INTENT_ACTION_VOLUME_DOWN = TOP_LEVEL_PACKAGE_NAME + "VOLUME_DOWN"
        const val INTENT_ACTION_DISPLAY_INVERT = TOP_LEVEL_PACKAGE_NAME + "DISPLAY_INVERT"
        const val INTENT_EXTRA_USB_DONGLE = TOP_LEVEL_PACKAGE_NAME + "USB_DONGLE"
    }

    @Inject
    lateinit var usbRepository: UsbRepository

    @Inject
    lateinit var fiioKa5UsbCommunicationRepository: FiioKa5UsbCommunicationRepository

    private lateinit var coroutineScope: CoroutineScope

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            val device = usbRepository.getAttachedDeviceOrNull()
            if (device != null) {
                val connection = usbRepository.openDeviceOrNull(device)
                if (connection != null) {
                    val usbDongle = fiioKa5UsbCommunicationRepository.getCurrentState(connection)
                    if (usbDongle is FiioKa5) {
                        showOrUpdateNotification(
                            usbDongle = usbDongle,
                            volumePercent = "${
                                (usbDongle.volumeLevel * 100 / usbDongle.volumeMode.steps).toDouble()
                                    .roundToInt()
                            }%"
                        )
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            INTENT_ACTION_VOLUME_UP -> {
                coroutineScope.launch {
                    val device = usbRepository.getAttachedDeviceOrNull()
                    if (device != null) {
                        val connection = usbRepository.openDeviceOrNull(device)
                        val usbDongle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                INTENT_EXTRA_USB_DONGLE,
                                UsbDongle::class.java
                            )
                        } else {
                            intent.getParcelableExtra(INTENT_EXTRA_USB_DONGLE)
                        }
                        if (connection != null && usbDongle is FiioKa5) {
                            val volumeLevel = usbDongle.volumeLevel + 1
                            val success = fiioKa5UsbCommunicationRepository.setVolumeLevel(
                                connection = connection,
                                volumeMode = usbDongle.volumeMode,
                                volumeLevel = volumeLevel.clamp(
                                    min = FiioKa5Defaults.VOLUME_LEVEL_MIN,
                                    max = if (usbDongle.volumeMode == VolumeMode.S120) {
                                        FiioKa5Defaults.VOLUME_LEVEL_A_MAX
                                    } else {
                                        FiioKa5Defaults.VOLUME_LEVEL_B_MAX
                                    }
                                )
                            )
                            if (success) {
                                showOrUpdateNotification(
                                    usbDongle = usbDongle.copy(volumeLevel = volumeLevel),
                                    volumePercent =
                                    "${(volumeLevel * 100 / usbDongle.volumeMode.steps)
                                        .toDouble()
                                        .roundToInt()}%"
                                )
                            }
                        }
                    }
                }
            }
            INTENT_ACTION_VOLUME_DOWN -> {
                coroutineScope.launch {
                    val device = usbRepository.getAttachedDeviceOrNull()
                    if (device != null) {
                        val connection = usbRepository.openDeviceOrNull(device)
                        val usbDongle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                INTENT_EXTRA_USB_DONGLE,
                                UsbDongle::class.java
                            )
                        } else {
                            intent.getParcelableExtra(INTENT_EXTRA_USB_DONGLE)
                        }
                        if (connection != null && usbDongle is FiioKa5) {
                            val volumeLevel = usbDongle.volumeLevel - 1
                            val success = fiioKa5UsbCommunicationRepository.setVolumeLevel(
                                connection = connection,
                                volumeMode = usbDongle.volumeMode,
                                volumeLevel = volumeLevel.clamp(
                                    min = FiioKa5Defaults.VOLUME_LEVEL_MIN,
                                    max = if (usbDongle.volumeMode == VolumeMode.S120) {
                                        FiioKa5Defaults.VOLUME_LEVEL_A_MAX
                                    } else {
                                        FiioKa5Defaults.VOLUME_LEVEL_B_MAX
                                    }
                                )
                            )
                            if (success) {
                                showOrUpdateNotification(
                                    usbDongle = usbDongle.copy(volumeLevel = volumeLevel),
                                    volumePercent =
                                    "${(volumeLevel * 100 / usbDongle.volumeMode.steps)
                                        .toDouble()
                                        .roundToInt()}%"
                                )
                            }
                        }
                    }
                }
            }
            INTENT_ACTION_DISPLAY_INVERT -> {
                coroutineScope.launch {
                    val device = usbRepository.getAttachedDeviceOrNull()
                    if (device != null) {
                        val connection = usbRepository.openDeviceOrNull(device)
                        val usbDongle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                INTENT_EXTRA_USB_DONGLE,
                                UsbDongle::class.java
                            )
                        } else {
                            intent.getParcelableExtra(INTENT_EXTRA_USB_DONGLE)
                        }
                        if (connection != null && usbDongle is FiioKa5) {
                            val displayInvertEnabled = !usbDongle.displayInvertEnabled
                            val success = fiioKa5UsbCommunicationRepository.setDisplayInvertEnabled(
                                connection = connection,
                                displayInvertEnabled = displayInvertEnabled
                            )
                            if (success) {
                                showOrUpdateNotification(
                                    usbDongle = usbDongle.copy(
                                        displayInvertEnabled = displayInvertEnabled
                                    ),
                                    volumePercent =
                                    "${(usbDongle.volumeLevel * 100 / usbDongle.volumeMode.steps)
                                        .toDouble()
                                        .roundToInt()}%"
                                )
                            }
                        }
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        kotlin.runCatching { nm.cancelAll() }
    }

    private suspend fun showOrUpdateNotification(
        usbDongle: UsbDongle,
        volumePercent: String
    ) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        coroutineScope.coroutineContext.suspendRunCatching {
            nm.createNotificationChannel(
                NotificationChannel(
                    ID_NOTIFICATION_CHANNEL,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setShowBadge(false)
                }
            )
            if (nm.areNotificationsEnabled()) {
                nm.notify(ID_NOTIFICATION, createOrUpdateNotification(usbDongle, volumePercent))
            }
        }.getOrElse { exception ->
            Timber.e(exception)
        }
    }

    private fun createOrUpdateNotification(
        usbDongle: UsbDongle,
        volumePercent: String
    ): Notification {
        return Notification
            .Builder(applicationContext, ID_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.volume_level, volumePercent))
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    REQUEST_CODE,
                    packageManager.getLaunchIntentForPackage(packageName),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                Notification.Action.Builder(
                    null,
                    getString(R.string.volume_up),
                    PendingIntent.getService(
                        applicationContext,
                        REQUEST_CODE,
                        Intent(applicationContext, UsbService::class.java).apply {
                            action = INTENT_ACTION_VOLUME_UP
                            putExtra(INTENT_EXTRA_USB_DONGLE, usbDongle)
                        },
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    null,
                    getString(R.string.volume_down),
                    PendingIntent.getService(
                        applicationContext,
                        REQUEST_CODE,
                        Intent(applicationContext, UsbService::class.java).apply {
                            action = INTENT_ACTION_VOLUME_DOWN
                            putExtra(INTENT_EXTRA_USB_DONGLE, usbDongle)
                        },
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    null,
                    getString(R.string.display_invert),
                    PendingIntent.getService(
                        applicationContext,
                        REQUEST_CODE,
                        Intent(applicationContext, UsbService::class.java).apply {
                            action = INTENT_ACTION_DISPLAY_INVERT
                            putExtra(INTENT_EXTRA_USB_DONGLE, usbDongle)
                        },
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                ).build()
            )
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .build()
    }
}
