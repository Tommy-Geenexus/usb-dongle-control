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
data class SampleRate(
    val displayValue: String
) : Parcelable {

    internal companion object {

        const val DEFAULT = "44.1kHz"
    }
}

internal fun SampleRate.Companion.create(key: Int) = SampleRate(
    displayValue = sampleRates.getOrDefault(
        key = key,
        defaultValue = DEFAULT
    )
)

internal fun SampleRate.Companion.default() = SampleRate(displayValue = DEFAULT)

private val sampleRates = mapOf(
    0 to "44.1kHz",
    1 to "48kHz",
    2 to "88.2kHz",
    3 to "96kHz",
    4 to "176.4kHz",
    5 to "192kHz",
    6 to "352.8kHz",
    7 to "384kHz",
    8 to "705.6kHz",
    9 to "768kHz",
    16 to "DoP64",
    17 to "DoP64",
    18 to "DoP128",
    19 to "DoP128",
    20 to "DoP256",
    21 to "DoP256",
    32 to "Native64",
    33 to "Native64",
    34 to "Native128",
    35 to "Native128",
    36 to "Native256",
    37 to "Native256",
    38 to "Native512",
    39 to "Native512"
)
