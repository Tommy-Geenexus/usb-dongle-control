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

import android.content.res.ColorStateList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.Slider

@Composable
fun UsbDongleControlSlider(
    stepSize: Float,
    value: Float,
    valueFrom: Float,
    valueTo: Float,
    onValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    tickActiveTintList: ColorStateList? = null,
    tickInactiveTintList: ColorStateList? = null,
    trackActiveTintList: ColorStateList? = null,
    trackInactiveTintList: ColorStateList? = null
) {
    AndroidView(
        factory = { context ->
            Slider(context).apply {
                this.stepSize = stepSize
                if (tickActiveTintList != null) {
                    this.tickActiveTintList = tickActiveTintList
                }
                if (tickInactiveTintList != null) {
                    this.tickInactiveTintList = tickInactiveTintList
                }
                if (trackActiveTintList != null) {
                    this.trackActiveTintList = trackActiveTintList
                }
                if (trackInactiveTintList != null) {
                    this.trackInactiveTintList = trackInactiveTintList
                }
                setLabelFormatter { value -> value.toString() }
                addOnSliderTouchListener(
                    object : Slider.OnSliderTouchListener {

                        override fun onStartTrackingTouch(slider: Slider) {
                        }

                        override fun onStopTrackingTouch(slider: Slider) {
                            onValueChangeFinished(slider.value)
                        }
                    }
                )
            }
        },
        modifier = modifier,
        update = { slider ->
            slider.valueFrom = valueFrom
            slider.valueTo = valueTo
            slider.value = value.coerceIn(minimumValue = valueFrom, maximumValue = valueTo)
        }
    )
}

@Preview
@Composable
private fun UsbDongleControlSliderPreview() {
    UsbDongleControlSlider(
        stepSize = 0.5f,
        value = -100f,
        valueFrom = -127.5f,
        valueTo = 0f,
        onValueChangeFinished = {}
    )
}
