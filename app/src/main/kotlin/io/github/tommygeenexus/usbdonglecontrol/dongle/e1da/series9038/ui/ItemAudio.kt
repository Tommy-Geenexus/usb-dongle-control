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

package io.github.tommygeenexus.usbdonglecontrol.dongle.e1da.series9038.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.default
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding

@Composable
fun ItemAudio(
    modifier: Modifier = Modifier,
    volumeLevel: Float = VolumeLevel.default().displayValue,
    volumeLevelMin: Float = VolumeLevel.MIN_DB.toFloat(),
    volumeLevelMax: Float = VolumeLevel.MAX_DB.toFloat(),
    volumeLevelStart: Float = VolumeLevel.MIN_DB.toFloat(),
    volumeLevelEnd: Float = VolumeLevel.MAX_DB.toFloat(),
    volumeLevelStepSize: Float = VolumeLevel.STEP_SIZE,
    onVolumeLevelSelected: (Float) -> Unit = {},
    onVolumeLevelMaxSelected: (Float) -> Unit = { _ -> },
    onVolumeLevelMinSelected: (Float) -> Unit = { _ -> }
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.volume),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.volume_level_db, volumeLevel),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            AndroidView(
                factory = { context ->
                    Slider(context).apply {
                        setLabelFormatter { value -> value.toString() }
                        addOnSliderTouchListener(
                            object : Slider.OnSliderTouchListener {

                                override fun onStartTrackingTouch(slider: Slider) {
                                }

                                override fun onStopTrackingTouch(slider: Slider) {
                                    onVolumeLevelSelected(slider.value)
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.padding(top = cardPadding),
                update = { slider ->
                    slider.stepSize = volumeLevelStepSize
                    slider.value = volumeLevel
                    slider.valueFrom = volumeLevelStart
                    slider.valueTo = volumeLevelEnd
                }
            )
            Text(
                text = stringResource(id = R.string.volume_range),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(
                    id = R.string.volume_range_min_max,
                    volumeLevelMin,
                    volumeLevelMax
                ),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            val volumeLevelMinMax by rememberUpdatedState((listOf(volumeLevelMin, volumeLevelMax)))
            AndroidView(
                factory = { context ->
                    RangeSlider(context).apply {
                        setLabelFormatter { value ->
                            value.toString()
                        }
                        addOnSliderTouchListener(
                            object : RangeSlider.OnSliderTouchListener {

                                override fun onStartTrackingTouch(slider: RangeSlider) {
                                }

                                override fun onStopTrackingTouch(slider: RangeSlider) {
                                    if (slider.values.first() != volumeLevelMinMax.first()) {
                                        onVolumeLevelMinSelected(slider.values.first())
                                    } else if (slider.values.last() != volumeLevelMinMax.last()) {
                                        onVolumeLevelMaxSelected(slider.values.last())
                                    }
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.padding(top = cardPadding),
                update = { slider ->
                    slider.stepSize = volumeLevelStepSize
                    slider.valueFrom = volumeLevelStart
                    slider.valueTo = volumeLevelEnd
                    slider.values = volumeLevelMinMax
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
