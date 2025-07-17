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
data class SampleRate(val displayValue: String) : Parcelable {

    companion object {

        const val DEFAULT = "Unknown"
    }
}

fun SampleRate.Companion.create(key: Int) = SampleRate(
    displayValue = sampleRates.getOrDefault(key = key, defaultValue = DEFAULT)
)

fun SampleRate.Companion.default() = SampleRate(displayValue = DEFAULT)

private val sampleRates = mapOf(
    0 to "PCM 44.1kHz",
    1 to "PCM 48kHz",
    2 to "PCM 88.2kHz",
    3 to "PCM 96kHz",
    4 to "PCM 176.4kHz",
    5 to "PCM 192kHz",
    6 to "PCM 352.8kHz",
    7 to "PCM 384kHz",
    8 to "DSD 64",
    9 to "DSD 128",
    10 to "DSD 256"
)
