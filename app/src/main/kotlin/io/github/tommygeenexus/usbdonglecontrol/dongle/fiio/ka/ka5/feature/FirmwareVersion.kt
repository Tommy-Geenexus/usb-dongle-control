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
data class FirmwareVersion(
    val displayValue: String
) : Parcelable {

    internal companion object {

        const val DEFAULT = "01.00"
    }
}

internal fun FirmwareVersion.Companion.createFromPayload(payload: Byte) = FirmwareVersion(
    displayValue = if (payload >= 100) {
        StringBuilder(payload.toString())
            .insert(2, ".")
            .toString()
    } else if (payload >= 10) {
        StringBuilder(payload.toString())
            .insert(0, "0")
            .insert(2, ".")
            .append("0")
            .toString()
    } else {
        StringBuilder(payload.toString())
            .insert(0, "00.")
            .append("0")
            .toString()
    }
)

internal fun FirmwareVersion.Companion.default() = FirmwareVersion(displayValue = DEFAULT)
