/*
 * Copyright (c) 2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.IntentCompat
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import io.github.tommygeenexus.usbdonglecontrol.core.volume.volumeDown
import io.github.tommygeenexus.usbdonglecontrol.core.volume.volumeUp
import io.github.tommygeenexus.usbdonglecontrol.volume.ui.UsbServiceNotification

class UsbServiceVolumeLevelReceiver(
    private val onSetVolumeLevel: (UsbDongle, Int, Int) -> Unit,
    private val onSetVolumeStepSize: (UsbDongle, Int) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        val usbDongle = IntentCompat.getParcelableExtra(
            intent,
            UsbServiceNotification.INTENT_EXTRA_USB_DONGLE,
            UsbDongle::class.java
        ) ?: return
        val volumeStepSize = intent.getIntExtra(
            UsbServiceNotification.INTENT_EXTRA_VOLUME_STEP_SIZE,
            UsbServiceNotification.VOLUME_STEP_SIZE_MIN
        )
        if (usbDongle !is HardwareVolumeControl) {
            return
        }
        when (intent.action) {
            UsbServiceNotification.INTENT_ACTION_VOLUME_DOWN -> {
                onSetVolumeLevel(
                    usbDongle,
                    usbDongle.volumeDown(volumeStepSize),
                    volumeStepSize
                )
            }
            UsbServiceNotification.INTENT_ACTION_VOLUME_UP -> {
                onSetVolumeLevel(
                    usbDongle,
                    usbDongle.volumeUp(volumeStepSize),
                    volumeStepSize
                )
            }
            UsbServiceNotification.INTENT_ACTION_VOLUME_STEP_SIZE -> {
                onSetVolumeStepSize(
                    usbDongle,
                    incrementOrWrapVolumeStepSize(
                        minVolumeStepSize = UsbServiceNotification.VOLUME_STEP_SIZE_MIN,
                        maxVolumeStepSize = UsbServiceNotification.VOLUME_STEP_SIZE_MAX,
                        currentVolumeStepSize = volumeStepSize
                    )
                )
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun incrementOrWrapVolumeStepSize(
        minVolumeStepSize: Int,
        maxVolumeStepSize: Int,
        currentVolumeStepSize: Int
    ): Int {
        var nextVolumeStepSize = currentVolumeStepSize.inc()
        if (nextVolumeStepSize > maxVolumeStepSize) {
            nextVolumeStepSize = minVolumeStepSize
        }
        return nextVolumeStepSize
    }
}
