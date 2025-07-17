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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlin.math.abs
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class VolumeLevel(val displayValue: Float, val payload: Byte) : Parcelable {

    companion object {
        const val STEP_SIZE = 0.5f
        const val MIN_DB = -127.5f
        const val MAX_DB = 0f
        const val DEFAULT = MAX_DB
    }
}

fun VolumeLevel.Companion.createFromPayload(payload: Byte) = VolumeLevel(
    displayValue = payload.toDb(),
    payload = payload
)

fun VolumeLevel.Companion.createFromDisplayValue(displayValue: Float): VolumeLevel {
    val displayValue = displayValue.coerceIn(minimumValue = MIN_DB, maximumValue = MAX_DB)
    return VolumeLevel(
        displayValue = displayValue,
        payload = displayValue.fromDb()
    )
}

fun VolumeLevel.Companion.default() = VolumeLevel(
    displayValue = DEFAULT,
    payload = DEFAULT.fromDb()
)

internal fun Float.fromDb() = (abs(this) * 2).toInt().toByte()

internal fun Byte.toDb(): Float = if (this > 0) {
    (this.toUByte().toInt() * -0.5f)
} else {
    0f
}
