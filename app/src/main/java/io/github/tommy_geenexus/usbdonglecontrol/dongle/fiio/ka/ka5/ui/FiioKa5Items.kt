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
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
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
    firmwareVersion: String = FiioKa5Defaults.FW_VERSION,
    sampleRate: String = FiioKa5Defaults.SAMPLE_RATE,
    channelBalance: Float = FiioKa5Defaults.CHANNEL_BALANCE.toFloat(),
    volumeLevel: Float = FiioKa5Defaults.VOLUME_LEVEL.toFloat(),
    volumeMode: VolumeMode = VolumeMode.default(),
    onChannelBalanceSelected: (Float) -> Unit = {},
    onVolumeLevelSelected: (Float) -> Unit = {},
    onVolumeModeSelected: (VolumeMode) -> Unit = {},
    displayBrightness: Float = FiioKa5Defaults.DISPLAY_BRIGHTNESS.toFloat(),
    displayTimeout: Float = FiioKa5Defaults.DISPLAY_TIMEOUT.toFloat(),
    displayInvertEnabled: Boolean = false,
    onDisplayBrightnessSelected: (Float) -> Unit = {},
    onDisplayTimeoutSelected: (Float) -> Unit = {},
    onDisplayInvertChange: (Boolean) -> Unit = {},
    gain: Gain = Gain.default(),
    onGainSelected: (Gain) -> Unit = {},
    filter: Filter = Filter.default(),
    onFilterSelected: (Filter) -> Unit = {},
    spdifOutEnabled: Boolean = false,
    hardwareMuteEnabled: Boolean = false,
    dacMode: DacMode = DacMode.default(),
    hidMode: HidMode = HidMode.default(),
    onSpdifOutEnabledSelected: (Boolean) -> Unit = {},
    onHardwareMuteEnabledSelected: (Boolean) -> Unit = {},
    onDacModeSelected: (DacMode) -> Unit = {},
    onHidModeSelected: (HidMode) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        item {
            ItemInfo(
                firmwareVersion = firmwareVersion,
                sampleRate = sampleRate
            )
        }
        item {
            ItemAudio(
                channelBalance = channelBalance,
                volumeLevel = volumeLevel,
                volumeMode = volumeMode,
                onChannelBalanceSelected = onChannelBalanceSelected,
                onVolumeLevelSelected = onVolumeLevelSelected,
                onVolumeModeSelected = onVolumeModeSelected
            )
        }
        item {
            ItemDisplay(
                displayBrightness = displayBrightness,
                displayTimeout = displayTimeout,
                displayInvertEnabled = displayInvertEnabled,
                onDisplayBrightnessSelected = onDisplayBrightnessSelected,
                onDisplayTimeoutSelected = onDisplayTimeoutSelected,
                onDisplayInvertChange = onDisplayInvertChange
            )
        }
        item {
            ItemGain(
                gain = gain,
                onGainSelected = onGainSelected
            )
        }
        item {
            ItemFilter(
                filter = filter,
                onFilterSelected = onFilterSelected
            )
        }
        item {
            ItemMisc(
                spdifOutEnabled = spdifOutEnabled,
                hardwareMuteEnabled = hardwareMuteEnabled,
                dacMode = dacMode,
                hidMode = hidMode,
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
