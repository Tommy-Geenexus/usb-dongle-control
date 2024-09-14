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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class DisplayBrightness(val displayValue: Int, val payload: Int) : Parcelable {

    companion object {

        const val MIN = 1
        const val MAX = 10
        const val DEFAULT = 3
    }
}

fun DisplayBrightness.Companion.createFromPayload(payload: Int) = DisplayBrightness(
    displayValue = (displayBrightness.indexOf(payload) + 1).coerceAtLeast(MIN),
    payload = payload
)

fun DisplayBrightness.Companion.createFromDisplayValue(displayValue: Int): DisplayBrightness =
    DisplayBrightness(
        displayValue = displayValue,
        payload = displayBrightness.getOrElse(displayValue - 1) { DEFAULT }
    )

fun DisplayBrightness.Companion.default() = DisplayBrightness(
    displayValue = DEFAULT,
    payload = displayBrightness.getOrElse(DEFAULT - 1) { DEFAULT }
)

fun DisplayBrightness.displayValueToPercent(): String =
    "${(payload * 100) / displayBrightness.last()}%"

private val displayBrightness = listOf(0, 10, 25, 40, 60, 80, 105, 130, 160, 200)
