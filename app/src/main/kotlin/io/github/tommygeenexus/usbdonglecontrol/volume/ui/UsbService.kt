/*
 * Copyright (c) 2022-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.volume.ui

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.tommygeenexus.usbdonglecontrol.control.domain.GetCurrentStateUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.GetVolumeLevelUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetVolumeLevelUseCase
import io.github.tommygeenexus.usbdonglecontrol.core.data.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.core.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.receiver.UsbServiceVolumeLevelReceiver
import io.github.tommygeenexus.usbdonglecontrol.core.util.TOP_LEVEL_PACKAGE_NAME
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsbService : Service() {

    companion object {

        const val INTENT_ACTION_VOLUME_CHANGED = TOP_LEVEL_PACKAGE_NAME + "VOLUME_CHANGED"
    }

    @Inject
    lateinit var usbRepository: UsbRepository

    @Inject
    lateinit var getCurrentStateUseCase: GetCurrentStateUseCase

    @Inject
    lateinit var getVolumeLevelUseCase: GetVolumeLevelUseCase

    @Inject
    lateinit var setVolumeLevelUseCase: SetVolumeLevelUseCase

    @DispatcherIo
    @Inject
    lateinit var dispatcherIo: CoroutineDispatcher

    private lateinit var usbServiceVolumeLevelReceiver: UsbServiceVolumeLevelReceiver
    private lateinit var coroutineScope: CoroutineScope

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        usbServiceVolumeLevelReceiver = UsbServiceVolumeLevelReceiver(
            onSetVolumeLevel = { usbDongle, volumeLevel, volumeStepSize ->
                setVolumeLevel(usbDongle, volumeLevel, volumeStepSize)
            },
            onSetVolumeStepSize = { usbDongle, volumeStepSize ->
                setVolumeStepSize(usbDongle, volumeStepSize)
            }
        )
        ContextCompat.registerReceiver(
            this,
            usbServiceVolumeLevelReceiver,
            IntentFilter(UsbServiceNotification.INTENT_ACTION_VOLUME_DOWN).apply {
                addAction(UsbServiceNotification.INTENT_ACTION_VOLUME_UP)
                addAction(UsbServiceNotification.INTENT_ACTION_VOLUME_STEP_SIZE)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        coroutineScope = CoroutineScope(dispatcherIo)
        showCurrentState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        START_REDELIVER_INTENT

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbServiceVolumeLevelReceiver)
        coroutineScope.cancel()
    }

    private fun showCurrentState() {
        coroutineScope.launch {
            val (_, usbDongle, _) = usbRepository
                .getFirstAttachedUsbDongle()
                .getOrNull()
                ?: return@launch
            val currentUsbDongle = getVolumeLevelUseCase(usbDongle)
                .getOrNull()
                ?: return@launch
            if (currentUsbDongle !is HardwareVolumeControl ||
                UsbServiceNotification.createNotificationChannel(applicationContext) == null
            ) {
                return@launch
            }
            startForeground(
                context = applicationContext,
                usbDongle = currentUsbDongle,
                volumeStepSize = currentUsbDongle.volumeStepSizeMin
            )
        }
    }

    private fun setVolumeLevel(usbDongle: UsbDongle, volumeLevel: Float, volumeStepSize: Float) {
        coroutineScope.launch {
            var currentUsbDongle = getVolumeLevelUseCase(usbDongle)
                .getOrNull()
                ?: return@launch
            currentUsbDongle = setVolumeLevelUseCase(currentUsbDongle, volumeLevel)
                .getOrNull()
                ?: return@launch
            if (currentUsbDongle !is HardwareVolumeControl) {
                return@launch
            }
            UsbServiceNotification.buildAndShow(
                context = this@UsbService,
                usbDongle = currentUsbDongle,
                volumeStepSize = volumeStepSize
            )
            sendBroadcast(
                Intent(INTENT_ACTION_VOLUME_CHANGED).apply {
                    setPackage(packageName)
                    putExtra(
                        UsbServiceNotification.INTENT_EXTRA_VOLUME_LEVEL,
                        currentUsbDongle.currentVolumeLevel
                    )
                }
            )
        }
    }

    private fun setVolumeStepSize(usbDongle: UsbDongle, volumeStepSize: Float) {
        if (usbDongle !is HardwareVolumeControl) {
            return
        }
        UsbServiceNotification.buildAndShow(
            context = this,
            usbDongle = usbDongle,
            volumeStepSize = volumeStepSize
        )
    }
}
