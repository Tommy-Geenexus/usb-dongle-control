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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.IndicatorState

@Composable
fun MoondropDawn44Items(
    modifier: Modifier = Modifier,
    moondropDawn44: MoondropDawn44 = MoondropDawn44(),
    onFilterSelected: (Filter) -> Unit = {},
    onGainSelected: (Gain) -> Unit = {},
    onIndicatorStateSelected: (IndicatorState) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        item {
            ItemFilter(
                filter = moondropDawn44.filter,
                onFilterSelected = onFilterSelected
            )
        }
        item {
            ItemGain(
                gain = moondropDawn44.gain,
                onGainSelected = onGainSelected
            )
        }
        item {
            ItemIndicatorState(
                indicatorState = moondropDawn44.indicatorState,
                onIndicatorStateSelected = onIndicatorStateSelected
            )
        }
    }
}

@Preview
@Composable
fun MoondropDawn44ItemsPreview() {
    MoondropDawn44Items()
}
