/*
 * Copyright (c) 2022-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

import android.content.res.ColorStateList
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlBodyText
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlRadioGroup
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlSlider
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlTitleText
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ItemAudio(
    modifier: Modifier = Modifier,
    channelBalance: Float = ChannelBalance.default().displayValue.toFloat(),
    channelBalanceInDb: String = "",
    channelBalanceDirection: String = "",
    channelBalanceStart: Float = ChannelBalance.MIN.toFloat(),
    channelBalanceEnd: Float = ChannelBalance.MAX.toFloat(),
    channelBalanceStepSize: Float = ChannelBalance.STEP_SIZE,
    volumeModeId: Byte = VolumeMode.default().id,
    volumeLevel: Float = VolumeLevel.default().displayValue.toFloat(),
    volumeLevelInPercent: String = "",
    volumeLevelStart: Float = VolumeLevel.MIN.toFloat(),
    volumeLevelEndModeA: Float = VolumeLevel.MAX_A.toFloat(),
    volumeLevelEndModeB: Float = VolumeLevel.MAX_B.toFloat(),
    volumeLevelStepSize: Float = VolumeLevel.STEP_SIZE,
    onChannelBalanceSelected: (Int) -> Unit = {},
    onVolumeLevelSelected: (Float) -> Unit = {},
    onVolumeModeSelected: (Byte) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            UsbDongleControlTitleText(textRes = R.string.channel_balance)
            UsbDongleControlBodyText(
                text = stringResource(
                    id = R.string.channel_balance_offset,
                    channelBalanceInDb,
                    channelBalanceDirection
                ),
                modifier = Modifier.padding(top = cardPadding)
            )
            val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
            val surfaceContainerHighestColor =
                MaterialTheme.colorScheme.surfaceContainerHighest.toArgb()
            UsbDongleControlSlider(
                stepSize = channelBalanceStepSize,
                value = channelBalance,
                valueFrom = channelBalanceStart,
                valueTo = channelBalanceEnd,
                onValueChangeFinished = { value ->
                    onChannelBalanceSelected(value.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding),
                tickActiveTintList = ColorStateList.valueOf(primaryColor),
                tickInactiveTintList = ColorStateList.valueOf(primaryColor),
                trackActiveTintList = ColorStateList.valueOf(surfaceContainerHighestColor),
                trackInactiveTintList = ColorStateList.valueOf(surfaceContainerHighestColor)
            )
            UsbDongleControlTitleText(
                textRes = R.string.volume,
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlBodyText(
                text = stringResource(id = R.string.volume_level, volumeLevelInPercent),
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlSlider(
                stepSize = volumeLevelStepSize,
                value = volumeLevel,
                valueFrom = volumeLevelStart,
                valueTo = if (volumeModeId == VolumeMode.S120.id) {
                    volumeLevelEndModeA
                } else {
                    volumeLevelEndModeB
                },
                onValueChangeFinished = onVolumeLevelSelected,
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlTitleText(
                textRes = R.string.volume_steps,
                modifier = Modifier.padding(vertical = cardPadding)
            )
            UsbDongleControlRadioGroup(
                selectedIndex = volumeModeId.toInt(),
                items = persistentListOf(
                    stringResource(id = R.string.volume_steps_120),
                    stringResource(id = R.string.volume_steps_60)
                ),
                onItemSelected = { index ->
                    onVolumeModeSelected(index.toByte())
                }
            )
        }
    }
}

@Preview
@Composable
private fun ItemAudioPreview() {
    ItemAudio()
}
