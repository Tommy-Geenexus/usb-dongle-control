/*
 * Copyright (c) 2024-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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
import kotlin.math.abs
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ChannelBalance(val displayValue: Int, val payload: Pair<Int, Int>) : Parcelable {

    companion object {

        const val STEP_SIZE = 1f
        const val MIN = -12
        const val MAX = 12
        const val DEFAULT = 0
    }
}

fun ChannelBalance.Companion.createFromPayload(
    channelRight: Int,
    channelLeft: Int
): ChannelBalance {
    val displayValue = if (channelRight > DEFAULT) {
        channelRight.coerceAtMost(MAX)
    } else if (channelLeft > DEFAULT) {
        -channelLeft.coerceAtLeast(MIN)
    } else {
        DEFAULT
    }
    return ChannelBalance(
        displayValue = displayValue,
        payload = if (displayValue < DEFAULT) {
            DEFAULT to abs(displayValue)
        } else if (displayValue > DEFAULT) {
            abs(displayValue) to DEFAULT
        } else {
            DEFAULT to DEFAULT
        }
    )
}

fun ChannelBalance.Companion.createFromDisplayValue(displayValue: Int): ChannelBalance {
    val value = displayValue.coerceIn(
        minimumValue = MIN,
        maximumValue = MAX
    )
    return ChannelBalance(
        displayValue = value,
        payload = if (value < DEFAULT) {
            DEFAULT to abs(value)
        } else if (value > DEFAULT) {
            abs(value) to DEFAULT
        } else {
            DEFAULT to DEFAULT
        }
    )
}

fun ChannelBalance.Companion.default() = ChannelBalance(
    displayValue = DEFAULT,
    payload = DEFAULT to DEFAULT
)
