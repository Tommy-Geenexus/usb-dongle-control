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

import android.content.res.ColorStateList
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.Slider
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToDecibel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt

@Composable
fun ItemAudio(
    modifier: Modifier = Modifier,
    channelBalance: Float = ChannelBalance.default().displayValue.toFloat(),
    channelBalanceInDb: String = ChannelBalance.default().displayValueToDecibel(),
    channelBalanceDirection: String = "",
    channelBalanceStart: Float = ChannelBalance.MIN.toFloat(),
    channelBalanceEnd: Float = ChannelBalance.MAX.toFloat(),
    channelBalanceStepSize: Float = 1f,
    volumeMode: VolumeMode = VolumeMode.default(),
    volumeLevel: Float = VolumeLevel.default().displayValue.toFloat(),
    volumeLevelInPercent: String = VolumeLevel.default().displayValueToPercent(volumeMode),
    volumeLevelStart: Float = VolumeLevel.MIN.toFloat(),
    volumeLevelEndModeA: Float = VolumeLevel.MAX_A.toFloat(),
    volumeLevelEndModeB: Float = VolumeLevel.MAX_B.toFloat(),
    onChannelBalanceToDb: (Int) -> String = { channelBalanceInDb },
    onChannelBalanceSelected: (Int) -> Unit = {},
    onVolumeLevelToPercent: (Int) -> String = { volumeLevelInPercent },
    onVolumeLevelSelected: (Int) -> Unit = {},
    onVolumeModeSelected: (Byte) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.channel_balance),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(
                    id = R.string.channel_balance_offset,
                    channelBalanceInDb,
                    channelBalanceDirection
                ),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
            val surfaceContainerHighestColor =
                MaterialTheme.colorScheme.surfaceContainerHighest.toArgb()
            AndroidView(
                factory = { context ->
                    Slider(context).apply {
                        setLabelFormatter { value ->
                            onChannelBalanceToDb(value.roundToInt())
                        }
                        addOnSliderTouchListener(
                            object : Slider.OnSliderTouchListener {

                                override fun onStartTrackingTouch(slider: Slider) {
                                }

                                override fun onStopTrackingTouch(slider: Slider) {
                                    onChannelBalanceSelected(slider.value.roundToInt())
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.padding(top = cardPadding),
                update = { slider ->
                    slider.tickActiveTintList = ColorStateList.valueOf(primaryColor)
                    slider.tickInactiveTintList = ColorStateList.valueOf(primaryColor)
                    slider.trackActiveTintList = ColorStateList.valueOf(
                        surfaceContainerHighestColor
                    )
                    slider.trackInactiveTintList = ColorStateList.valueOf(
                        surfaceContainerHighestColor
                    )
                    slider.stepSize = channelBalanceStepSize
                    slider.value = channelBalance
                    slider.valueFrom = channelBalanceStart
                    slider.valueTo = channelBalanceEnd
                }
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
            if (volumeMode == VolumeMode.S120) {
                AndroidView(
                    factory = { context ->
                        Slider(context).apply {
                            setLabelFormatter { value ->
                                onVolumeLevelToPercent(value.roundToInt())
                            }
                            addOnSliderTouchListener(
                                object : Slider.OnSliderTouchListener {

                                    override fun onStartTrackingTouch(slider: Slider) {
                                    }

                                    override fun onStopTrackingTouch(slider: Slider) {
                                        onVolumeLevelSelected(slider.value.roundToInt())
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.padding(top = cardPadding),
                    update = { slider ->
                        slider.value = volumeLevel
                        slider.valueFrom = volumeLevelStart
                        slider.valueTo = volumeLevelEndModeA
                        slider.valueFrom = volumeLevelStart
                    }
                )
            } else {
                AndroidView(
                    factory = { context ->
                        Slider(context).apply {
                            setLabelFormatter { value ->
                                onVolumeLevelToPercent(value.roundToInt())
                            }
                            addOnSliderTouchListener(
                                object : Slider.OnSliderTouchListener {

                                    override fun onStartTrackingTouch(slider: Slider) {
                                    }

                                    override fun onStopTrackingTouch(slider: Slider) {
                                        onVolumeLevelSelected(slider.value.roundToInt())
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.padding(top = cardPadding),
                    update = { slider ->
                        slider.value = volumeLevel
                        slider.valueFrom = volumeLevelStart
                        slider.valueTo = volumeLevelEndModeB
                        slider.valueFrom = volumeLevelStart
                    }
                )
            }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVolumeModeSelected(index.toByte()) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = index.toByte() == volumeMode.id,
                        onClick = {
                            onVolumeModeSelected(index.toByte())
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
private fun ItemAudioPreview() {
    ItemAudio()
}
