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

package io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.moonriver2ti.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.moonriver2ti.MoondropMoonriver2Ti
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.ui.ItemGain
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.ItemAudio
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.ItemFilter
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.ItemIndicatorState
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPaddingBetween
import io.github.tommygeenexus.usbdonglecontrol.theme.cardSizeMinDp

@Composable
fun MoondropMoonriver2TiItems(
    modifier: Modifier = Modifier,
    moondropMoonriver2Ti: MoondropMoonriver2Ti = MoondropMoonriver2Ti(),
    onFilterSelected: (Byte) -> Unit = {},
    onGainSelected: (Byte) -> Unit = {},
    onIndicatorStateSelected: (Byte) -> Unit = {},
    onVolumeLevelSelected: (Float) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = cardSizeMinDp),
        modifier = modifier,
        contentPadding = PaddingValues(all = cardPaddingBetween),
        verticalItemSpacing = cardPaddingBetween,
        horizontalArrangement = Arrangement.spacedBy(cardPaddingBetween)
    ) {
        item {
            ItemFilter(
                filterId = moondropMoonriver2Ti.filter.id,
                onFilterSelected = onFilterSelected
            )
        }
        item {
            ItemGain(
                gainId = moondropMoonriver2Ti.gain.id,
                onGainSelected = onGainSelected
            )
        }
        item {
            ItemIndicatorState(
                indicatorStateId = moondropMoonriver2Ti.indicatorState.id,
                onIndicatorStateSelected = onIndicatorStateSelected
            )
        }
        item {
            ItemAudio(
                volumeLevel =
                VolumeLevel.MIN -
                    moondropMoonriver2Ti.volumeLevel.displayValue.toFloat(),
                volumeLevelInPercent = moondropMoonriver2Ti.displayVolumeLevel(
                    LocalContext.current
                ),
                onVolumeLevelSelected = { volumeLevel ->
                    onVolumeLevelSelected(VolumeLevel.MIN - volumeLevel)
                }
            )
        }
    }
}

@Preview
@Composable
private fun MoondropMoonriver2TiItemsPreview() {
    MoondropMoonriver2TiItems()
}
