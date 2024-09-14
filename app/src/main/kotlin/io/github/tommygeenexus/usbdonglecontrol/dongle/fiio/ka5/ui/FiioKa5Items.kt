/*
 * Copyright (c) 2022-2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToDecibel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToDirection
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToSeconds
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.ItemGain
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPaddingBetween
import io.github.tommygeenexus.usbdonglecontrol.theme.cardSizeMinDp

@Composable
fun FiioKa5Items(
    modifier: Modifier = Modifier,
    fiioKa5: FiioKa5 = FiioKa5(),
    onChannelBalanceSelected: (Int) -> Unit = {},
    onVolumeLevelSelected: (Int) -> Unit = {},
    onVolumeModeSelected: (Byte) -> Unit = {},
    onDisplayBrightnessSelected: (Int) -> Unit = {},
    onDisplayTimeoutSelected: (Int) -> Unit = {},
    onDisplayInvertSelected: (Boolean) -> Unit = {},
    onGainSelected: (Byte) -> Unit = {},
    onFilterSelected: (Byte) -> Unit = {},
    onSpdifOutSelected: (Boolean) -> Unit = {},
    onHardwareMuteSelected: (Boolean) -> Unit = {},
    onDacModeSelected: (Byte) -> Unit = {},
    onHidModeSelected: (Byte) -> Unit = {}
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
                firmwareVersion = fiioKa5.firmwareVersion.displayValue,
                sampleRate = fiioKa5.sampleRate.displayValue
            )
        }
        item {
            val context = LocalContext.current
            ItemAudio(
                channelBalance = fiioKa5.channelBalance.displayValue.toFloat(),
                channelBalanceInDb = fiioKa5.channelBalance.displayValueToDecibel(),
                channelBalanceDirection = fiioKa5.channelBalance.displayValueToDirection(context),
                volumeLevel = fiioKa5.volumeLevel.displayValue.toFloat(),
                volumeLevelInPercent = fiioKa5.displayVolumeLevel,
                volumeMode = fiioKa5.volumeMode,
                onChannelBalanceToDb = { channelBalance ->
                    ChannelBalance
                        .createFromDisplayValue(channelBalance)
                        .displayValueToDecibel(isSignShown = true)
                },
                onChannelBalanceSelected = onChannelBalanceSelected,
                onVolumeLevelToPercent = { volumeLevel ->
                    VolumeLevel
                        .createFromDisplayValue(volumeLevel, fiioKa5.volumeMode)
                        .displayValueToPercent(fiioKa5.volumeMode)
                },
                onVolumeLevelSelected = onVolumeLevelSelected,
                onVolumeModeSelected = onVolumeModeSelected
            )
        }
        item {
            ItemDisplay(
                displayBrightness = fiioKa5.displayBrightness.displayValue.toFloat(),
                displayBrightnessInPercent = fiioKa5.displayBrightness.displayValueToPercent(),
                displayTimeout = fiioKa5.displayTimeout.displayValue.toFloat(),
                displayTimeoutInSeconds = fiioKa5.displayTimeout.displayValueToSeconds(),
                isDisplayInvertEnabled = fiioKa5.displayInvert.isEnabled,
                onDisplayBrightnessToPercent = { displayBrightness ->
                    DisplayBrightness
                        .createFromDisplayValue(displayBrightness)
                        .displayValueToPercent()
                },
                onDisplayBrightnessSelected = onDisplayBrightnessSelected,
                onDisplayTimeoutToSeconds = { displayTimeout ->
                    DisplayTimeout
                        .createFromDisplayValue(displayTimeout)
                        .displayValueToSeconds()
                },
                onDisplayTimeoutSelected = onDisplayTimeoutSelected,
                onDisplayInvertSwitched = onDisplayInvertSelected
            )
        }
        item {
            ItemGain(
                gainId = fiioKa5.gain.id,
                onGainSelected = onGainSelected
            )
        }
        item {
            ItemFilter(
                filterId = fiioKa5.filter.id,
                onFilterSelected = onFilterSelected
            )
        }
        item {
            ItemMisc(
                isSpdifOutEnabled = fiioKa5.spdifOut.isEnabled,
                isHardwareMuteEnabled = fiioKa5.hardwareMute.isEnabled,
                dacModeId = fiioKa5.dacMode.id,
                hidModeId = fiioKa5.hidMode.id,
                onSpdifOutEnabledSwitched = onSpdifOutSelected,
                onHardwareMuteEnabledSwitched = onHardwareMuteSelected,
                onDacModeSelected = onDacModeSelected,
                onHidModeSelected = onHidModeSelected
            )
        }
    }
}

@Preview
@Composable
private fun FiioKa5ItemsPreview() {
    FiioKa5Items()
}
