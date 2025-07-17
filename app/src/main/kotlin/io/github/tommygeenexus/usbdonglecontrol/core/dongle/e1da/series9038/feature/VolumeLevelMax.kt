/*
 * Copyright (c) 2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class VolumeLevelMax(val displayValue: Float, val payload: Byte) : Parcelable {

    companion object {
        const val MIN_DB = -127.5f
        const val MAX_DB = 0f
        const val DEFAULT = MIN_DB
    }
}

fun VolumeLevelMax.Companion.createFromPayload(payload: Byte) = VolumeLevelMax(
    displayValue = payload.toDb(),
    payload = payload
)

fun VolumeLevelMax.Companion.createFromDisplayValue(displayValue: Float): VolumeLevelMax {
    val displayValue = displayValue.coerceIn(minimumValue = MIN_DB, maximumValue = MAX_DB)
    return VolumeLevelMax(
        displayValue = displayValue,
        payload = displayValue.fromDb()
    )
}

fun VolumeLevelMax.Companion.default() = VolumeLevelMax(
    displayValue = DEFAULT,
    payload = DEFAULT.fromDb()
)
