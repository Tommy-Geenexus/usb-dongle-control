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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DacMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.HidMode
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding

@Composable
fun ItemMisc(
    modifier: Modifier = Modifier,
    isSpdifOutEnabled: Boolean = false,
    isHardwareMuteEnabled: Boolean = false,
    dacModeId: Byte = DacMode.default().id,
    hidModeId: Byte = HidMode.default().id,
    onSpdifOutEnabledSwitched: (Boolean) -> Unit = {},
    onHardwareMuteEnabledSwitched: (Boolean) -> Unit = {},
    onDacModeSelected: (Byte) -> Unit = {},
    onHidModeSelected: (Byte) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.hid_mode),
                modifier = Modifier.padding(bottom = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            val hidModes = listOf(
                stringResource(id = R.string.hid_mode_a),
                stringResource(id = R.string.hid_mode_b)
            )
            hidModes.forEachIndexed { index, mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = index.toByte() == hidModeId,
                        onClick = {
                            onHidModeSelected(index.toByte())
                        }
                    )
                    Text(
                        text = mode,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.dac_mode),
                modifier = Modifier.padding(vertical = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            val dacModes = listOf(
                stringResource(id = R.string.dac_mode_ab),
                stringResource(id = R.string.dac_mode_h)
            )
            dacModes.forEachIndexed { index, mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = index.toByte() == dacModeId,
                        onClick = {
                            onDacModeSelected(index.toByte())
                        }
                    )
                    Text(
                        text = mode,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.spdif_out),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = isSpdifOutEnabled,
                    onCheckedChange = onSpdifOutEnabledSwitched
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.hw_mute),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = isHardwareMuteEnabled,
                    onCheckedChange = onHardwareMuteEnabledSwitched
                )
            }
        }
    }
}

@Preview
@Composable
private fun ItemMiscPreview() {
    ItemMisc()
}
