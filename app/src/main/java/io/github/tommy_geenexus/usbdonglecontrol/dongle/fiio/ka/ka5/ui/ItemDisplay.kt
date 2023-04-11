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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
import kotlin.math.roundToInt

@Composable
fun ItemDisplay(
    modifier: Modifier = Modifier,
    displayBrightness: Float = FiioKa5Defaults.DISPLAY_BRIGHTNESS.toFloat(),
    displayBrightnessStepSize: Int = FiioKa5Defaults.DISPLAY_BRIGHTNESS_MAX,
    displayBrightnessRange: ClosedFloatingPointRange<Float> =
        FiioKa5Defaults.DISPLAY_BRIGHTNESS_MIN.toFloat()..FiioKa5Defaults.DISPLAY_BRIGHTNESS_MAX.toFloat(),
    displayTimeout: Float = FiioKa5Defaults.DISPLAY_TIMEOUT.toFloat(),
    displayTimeoutStepSize: Int = FiioKa5Defaults.DISPLAY_TIMEOUT_MAX,
    displayTimeoutRange: ClosedFloatingPointRange<Float> =
        FiioKa5Defaults.DISPLAY_TIMEOUT_MIN.toFloat()..FiioKa5Defaults.DISPLAY_TIMEOUT_MAX.toFloat(),
    displayInvertEnabled: Boolean = false,
    onDisplayBrightnessSelected: (Float) -> Unit = {},
    onDisplayTimeoutSelected: (Float) -> Unit = {},
    onDisplayInvertChange: (Boolean) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.display_brightness),
                modifier = Modifier.padding(all = 16.dp),
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
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = displayBrightness,
                onValueChange = onDisplayBrightnessSelected,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                ),
                valueRange = displayBrightnessRange,
                steps = displayBrightnessStepSize
            )
            Text(
                text = stringResource(id = R.string.display_timeout),
                modifier = Modifier.padding(all = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.display_timeout_delay, displayTimeout),
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = displayTimeout,
                onValueChange = onDisplayTimeoutSelected,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                ),
                valueRange = displayTimeoutRange,
                steps = displayTimeoutStepSize
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.display_invert),
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = displayInvertEnabled,
                    onCheckedChange = onDisplayInvertChange,
                    modifier = Modifier.padding(end = 16.dp)
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
