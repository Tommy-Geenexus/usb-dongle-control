/*
 * Copyright (c) 2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.RangeSlider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun UsbDongleControlRangeSlider(
    stepSize: Float,
    values: ImmutableList<Float>,
    valueFrom: Float,
    valueTo: Float,
    onFirstValueChangeFinished: (Float) -> Unit,
    onLastValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val updatedValues by rememberUpdatedState(values)
    AndroidView(
        factory = { context ->
            RangeSlider(context).apply {
                setLabelFormatter { value -> value.toString() }
                addOnSliderTouchListener(
                    object : RangeSlider.OnSliderTouchListener {

                        override fun onStartTrackingTouch(slider: RangeSlider) {
                        }

                        override fun onStopTrackingTouch(slider: RangeSlider) {
                            if (slider.values.first() != updatedValues.first()) {
                                onFirstValueChangeFinished(slider.values.first())
                            } else if (slider.values.last() != updatedValues.last()) {
                                onLastValueChangeFinished(slider.values.last())
                            }
                        }
                    }
                )
            }
        },
        modifier = modifier,
        update = { slider ->
            slider.stepSize = stepSize
            slider.valueFrom = valueFrom
            slider.valueTo = valueTo
            slider.values = values
        }
    )
}

@Preview
@Composable
private fun UsbDongleControlRangeSliderPreview() {
    UsbDongleControlRangeSlider(
        stepSize = 0.5f,
        values = persistentListOf(-100f, -10f),
        valueFrom = -127.5f,
        valueTo = 0f,
        onFirstValueChangeFinished = {},
        onLastValueChangeFinished = {}
    )
}
