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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class DisplayTimeout(
    val displayValue: Int,
    val payload: Int
) : Parcelable {

    internal companion object {

        const val MIN = 0
        const val MAX = 60
        const val DEFAULT = 15
    }
}

internal fun DisplayTimeout.Companion.createFromPayload(payload: Int) = DisplayTimeout(
    displayValue = payload.coerceIn(
        minimumValue = MIN,
        maximumValue = MAX
    ),
    payload = payload.coerceIn(
        minimumValue = MIN,
        maximumValue = MAX
    )
)

internal fun DisplayTimeout.Companion.createFromDisplayValue(displayValue: Int) = DisplayTimeout(
    displayValue = displayValue.coerceIn(
        minimumValue = MIN,
        maximumValue = MAX
    ),
    payload = displayValue.coerceIn(
        minimumValue = MIN,
        maximumValue = MAX
    )
)

internal fun DisplayTimeout.Companion.default() = DisplayTimeout(
    displayValue = DEFAULT,
    payload = DEFAULT
)

internal fun DisplayTimeout.displayValueToSeconds() = "${displayValue}s"
