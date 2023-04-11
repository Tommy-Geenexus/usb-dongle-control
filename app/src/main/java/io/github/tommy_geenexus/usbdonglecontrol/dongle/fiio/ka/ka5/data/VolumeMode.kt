/*
 * Copyright (c) 2022-2023, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class VolumeMode(
    val id: Byte,
    val steps: Int
) : Parcelable {

    companion object {

        fun default() = S120

        fun findById(id: Byte): VolumeMode? {
            return when (id) {
                S120.id -> S120
                S60.id -> S60
                else -> null
            }
        }
    }

    @Parcelize
    object S120 : VolumeMode(id = 0, steps = 120)

    @Parcelize
    object S60 : VolumeMode(id = 1, steps = 60)
}
