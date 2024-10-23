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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka13.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.ui.ItemFilter
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.ItemAudio
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.ItemIndicatorState
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPaddingBetween
import io.github.tommygeenexus.usbdonglecontrol.theme.cardSizeMinDp

@Composable
fun FiioKa13Items(
    modifier: Modifier = Modifier,
    fiioKa13: FiioKa13 = FiioKa13(),
    onFilterSelected: (Byte) -> Unit = {},
    onIndicatorStateSelected: (Byte) -> Unit = {},
    onSpdifOutSelected: (Boolean) -> Unit = {},
    onVolumeLevelSelected: (Int) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = cardSizeMinDp),
        modifier = modifier,
        contentPadding = PaddingValues(all = cardPaddingBetween),
        verticalItemSpacing = cardPaddingBetween,
        horizontalArrangement = Arrangement.spacedBy(cardPaddingBetween)
    ) {
        item {
            ItemInfo(
                firmwareVersion = fiioKa13.firmwareVersion.displayValue,
                sampleRate = fiioKa13.sampleRate.displayValue
            )
        }
        item {
            ItemMisc(
                isSpdifOutEnabled = fiioKa13.spdifOut.isEnabled,
                onSpdifOutEnabledSwitched = onSpdifOutSelected
            )
        }
        item {
            ItemIndicatorState(
                indicatorStateId = fiioKa13.indicatorState.id,
                onIndicatorStateSelected = onIndicatorStateSelected
            )
        }
        item {
            ItemAudio(
                volumeLevel =
                VolumeLevel.MIN - fiioKa13.volumeLevel.displayValueAndPayload.toFloat(),
                volumeLevelInPercent = fiioKa13.displayVolumeLevel,
                volumeLevelStart = VolumeLevel.MAX.toFloat(),
                volumeLevelEnd = VolumeLevel.MIN.toFloat(),
                volumeLevelStepSize = 2f,
                onVolumeLevelToPercent = { volumeLevel ->
                    VolumeLevel
                        .createFromDisplayValue(VolumeLevel.MIN - volumeLevel)
                        .displayValueToPercent()
                },
                onVolumeLevelSelected = { volumeLevel ->
                    onVolumeLevelSelected(VolumeLevel.MIN - volumeLevel)
                }
            )
        }
        item {
            ItemFilter(
                filterId = fiioKa13.filter.id,
                onFilterSelected = onFilterSelected
            )
        }
    }
}

@Preview
@Composable
private fun FiioKa13ItemsPreview() {
    FiioKa13Items()
}
