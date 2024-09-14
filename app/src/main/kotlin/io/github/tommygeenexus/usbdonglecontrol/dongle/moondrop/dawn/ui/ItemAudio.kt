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

package io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.Slider
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt

@Composable
fun ItemAudio(
    modifier: Modifier = Modifier,
    volumeLevel: Float = VolumeLevel.default().displayValueAndPayload.toFloat(),
    volumeLevelInPercent: String = VolumeLevel.default().displayValueToPercent(),
    volumeLevelStart: Float = VolumeLevel.MAX.toFloat(),
    volumeLevelEnd: Float = VolumeLevel.MIN.toFloat(),
    onVolumeLevelToPercent: (Int) -> String = { _ -> volumeLevelInPercent },
    onVolumeLevelSelected: (Int) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.volume),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(
                    id = R.string.volume_level,
                    volumeLevelInPercent
                ),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
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
                    slider.valueTo = volumeLevelEnd
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
