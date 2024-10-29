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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.Slider
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToSeconds
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt

@Composable
fun ItemDisplay(
    modifier: Modifier = Modifier,
    displayBrightness: Float = DisplayBrightness.default().displayValue.toFloat(),
    displayBrightnessInPercent: String = DisplayBrightness.default().displayValueToPercent(),
    displayBrightnessStart: Float = DisplayBrightness.MIN.toFloat(),
    displayBrightnessEnd: Float = DisplayBrightness.MAX.toFloat(),
    displayBrightnessStepSize: Float = 1f,
    displayTimeout: Float = DisplayTimeout.default().displayValue.toFloat(),
    displayTimeoutInSeconds: String = DisplayTimeout.default().displayValueToSeconds(),
    displayTimeoutStart: Float = DisplayTimeout.MIN.toFloat(),
    displayTimeoutEnd: Float = DisplayTimeout.MAX.toFloat(),
    isDisplayInvertEnabled: Boolean = false,
    onDisplayBrightnessToPercent: (Int) -> String = { _ -> displayBrightnessInPercent },
    onDisplayBrightnessSelected: (Int) -> Unit = {},
    onDisplayTimeoutToSeconds: (Int) -> String = { _ -> displayTimeoutInSeconds },
    onDisplayTimeoutSelected: (Int) -> Unit = {},
    onDisplayInvertSwitched: (Boolean) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.display_brightness),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(
                    id = R.string.display_brightness_level,
                    displayBrightnessInPercent
                ),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            AndroidView(
                factory = { context ->
                    Slider(context).apply {
                        setLabelFormatter { value ->
                            onDisplayBrightnessToPercent(value.roundToInt())
                        }
                        addOnSliderTouchListener(
                            object : Slider.OnSliderTouchListener {

                                override fun onStartTrackingTouch(slider: Slider) {
                                }

                                override fun onStopTrackingTouch(slider: Slider) {
                                    onDisplayBrightnessSelected(slider.value.roundToInt())
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.padding(top = cardPadding),
                update = { slider ->
                    slider.stepSize = displayBrightnessStepSize
                    slider.value = displayBrightness
                    slider.valueFrom = displayBrightnessStart
                    slider.valueTo = displayBrightnessEnd
                }
            )
            Text(
                text = stringResource(id = R.string.display_timeout),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.display_timeout_delay, displayTimeout),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            AndroidView(
                factory = { context ->
                    Slider(context).apply {
                        setLabelFormatter { value ->
                            onDisplayTimeoutToSeconds(value.roundToInt())
                        }
                        addOnSliderTouchListener(
                            object : Slider.OnSliderTouchListener {

                                override fun onStartTrackingTouch(slider: Slider) {
                                }

                                override fun onStopTrackingTouch(slider: Slider) {
                                    onDisplayTimeoutSelected(slider.value.roundToInt())
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.padding(top = cardPadding),
                update = { slider ->
                    slider.value = displayTimeout
                    slider.valueFrom = displayTimeoutStart
                    slider.valueTo = displayTimeoutEnd
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = cardPadding)
                    .clickable { onDisplayInvertSwitched(!isDisplayInvertEnabled) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.display_invert),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = isDisplayInvertEnabled,
                    onCheckedChange = onDisplayInvertSwitched
                )
            }
        }
    }
}

@Preview
@Composable
private fun ItemDisplayPreview() {
    ItemDisplay()
}
