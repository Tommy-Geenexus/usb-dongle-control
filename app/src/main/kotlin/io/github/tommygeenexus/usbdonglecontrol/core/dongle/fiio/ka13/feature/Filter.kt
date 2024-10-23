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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
sealed class Filter(val id: Byte, val payload: Byte) : Parcelable {

    companion object {

        fun default() = FastRollOffLowLatency

        fun findByIdOrDefault(id: Byte): Filter = when (id) {
            FastRollOffLowLatency.id -> FastRollOffLowLatency
            FastRollOffPhaseCompensated.id -> FastRollOffPhaseCompensated
            SlowRollOffLowLatency.id -> SlowRollOffLowLatency
            SlowRollOffPhaseCompensated.id -> SlowRollOffPhaseCompensated
            NonOversampling.id -> NonOversampling
            else -> default()
        }
    }

    @Parcelize
    data object FastRollOffLowLatency : Filter(id = 0, payload = 2)

    @Parcelize
    data object FastRollOffPhaseCompensated : Filter(id = 1, payload = 66)

    @Parcelize
    data object SlowRollOffLowLatency : Filter(id = 2, payload = 130.toByte())

    @Parcelize
    data object SlowRollOffPhaseCompensated : Filter(id = 3, payload = 194.toByte())

    @Parcelize
    data object NonOversampling : Filter(id = 4, payload = 34)
}
