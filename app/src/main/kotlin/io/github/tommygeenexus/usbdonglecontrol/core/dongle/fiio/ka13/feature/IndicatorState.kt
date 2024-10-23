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
sealed class IndicatorState(val id: Byte, val payload: ByteArray) : Parcelable {

    companion object {

        private val payloadIndicatorStateEnabled = byteArrayOf(-1, 0)
        private val payloadIndicatorStateDisabledTemp = byteArrayOf(-2, 1)
        private val payloadIndicatorStateDisabled = byteArrayOf(-3, 2)

        fun default() = Enabled

        fun findByIdOrDefault(id: Byte): IndicatorState = when (id) {
            Enabled.id -> Enabled
            DisabledTemp.id -> DisabledTemp
            Disabled.id -> Disabled
            else -> default()
        }
    }

    @Parcelize
    data object Enabled : IndicatorState(id = 0, payload = payloadIndicatorStateEnabled)

    @Parcelize
    data object DisabledTemp : IndicatorState(id = 1, payload = payloadIndicatorStateDisabledTemp)

    @Parcelize
    data object Disabled : IndicatorState(id = 2, payload = payloadIndicatorStateDisabled)
}
