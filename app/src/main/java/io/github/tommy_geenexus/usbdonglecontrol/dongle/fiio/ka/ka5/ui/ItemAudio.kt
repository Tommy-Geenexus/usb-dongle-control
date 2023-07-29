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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.VolumeMode
import io.github.tommy_geenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun ItemAudio(
    modifier: Modifier = Modifier,
    channelBalance: Float = FiioKa5Defaults.CHANNEL_BALANCE.toFloat(),
    channelBalanceStepSize: Int =
        FiioKa5Defaults.CHANNEL_BALANCE_MIN.absoluteValue + FiioKa5Defaults.CHANNEL_BALANCE_MAX,
    channelBalanceRange: ClosedFloatingPointRange<Float> =
        FiioKa5Defaults.CHANNEL_BALANCE_MIN.toFloat()..FiioKa5Defaults.CHANNEL_BALANCE_MAX.toFloat(),
    volumeMode: VolumeMode = VolumeMode.default(),
    volumeLevel: Float = FiioKa5Defaults.VOLUME_LEVEL.toFloat(),
    volumeLevelInPercent: String = FiioKa5Defaults.VOLUME_LEVEL.toString(),
    volumeStepSize: Int = volumeMode.steps,
    volumeRange: ClosedFloatingPointRange<Float> = if (volumeMode == VolumeMode.S120) {
        FiioKa5Defaults.VOLUME_LEVEL_MIN.toFloat()..FiioKa5Defaults.VOLUME_LEVEL_A_MAX.toFloat()
    } else {
        FiioKa5Defaults.VOLUME_LEVEL_MIN.toFloat()..FiioKa5Defaults.VOLUME_LEVEL_B_MAX.toFloat()
    },
    onChannelBalanceChanged: (Int) -> Unit = {},
    onChannelBalanceSelected: (Int) -> Unit = {},
    onVolumeLevelChanged: (Int) -> Unit = {},
    onVolumeLevelSelected: (Int) -> Unit = {},
    onVolumeModeSelected: (VolumeMode) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.channel_balance),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (channelBalance > 0) {
                    stringResource(
                        id = R.string.channel_balance_offset,
                        channelBalance.absoluteValue / 2,
                        stringResource(id = R.string.right)
                    )
                } else if (channelBalance < 0) {
                    stringResource(
                        id = R.string.channel_balance_offset,
                        channelBalance.absoluteValue / 2,
                        stringResource(id = R.string.left)
                    )
                } else {
                    stringResource(
                        id = R.string.channel_balance_offset,
                        channelBalance.absoluteValue,
                        ""
                    )
                },
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            var pendingChannelBalance = remember { channelBalance }
            Slider(
                value = channelBalance,
                onValueChange = { value ->
                    pendingChannelBalance = value
                    onChannelBalanceChanged(value.roundToInt())
                },
                onValueChangeFinished = {
                    onChannelBalanceSelected(pendingChannelBalance.roundToInt())
                },
                modifier = Modifier.padding(vertical = cardPadding),
                valueRange = channelBalanceRange,
                steps = channelBalanceStepSize,
                colors = SliderDefaults.colors(
                    activeTickColor = MaterialTheme.colorScheme.primary,
                    inactiveTickColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Text(
                text = stringResource(id = R.string.volume),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.volume_level, volumeLevelInPercent),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            var pendingVolumeLevel = remember { volumeLevel }
            Slider(
                value = volumeLevel,
                onValueChange = { value ->
                    pendingVolumeLevel = value
                    onVolumeLevelChanged(value.roundToInt())
                },
                onValueChangeFinished = {
                    onVolumeLevelSelected(pendingVolumeLevel.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding),
                valueRange = volumeRange,
                steps = volumeStepSize
            )
            Text(
                text = stringResource(id = R.string.volume_steps),
                modifier = Modifier.padding(vertical = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            val volumeSteps = listOf(
                stringResource(id = R.string.volume_steps_120),
                stringResource(id = R.string.volume_steps_60)
            )
            volumeSteps.forEachIndexed { index, step ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = index.toByte() == volumeMode.id,
                        onClick = {
                            onVolumeModeSelected(VolumeMode.findByIdOrDefault(id = index.toByte()))
                        }
                    )
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ItemAudioPreview() {
    ItemAudio()
}
