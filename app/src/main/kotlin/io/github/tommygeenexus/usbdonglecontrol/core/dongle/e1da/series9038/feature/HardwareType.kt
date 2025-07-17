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
sealed class HardwareType(val displayValue: String, val id: Byte) : Parcelable {

    companion object {

        fun default() = Sg3

        fun findByIdOrDefault(id: Byte): HardwareType = when (id) {
            Sg3.id -> Sg3
            Sg3Susumu.id -> Sg3Susumu
            D.id -> D
            else -> default()
        }
    }

    @Parcelize
    data object Sg3 : HardwareType(displayValue = "#9038SG3", id = 115)

    @Parcelize
    data object Sg3Susumu : HardwareType(displayValue = "#9038SG3 Susumu", id = 116)

    @Parcelize
    data object D : HardwareType(displayValue = "#9038D", id = 100)
}
