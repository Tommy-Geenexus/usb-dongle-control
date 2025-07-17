/*
 * Copyright (c) 2023-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.core.volume

import android.content.Context

interface HardwareVolumeControl {
    companion object {
        const val VOLUME_STEP_SIZE_MAX = 3
    }

    val currentVolumeLevel: Float
    val isVolumeControlInverted: Boolean
    val volumeStepSizeMin: Float

    fun displayVolumeLevel(context: Context): String
}

fun HardwareVolumeControl.incrementOrWrapVolumeStepSize(volumeStepSize: Float): Float {
    var nextVolumeStepSize = volumeStepSize + volumeStepSizeMin
    if (nextVolumeStepSize > HardwareVolumeControl.VOLUME_STEP_SIZE_MAX) {
        nextVolumeStepSize = volumeStepSizeMin
    }
    return nextVolumeStepSize
}

fun HardwareVolumeControl.volumeDown(volumeStepSize: Float) = if (isVolumeControlInverted) {
    currentVolumeLevel + volumeStepSize
} else {
    currentVolumeLevel - volumeStepSize
}

fun HardwareVolumeControl.volumeUp(volumeStepSize: Float) = if (isVolumeControlInverted) {
    currentVolumeLevel - volumeStepSize
} else {
    currentVolumeLevel + volumeStepSize
}
