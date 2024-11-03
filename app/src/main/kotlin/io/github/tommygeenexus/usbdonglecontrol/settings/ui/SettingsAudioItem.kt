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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.github.tommygeenexus.usbdonglecontrol.R

@Composable
fun SettingsAudioItem(
    modifier: Modifier = Modifier,
    isMaximizeVolumeEnabled: Boolean = false,
    onMaximizeVolumeSwitched: (Boolean) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.audio),
            modifier = Modifier.padding(start = 56.dp, top = 24.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onMaximizeVolumeSwitched(!isMaximizeVolumeEnabled)
                }
        ) {
            val (icon, title, subtitle, checkbox) = createRefs()
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                contentDescription = stringResource(id = R.string.maximize_volume),
                modifier = Modifier.constrainAs(icon) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(anchor = parent.start, margin = 16.dp)
                    top.linkTo(parent.top)
                }
            )
            Text(
                text = stringResource(R.string.maximize_volume),
                modifier = Modifier.constrainAs(title) {
                    bottom.linkTo(subtitle.top)
                    end.linkTo(checkbox.start, margin = 16.dp)
                    start.linkTo(icon.end, margin = 16.dp)
                    top.linkTo(parent.top, margin = 16.dp)
                    horizontalBias = 0f
                    width = Dimension.preferredWrapContent
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(id = R.string.maximize_volume_description),
                modifier = Modifier.constrainAs(subtitle) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(checkbox.start, margin = 16.dp)
                    start.linkTo(icon.end, margin = 16.dp)
                    top.linkTo(title.bottom)
                    horizontalBias = 0f
                    width = Dimension.preferredWrapContent
                },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Checkbox(
                checked = isMaximizeVolumeEnabled,
                onCheckedChange = onMaximizeVolumeSwitched,
                modifier = Modifier.constrainAs(checkbox) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(anchor = parent.end, margin = 16.dp)
                    top.linkTo(parent.top)
                    horizontalBias = 1f
                }
            )
        }
    }
}

@Preview
@Composable
private fun SettingsAudioItemPreview() {
    SettingsAudioItem()
}
