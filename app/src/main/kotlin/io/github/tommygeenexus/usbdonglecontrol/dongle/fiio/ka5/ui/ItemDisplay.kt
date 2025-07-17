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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlBodyText
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlSlider
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlSwitchRow
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlTitleText
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt

@Composable
fun ItemDisplay(
    modifier: Modifier = Modifier,
    displayBrightness: Float = DisplayBrightness.default().displayValue.toFloat(),
    displayBrightnessStart: Float = DisplayBrightness.MIN.toFloat(),
    displayBrightnessEnd: Float = DisplayBrightness.MAX.toFloat(),
    displayBrightnessStepSize: Float = DisplayBrightness.STEP_SIZE,
    displayTimeout: Float = DisplayTimeout.default().displayValue.toFloat(),
    displayTimeoutStart: Float = DisplayTimeout.MIN.toFloat(),
    displayTimeoutEnd: Float = DisplayTimeout.MAX.toFloat(),
    displayTimeoutStepSize: Float = DisplayTimeout.STEP_SIZE,
    isDisplayInvertEnabled: Boolean = false,
    onDisplayBrightnessSelected: (Int) -> Unit = {},
    onDisplayTimeoutSelected: (Int) -> Unit = {},
    onDisplayInvertSwitched: (Boolean) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            UsbDongleControlTitleText(textRes = R.string.display_brightness)
            UsbDongleControlBodyText(
                text = stringResource(id = R.string.display_brightness_level, displayBrightness),
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlSlider(
                stepSize = displayBrightnessStepSize,
                value = displayBrightness,
                valueFrom = displayBrightnessStart,
                valueTo = displayBrightnessEnd,
                onValueChangeFinished = { value ->
                    onDisplayBrightnessSelected(value.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlTitleText(
                textRes = R.string.display_timeout,
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlBodyText(
                text = stringResource(id = R.string.display_timeout_delay, displayTimeout),
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlSlider(
                stepSize = displayTimeoutStepSize,
                value = displayTimeout,
                valueFrom = displayTimeoutStart,
                valueTo = displayTimeoutEnd,
                onValueChangeFinished = { value ->
                    onDisplayTimeoutSelected(value.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding)
            )
            UsbDongleControlSwitchRow(
                textRes = R.string.display_invert,
                isChecked = isDisplayInvertEnabled,
                modifier = Modifier.padding(top = cardPadding),
                onCheckedChange = onDisplayInvertSwitched
            )
        }
    }
}

@Preview
@Composable
private fun ItemDisplayPreview() {
    ItemDisplay()
}
