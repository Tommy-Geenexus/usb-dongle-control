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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.MoondropDawnDefaults
import io.github.tommy_geenexus.usbdonglecontrol.theme.cardPadding
import kotlin.math.roundToInt

@Composable
fun ItemAudio(
    modifier: Modifier = Modifier,
    volumeLevel: Float = MoondropDawnDefaults.VOLUME_LEVEL.toFloat(),
    volumeRange: ClosedFloatingPointRange<Float> =
        MoondropDawnDefaults.VOLUME_LEVEL_MAX.toFloat()..MoondropDawnDefaults.VOLUME_LEVEL_MIN.toFloat(),
    onVolumeLevelChanged: (Int) -> Unit = {},
    onVolumeLevelSelected: (Int) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.volume),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.volume_level, volumeLevel.roundToInt()),
                modifier = Modifier.padding(top = cardPadding),
                style = MaterialTheme.typography.bodyMedium
            )
            var pendingVolumeLevel by remember { mutableFloatStateOf(volumeLevel) }
            Slider(
                value = volumeLevel,
                onValueChange = { value ->
                    pendingVolumeLevel = value
                    onVolumeLevelChanged(value.roundToInt())
                },
                onValueChangeFinished = {
                    onVolumeLevelSelected(pendingVolumeLevel.roundToInt())
                },
                modifier = Modifier.padding(top = cardPadding),
                valueRange = volumeRange
            )
        }
    }
}

@Preview
@Composable
fun ItemAudioPreview() {
    ItemAudio()
}
