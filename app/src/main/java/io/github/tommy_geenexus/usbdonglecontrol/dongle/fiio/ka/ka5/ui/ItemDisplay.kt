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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
import io.github.tommy_geenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt

@Composable
fun ItemDisplay(
    modifier: Modifier = Modifier,
    displayBrightness: Float = FiioKa5Defaults.DISPLAY_BRIGHTNESS.toFloat(),
    displayBrightnessStepSize: Int = FiioKa5Defaults.DISPLAY_BRIGHTNESS_MAX,
    displayBrightnessRange: ClosedFloatingPointRange<Float> =
        FiioKa5Defaults.DISPLAY_BRIGHTNESS_MIN.toFloat()..FiioKa5Defaults.DISPLAY_BRIGHTNESS_MAX.toFloat(),
    displayTimeout: Float = FiioKa5Defaults.DISPLAY_TIMEOUT.toFloat(),
    displayTimeoutRange: ClosedFloatingPointRange<Float> =
        FiioKa5Defaults.DISPLAY_TIMEOUT_MIN.toFloat()..FiioKa5Defaults.DISPLAY_TIMEOUT_MAX.toFloat(),
    displayInvertEnabled: Boolean = false,
    onDisplayBrightnessChanged: (Int) -> Unit = {},
    onDisplayBrightnessSelected: (Int) -> Unit = {},
    onDisplayTimeoutChanged: (Int) -> Unit = {},
    onDisplayTimeoutSelected: (Int) -> Unit = {},
    onDisplayInvertChange: (Boolean) -> Unit = {}
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
                    "${(
                        displayBrightness * 100 /
                            displayBrightnessStepSize
                        ).toDouble().roundToInt()}%"
                ),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            var pendingDisplayBrightness by remember { mutableFloatStateOf(displayBrightness) }
            Slider(
                value = displayBrightness,
                onValueChange = { value ->
                    pendingDisplayBrightness = value
                    onDisplayBrightnessChanged(value.roundToInt())
                },
                onValueChangeFinished = {
                    onDisplayBrightnessSelected(pendingDisplayBrightness.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding),
                valueRange = displayBrightnessRange,
                steps = displayBrightnessStepSize
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
            var pendingDisplayTimeout by remember { mutableFloatStateOf(displayTimeout) }
            Slider(
                value = displayTimeout,
                onValueChange = { value ->
                    pendingDisplayTimeout = value
                    onDisplayTimeoutChanged(value.roundToInt())
                },
                onValueChangeFinished = {
                    onDisplayTimeoutSelected(pendingDisplayTimeout.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding),
                valueRange = displayTimeoutRange
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.display_invert),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = displayInvertEnabled,
                    onCheckedChange = onDisplayInvertChange
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemDisplayPreview() {
    ItemDisplay()
}
