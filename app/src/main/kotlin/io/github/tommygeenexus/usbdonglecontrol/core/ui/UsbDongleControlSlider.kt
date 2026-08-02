/*
 * Copyright (c) 2025-2026, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.core.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.Thumb
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@SuppressLint("ComposeRedundantComposable")
@Composable
fun UsbDongleControlSlider(
    steps: Int,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors()
) {
    var sliderPosition by remember { mutableFloatStateOf(value) }
    val interactionSource = remember { MutableInteractionSource() }
    Slider(
        value = sliderPosition,
        valueRange = valueRange,
        steps = steps,
        onValueChange = { sliderPosition = it },
        modifier = modifier,
        onValueChangeFinished = { onValueChangeFinished(sliderPosition) },
        colors = colors,
        interactionSource = interactionSource,
        thumb = {
            val sliderDraggedState = interactionSource.collectIsDraggedAsState()
            Label(
                label = {
                    PlainTooltip(
                        modifier = Modifier.wrapContentSize(),
                        shape = CircleShape
                    ) {
                        Text(text = sliderPosition.roundToInt().toString())
                    }
                },
                interactionSource = interactionSource,
                isPersistent = sliderDraggedState.value
            ) {
                Thumb(interactionSource = interactionSource, colors = colors)
            }
        }
    )
}

@Preview
@Composable
private fun UsbDongleControlSliderPreview() {
    UsbDongleControlSlider(
        steps = 255,
        value = -100f,
        valueRange = -127.5f..0f,
        onValueChangeFinished = {}
    )
}
