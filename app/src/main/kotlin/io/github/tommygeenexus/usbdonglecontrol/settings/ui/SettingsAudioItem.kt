/*
 * Copyright (c) 2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.theme.getHorizontalCardPadding
import io.github.tommygeenexus.usbdonglecontrol.theme.getHorizontalPadding

@Composable
fun SettingsAudioItem(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero),
    isMaximizeVolumeEnabled: Boolean = false,
    onMaximizeVolumeRequested: (Boolean) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.audio),
            modifier = Modifier.padding(
                start = 56.dp,
                top = 24.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onMaximizeVolumeRequested(!isMaximizeVolumeEnabled)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                contentDescription = stringResource(id = R.string.maximize_volume),
                modifier = Modifier.padding(start = windowSizeClass.getHorizontalPadding())
            )
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text = stringResource(id = R.string.maximize_volume),
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(id = R.string.maximize_volume_description),
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 4.dp,
                        bottom = 24.dp
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = isMaximizeVolumeEnabled,
                onCheckedChange = onMaximizeVolumeRequested,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = windowSizeClass.getHorizontalCardPadding()
                )
            )
        }
    }
}

@Preview
@Composable
private fun SettingsAudioItemPreview() {
    SettingsAudioItem()
}
