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

package io.github.tommygeenexus.usbdonglecontrol.dongle.e1da.series9038.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.E1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Filters
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.MasterClockDividersDsd
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.MasterClockDividersPcm
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPaddingBetween
import io.github.tommygeenexus.usbdonglecontrol.theme.cardSizeMinDp

@Composable
fun E1da9038Items(
    modifier: Modifier = Modifier,
    e1da9038: E1da9038 = E1da9038(),
    onFilterSelected: (Byte, Int) -> Unit = { _, _ -> },
    onHardwareMuteEnabledSelected: (Boolean) -> Unit = {},
    onMasterClockDividerDsdSelected: (Byte, Int) -> Unit = { _, _ -> },
    onMasterClockDividerPcmSelected: (Byte, Int) -> Unit = { _, _ -> },
    onStandbyEnabledSelected: (Boolean) -> Unit = {},
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
            ItemInfo(
                model = e1da9038.hardwareType.displayValue,
                firmwareVersion = e1da9038.firmwareVersion.displayValue,
                sampleRate = e1da9038.sampleRate.displayValue
            )
        }
        item {
            ItemAudio(
                volumeLevel = e1da9038.volumeLevel.displayValue,
                onVolumeLevelSelected = onVolumeLevelSelected
            )
        }
        item {
            ItemFilter(
                currentFilters = Filters(items = e1da9038.filters.map { it.index to it.id }),
                onFilterSelected = onFilterSelected
            )
        }
        item {
            ItemMasterClockPcm(
                currentMasterClockDividers = MasterClockDividersPcm(
                    items = e1da9038.masterClockDividersPcm.map { it.index to it.id }
                ),
                onMasterClockDividerPcmSelected = onMasterClockDividerPcmSelected
            )
        }
        item {
            ItemMasterClockDsd(
                currentMasterClockDividers = MasterClockDividersDsd(
                    items = e1da9038.masterClockDividersDsd.map { it.index to it.id }
                ),
                onMasterClockDividerDsdSelected = onMasterClockDividerDsdSelected
            )
        }
        item {
            ItemMisc(
                isStandbyEnabled = e1da9038.standby.isEnabled,
                isHardwareMuteEnabled = e1da9038.hardwareMute.isEnabled,
                onStandbyEnabledSwitched = onStandbyEnabledSelected,
                onHardwareMuteEnabledSwitched = onHardwareMuteEnabledSelected
            )
        }
    }
}

@Preview
@Composable
private fun E1da9038ItemsItemsPreview() {
    E1da9038Items()
}
