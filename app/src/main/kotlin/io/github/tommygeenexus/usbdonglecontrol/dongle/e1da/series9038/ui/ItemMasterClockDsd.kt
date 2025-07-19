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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.AvailableMasterClockDividers
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.MasterClockDividersDsd
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding

@Composable
fun ItemMasterClockDsd(
    modifier: Modifier = Modifier,
    availableMasterClockDividers: AvailableMasterClockDividers = AvailableMasterClockDividers(),
    currentMasterClockDividers: MasterClockDividersDsd = MasterClockDividersDsd(),
    onMasterClockDividerDsdSelected: (Byte, Int) -> Unit = { _, _ -> }
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(R.string.master_clock_divider_for_selected_dsd_sample_rate),
                modifier = Modifier.padding(bottom = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            val dsdSampleRates = listOf(
                stringResource(R.string.dsd_64),
                stringResource(R.string.dsd_128),
                stringResource(R.string.dsd_256)
            )
            var dsdSelectedIndex by remember { mutableIntStateOf(0) }
            FlowRow(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    ButtonGroupDefaults.ConnectedSpaceBetween
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                dsdSampleRates.forEachIndexed { index, label ->
                    ToggleButton(
                        checked = dsdSelectedIndex == index,
                        onCheckedChange = { dsdSelectedIndex = index },
                        shapes =
                        when (index) {
                            0 -> {
                                ButtonGroupDefaults.connectedLeadingButtonShapes()
                            }
                            dsdSampleRates.lastIndex -> {
                                ButtonGroupDefaults.connectedTrailingButtonShapes()
                            }
                            else -> {
                                ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                        },
                        modifier = Modifier.semantics { role = Role.RadioButton }
                    ) {
                        Text(label)
                    }
                }
            }
            val dsdDividerClocks = listOf(
                stringResource(R.string._50_mhz),
                stringResource(R.string._25_mhz),
                stringResource(R.string._12_5_mhz)
            )
            dsdDividerClocks.forEachIndexed { index, f ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onMasterClockDividerDsdSelected(
                                availableMasterClockDividers.items[index],
                                dsdSelectedIndex
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected =
                        currentMasterClockDividers.items[dsdSelectedIndex].first == index,
                        onClick = {
                            onMasterClockDividerDsdSelected(
                                availableMasterClockDividers.items[index],
                                dsdSelectedIndex
                            )
                        }
                    )
                    Text(
                        text = f,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ItemMasterClockDsdPreview() {
    ItemMasterClockDsd()
}
