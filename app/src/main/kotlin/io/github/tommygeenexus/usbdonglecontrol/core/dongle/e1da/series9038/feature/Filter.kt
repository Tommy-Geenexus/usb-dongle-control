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
sealed class Filter(val id: Byte, val index: Int) : Parcelable {

    companion object {

        fun defaults() = listOf(
            LinearPhaseSlow,
            LinearPhaseSlow,
            LinearPhaseSlow,
            LinearPhaseSlow,
            MinPhaseFast,
            MinPhaseFast,
            MinPhaseFast,
            MinPhaseFast
        )

        fun findByIdOrDefault(id: Byte): Filter = when (id) {
            ApodizingFast.id -> ApodizingFast
            BrickWall.id -> BrickWall
            CorrectedMinPhaseFast.id -> CorrectedMinPhaseFast
            LinearPhaseFast.id -> LinearPhaseFast
            LinearPhaseSlow.id -> LinearPhaseSlow
            MinPhaseFast.id -> MinPhaseFast
            MinPhaseSlow.id -> MinPhaseSlow
            else -> LinearPhaseSlow
        }
    }

    @Parcelize
    data object ApodizingFast : Filter(id = Byte.MIN_VALUE, index = 4)

    @Parcelize
    data object BrickWall : Filter(id = -32, index = 6)

    @Parcelize
    data object CorrectedMinPhaseFast : Filter(id = -64, index = 5)

    @Parcelize
    data object LinearPhaseFast : Filter(id = 0, index = 0)

    @Parcelize
    data object LinearPhaseSlow : Filter(id = 32, index = 1)

    @Parcelize
    data object MinPhaseFast : Filter(id = 64, index = 2)

    @Parcelize
    data object MinPhaseSlow : Filter(id = 96, index = 3)
}

@Immutable
data class Filters(val items: List<Pair<Int, Byte>> = Filter.defaults().map { it.index to it.id })

@Immutable
data class AvailableFilters(
    val items: List<Byte> = listOf(
        Filter.LinearPhaseFast,
        Filter.LinearPhaseSlow,
        Filter.MinPhaseFast,
        Filter.MinPhaseSlow,
        Filter.ApodizingFast,
        Filter.CorrectedMinPhaseFast,
        Filter.BrickWall
    ).map { it.id }
)
