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
sealed class MasterClockDivider(val id: Byte, val index: Int) : Parcelable {

    companion object {

        fun pcmDefaults() = listOf(
            S12Mhz,
            S12Mhz,
            S12Mhz,
            S12Mhz,
            S25Mhz,
            S25Mhz,
            S50Mhz,
            S50Mhz
        )

        fun dsdDefaults() = listOf(
            S12Mhz,
            S50Mhz,
            S50Mhz
        )

        fun findByIdOrDefault(id: Byte): MasterClockDivider = when (id) {
            S12Mhz.id -> S12Mhz
            S25Mhz.id -> S25Mhz
            else -> S50Mhz
        }
    }

    @Parcelize
    data object S12Mhz : MasterClockDivider(id = 8, index = 2)

    @Parcelize
    data object S25Mhz : MasterClockDivider(id = 4, index = 1)

    @Parcelize
    data object S50Mhz : MasterClockDivider(id = 0, index = 0)
}

@Immutable
data class MasterClockDividersDsd(
    val items: List<Pair<Int, Byte>> = MasterClockDivider.dsdDefaults().map { it.index to it.id }
)

@Immutable
data class MasterClockDividersPcm(
    val items: List<Pair<Int, Byte>> = MasterClockDivider.pcmDefaults().map { it.index to it.id }
)

@Immutable
data class AvailableMasterClockDividers(
    val items: List<Byte> = listOf(
        MasterClockDivider.S50Mhz,
        MasterClockDivider.S25Mhz,
        MasterClockDivider.S12Mhz
    ).map { it.id }
)
