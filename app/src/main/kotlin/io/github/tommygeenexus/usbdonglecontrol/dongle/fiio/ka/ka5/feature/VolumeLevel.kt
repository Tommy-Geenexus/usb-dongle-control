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
data class VolumeLevel internal constructor(
    val displayValue: Int,
    val payload: Int
) : Parcelable {

    internal companion object {

        const val MIN = 0
        const val MAX_A = 120
        const val MAX_B = 60
        const val DEFAULT = MAX_B
    }
}

internal fun VolumeLevel.Companion.createFromPayload(
    payload: Int,
    volumeMode: VolumeMode
): VolumeLevel {
    return VolumeLevel(
        displayValue = when (volumeMode) {
            VolumeMode.S60 -> volumeLevels60Steps.indexOf(payload).coerceAtLeast(MIN)
            VolumeMode.S120 -> volumeLevels120Steps.indexOf(payload).coerceAtLeast(MIN)
        },
        payload = when (volumeMode) {
            VolumeMode.S60 -> payload.coerceIn(
                minimumValue = volumeLevels60Steps.last(),
                maximumValue = volumeLevels60Steps.first()
            )
            VolumeMode.S120 -> payload.coerceIn(
                minimumValue = volumeLevels120Steps.last(),
                maximumValue = volumeLevels120Steps.first()
            )
        }
    )
}

internal fun VolumeLevel.Companion.createFromDisplayValue(
    displayValue: Int,
    volumeMode: VolumeMode
) = VolumeLevel(
    displayValue = when (volumeMode) {
        VolumeMode.S60 -> displayValue.coerceIn(
            minimumValue = MIN,
            maximumValue = MAX_B
        )
        VolumeMode.S120 -> displayValue.coerceIn(
            minimumValue = MIN,
            maximumValue = MAX_A
        )
    },
    payload = when (volumeMode) {
        VolumeMode.S60 -> volumeLevels60Steps.getOrElse(displayValue) { MIN }
        VolumeMode.S120 -> volumeLevels120Steps.getOrElse(displayValue) { MIN }
    }
)

internal fun VolumeLevel.Companion.default() = VolumeLevel(
    displayValue = DEFAULT,
    payload = volumeLevels60Steps[DEFAULT]
)

internal fun VolumeLevel.displayValueToPercent(volumeMode: VolumeMode): String {
    return "${(displayValue * 100 / volumeMode.steps)}%"
}

private val volumeLevels60Steps = listOf(
    255,
    190,
    182,
    174,
    166,
    158,
    150,
    142,
    134,
    126,
    120,
    114,
    108,
    102,
    96,
    90,
    86,
    82,
    78,
    74,
    70,
    66,
    62,
    58,
    54,
    50,
    48,
    46,
    44,
    42,
    40,
    38,
    36,
    34,
    32,
    30,
    28,
    26,
    24,
    22,
    20,
    19,
    18,
    17,
    16,
    15,
    14,
    13,
    12,
    11,
    10,
    9,
    8,
    7,
    6,
    5,
    4,
    3,
    2,
    1,
    0
)

private val volumeLevels120Steps = listOf(
    255,
    190,
    182,
    178,
    174,
    170,
    166,
    162,
    158,
    154,
    150,
    148,
    146,
    144,
    142,
    140,
    138,
    136,
    134,
    132,
    130,
    128,
    126,
    124,
    122,
    120,
    118,
    116,
    114,
    112,
    110,
    108,
    106,
    104,
    102,
    100,
    98,
    96,
    94,
    92,
    90,
    88,
    86,
    84,
    82,
    80,
    78,
    76,
    74,
    72,
    70,
    69,
    68,
    67,
    66,
    65,
    64,
    63,
    62,
    61,
    60,
    59,
    58,
    57,
    56,
    55,
    54,
    53,
    52,
    51,
    50,
    49,
    48,
    47,
    46,
    45,
    44,
    43,
    42,
    41,
    40,
    39,
    38,
    37,
    36,
    35,
    34,
    33,
    32,
    31,
    30,
    29,
    28,
    27,
    26,
    25,
    24,
    23,
    22,
    21,
    20,
    19,
    18,
    17,
    16,
    15,
    14,
    13,
    12,
    11,
    10,
    9,
    8,
    7,
    6,
    5,
    4,
    3,
    2,
    1,
    0
)
