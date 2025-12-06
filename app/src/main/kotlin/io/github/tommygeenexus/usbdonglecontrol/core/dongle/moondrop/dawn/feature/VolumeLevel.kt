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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class VolumeLevel(val displayValue: Int) : Parcelable {

    companion object {
        const val STEP_SIZE = 1f
        const val MIN = 60
        const val MAX = 0
        const val DEFAULT = MAX

        private val dawnVolumeTable = arrayOf(
            intArrayOf(255, 0), intArrayOf(200, 1), intArrayOf(180, 2), intArrayOf(170, 3),
            intArrayOf(160, 4), intArrayOf(150, 5), intArrayOf(140, 6), intArrayOf(130, 7),
            intArrayOf(122, 8), intArrayOf(116, 9), intArrayOf(110, 10), intArrayOf(106, 11),
            intArrayOf(102, 12), intArrayOf(98, 13), intArrayOf(94, 14), intArrayOf(90, 15),
            intArrayOf(88, 16), intArrayOf(86, 17), intArrayOf(84, 18), intArrayOf(82, 19),
            intArrayOf(80, 20), intArrayOf(78, 21), intArrayOf(76, 22), intArrayOf(74, 23),
            intArrayOf(72, 24), intArrayOf(70, 25), intArrayOf(68, 26), intArrayOf(66, 27),
            intArrayOf(64, 28), intArrayOf(62, 29), intArrayOf(60, 30), intArrayOf(58, 31),
            intArrayOf(56, 32), intArrayOf(54, 33), intArrayOf(52, 34), intArrayOf(50, 35),
            intArrayOf(48, 36), intArrayOf(46, 37), intArrayOf(44, 38), intArrayOf(42, 39),
            intArrayOf(40, 40), intArrayOf(38, 41), intArrayOf(36, 42), intArrayOf(34, 43),
            intArrayOf(32, 44), intArrayOf(30, 45), intArrayOf(28, 46), intArrayOf(26, 47),
            intArrayOf(24, 48), intArrayOf(22, 49), intArrayOf(20, 50), intArrayOf(18, 51),
            intArrayOf(16, 52), intArrayOf(14, 53), intArrayOf(12, 54), intArrayOf(10, 55),
            intArrayOf(8, 56), intArrayOf(6, 57), intArrayOf(4, 58), intArrayOf(2, 59),
            intArrayOf(0, 60)
        )

        fun rawToNormal(raw: Int): Int {
            for (entry in dawnVolumeTable) {
                if (raw == entry[0]) {
                    return entry[1]
                }
            }
            return -1
        }

        fun normalToRaw(normal: Int): Int {
            for (entry in dawnVolumeTable) {
                if (normal == entry[1]) {
                    return entry[0]
                }
            }
            return -1
        }
    }
}

fun VolumeLevel.Companion.createFromDisplayValue(displayValue: Int) = VolumeLevel(
    displayValue = displayValue.coerceIn(
        minimumValue = MAX,
        maximumValue = MIN
    )
)

fun VolumeLevel.Companion.default() = VolumeLevel(displayValue = DEFAULT)
