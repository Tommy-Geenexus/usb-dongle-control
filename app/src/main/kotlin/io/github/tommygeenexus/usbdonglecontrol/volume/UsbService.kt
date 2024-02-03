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

package io.github.tommygeenexus.usbdonglecontrol.volume

import android.app.Service
import android.content.Intent
import androidx.core.content.IntentCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.tommygeenexus.usbdonglecontrol.control.domain.GetCurrentStateUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.GetVolumeLevelUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetVolumeLevelUseCase
import io.github.tommygeenexus.usbdonglecontrol.di.DispatcherMainImmediate
import io.github.tommygeenexus.usbdonglecontrol.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.dongle.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.suspendRunCatching
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class UsbService : Service() {

    @Inject
    lateinit var usbRepository: UsbRepository

    @Inject
    lateinit var getCurrentStateUseCase: GetCurrentStateUseCase

    @Inject
    lateinit var getVolumeLevelUseCase: GetVolumeLevelUseCase

    @Inject
    lateinit var setVolumeLevelUseCase: SetVolumeLevelUseCase

    @DispatcherMainImmediate
    @Inject
    lateinit var dispatcherMainImmediate: CoroutineDispatcher

    private lateinit var coroutineScope: CoroutineScope

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        coroutineScope = CoroutineScope(dispatcherMainImmediate)
        showCurrentState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            UsbServiceNotification.INTENT_ACTION_VOLUME_UP -> {
                val usbDongle = IntentCompat.getParcelableExtra(
                    intent,
                    UsbServiceNotification.INTENT_EXTRA_USB_DONGLE,
                    UsbDongle::class.java
                )
                val volumeStepSize = intent.getIntExtra(
                    UsbServiceNotification.INTENT_EXTRA_VOLUME_STEP_SIZE,
                    HardwareVolumeControl.VOLUME_STEP_SIZE_DEFAULT
                )
                (usbDongle as? HardwareVolumeControl)
                    ?.volumeUp(volumeStepSize)
                    ?.also { setVolumeLevel(usbDongle, it, volumeStepSize) }
            }
            UsbServiceNotification.INTENT_ACTION_VOLUME_DOWN -> {
                val usbDongle = IntentCompat.getParcelableExtra(
                    intent,
                    UsbServiceNotification.INTENT_EXTRA_USB_DONGLE,
                    UsbDongle::class.java
                )
                val volumeStepSize = intent.getIntExtra(
                    UsbServiceNotification.INTENT_EXTRA_VOLUME_STEP_SIZE,
                    HardwareVolumeControl.VOLUME_STEP_SIZE_DEFAULT
                )
                (usbDongle as? HardwareVolumeControl)
                    ?.volumeDown(volumeStepSize)
                    ?.also { setVolumeLevel(usbDongle, it, volumeStepSize) }
            }
            UsbServiceNotification.INTENT_ACTION_VOLUME_STEP_SIZE -> {
                val usbDongle = IntentCompat.getParcelableExtra(
                    intent,
                    UsbServiceNotification.INTENT_EXTRA_USB_DONGLE,
                    UsbDongle::class.java
                )
                val volumeStepSize = intent.getIntExtra(
                    UsbServiceNotification.INTENT_EXTRA_VOLUME_STEP_SIZE,
                    HardwareVolumeControl.VOLUME_STEP_SIZE_DEFAULT
                )
                (usbDongle as? HardwareVolumeControl)
                    ?.incrementOrWrapVolumeStepSize(volumeStepSize)
                    ?.also {
                        UsbServiceNotification.buildAndShow(
                            context = applicationContext,
                            usbDongle = usbDongle,
                            volumePercent = usbDongle.displayVolumeLevel,
                            volumeStepSize = it
                        )
                    }
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun showCurrentState() {
        coroutineScope.launch {
            coroutineContext.suspendRunCatching {
                val (_, usbDongle, _) = usbRepository.getFirstAttachedUsbDongle().getOrThrow()
                val currentUsbDongle = getVolumeLevelUseCase(usbDongle).getOrThrow()
                if (currentUsbDongle !is HardwareVolumeControl ||
                    UsbServiceNotification.createNotificationChannel(applicationContext) == null
                ) {
                    return@suspendRunCatching
                }
                startForeground(
                    context = applicationContext,
                    usbDongle = currentUsbDongle,
                    volumePercent = currentUsbDongle.displayVolumeLevel,
                    volumeStepSize = HardwareVolumeControl.VOLUME_STEP_SIZE_DEFAULT
                )
            }.getOrElse { exception ->
                Timber.e(exception)
            }
        }
    }

    private fun setVolumeLevel(usbDongle: UsbDongle, volumeLevel: Int, volumeStepSize: Int) {
        coroutineScope.launch {
            coroutineContext.suspendRunCatching {
                var currentUsbDongle = getVolumeLevelUseCase(usbDongle).getOrThrow()
                currentUsbDongle = setVolumeLevelUseCase(currentUsbDongle, volumeLevel).getOrThrow()
                if (currentUsbDongle !is HardwareVolumeControl) {
                    return@suspendRunCatching
                }
                UsbServiceNotification.buildAndShow(
                    context = applicationContext,
                    usbDongle = currentUsbDongle,
                    volumePercent = currentUsbDongle.displayVolumeLevel,
                    volumeStepSize = volumeStepSize
                )
            }.getOrElse { exception ->
                Timber.e(exception)
            }
        }
    }
}
