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

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import io.github.tommygeenexus.usbdonglecontrol.R
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ChannelBalance(
    val displayValue: Int,
    val payload: Pair<Int, Int>
) : Parcelable {

    internal companion object {

        const val MIN = -12
        const val MAX = 12
        const val DEFAULT = 0
    }
}

internal fun ChannelBalance.Companion.createFromPayload(
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

internal fun ChannelBalance.Companion.createFromDisplayValue(displayValue: Int): ChannelBalance {
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

internal fun ChannelBalance.Companion.default() = ChannelBalance(
    displayValue = DEFAULT,
    payload = DEFAULT to DEFAULT
)

internal fun ChannelBalance.displayValueToDecibel(isSignShown: Boolean = false): String {
    val offsetDb = if (displayValue != 0) {
        displayValue.absoluteValue / 2f
    } else {
        0f
    }
    val sign = if (isSignShown && displayValue < 0) {
        "-"
    } else if (isSignShown && displayValue > 0) {
        "+"
    } else {
        ""
    }
    return "${sign}$offsetDb dB"
}

internal fun ChannelBalance.displayValueToDirection(context: Context) = if (displayValue > 0) {
    context.getString(R.string.right)
} else if (displayValue < 0) {
    context.getString(R.string.left)
} else {
    ""
}
