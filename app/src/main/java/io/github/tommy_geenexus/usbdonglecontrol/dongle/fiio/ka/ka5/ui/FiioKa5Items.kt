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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.DacMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.HidMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.VolumeMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.ui.ItemFilter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.ui.ItemGain

@Composable
fun FiioKa5Items(
    modifier: Modifier = Modifier,
    fiioKa5: FiioKa5 = FiioKa5(),
    onChannelBalanceSelected: (Float) -> Unit = {},
    onVolumeLevelSelected: (Float) -> Unit = {},
    onVolumeModeSelected: (VolumeMode) -> Unit = {},
    onDisplayBrightnessSelected: (Float) -> Unit = {},
    onDisplayTimeoutSelected: (Float) -> Unit = {},
    onDisplayInvertChange: (Boolean) -> Unit = {},
    onGainSelected: (Gain) -> Unit = {},
    onFilterSelected: (Filter) -> Unit = {},
    onSpdifOutEnabledSelected: (Boolean) -> Unit = {},
    onHardwareMuteEnabledSelected: (Boolean) -> Unit = {},
    onDacModeSelected: (DacMode) -> Unit = {},
    onHidModeSelected: (HidMode) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        item {
            ItemInfo(
                firmwareVersion = fiioKa5.firmwareVersion,
                sampleRate = fiioKa5.sampleRate
            )
        }
        item {
            ItemAudio(
                channelBalance = fiioKa5.channelBalance.toFloat(),
                volumeLevel = fiioKa5.volumeLevel.toFloat(),
                volumeLevelInPercent = fiioKa5.currentVolumeLevelInPercent(),
                volumeMode = fiioKa5.volumeMode,
                onChannelBalanceSelected = onChannelBalanceSelected,
                onVolumeLevelSelected = onVolumeLevelSelected,
                onVolumeModeSelected = onVolumeModeSelected
            )
        }
        item {
            ItemDisplay(
                displayBrightness = fiioKa5.displayBrightness.toFloat(),
                displayTimeout = fiioKa5.displayTimeout.toFloat(),
                displayInvertEnabled = fiioKa5.displayInvertEnabled,
                onDisplayBrightnessSelected = onDisplayBrightnessSelected,
                onDisplayTimeoutSelected = onDisplayTimeoutSelected,
                onDisplayInvertChange = onDisplayInvertChange
            )
        }
        item {
            ItemGain(
                gain = fiioKa5.gain,
                onGainSelected = onGainSelected
            )
        }
        item {
            ItemFilter(
                filter = fiioKa5.filter,
                onFilterSelected = onFilterSelected
            )
        }
        item {
            ItemMisc(
                spdifOutEnabled = fiioKa5.spdifOutEnabled,
                hardwareMuteEnabled = fiioKa5.hardwareMuteEnabled,
                dacMode = fiioKa5.dacMode,
                hidMode = fiioKa5.hidMode,
                onSpdifOutEnabledSelected = onSpdifOutEnabledSelected,
                onHardwareMuteEnabledSelected = onHardwareMuteEnabledSelected,
                onDacModeSelected = onDacModeSelected,
                onHidModeSelected = onHidModeSelected
            )
        }
    }
}

@Preview
@Composable
fun FiioKa5ItemsPreview() {
    FiioKa5Items()
}
