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
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.AvailableFilters
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Filters
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding

@Composable
fun ItemFilter(
    modifier: Modifier = Modifier,
    availableFilters: AvailableFilters = AvailableFilters(),
    currentFilters: Filters = Filters(),
    onFilterSelected: (Byte, Int) -> Unit = { _, _ -> }
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(R.string.filter_for_selected_pcm_sample_rate),
                modifier = Modifier.padding(bottom = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            val sampleRates = listOf(
                stringResource(R.string._44_1_khz),
                stringResource(R.string._48_khz),
                stringResource(R.string._88_2_khz),
                stringResource(R.string._96_khz),
                stringResource(R.string._176_4_khz),
                stringResource(R.string._192_khz),
                stringResource(R.string._352_8_khz),
                stringResource(R.string._384_khz)
            )
            var selectedIndex by remember { mutableIntStateOf(0) }
            FlowRow(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    ButtonGroupDefaults.ConnectedSpaceBetween
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                sampleRates.forEachIndexed { index, label ->
                    ToggleButton(
                        checked = selectedIndex == index,
                        onCheckedChange = { selectedIndex = index },
                        shapes =
                        when (index) {
                            0 -> {
                                ButtonGroupDefaults.connectedLeadingButtonShapes()
                            }
                            sampleRates.lastIndex -> {
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
            val filters = listOf(
                stringResource(id = R.string.linear_phase_fast),
                stringResource(id = R.string.linear_phase_slow),
                stringResource(id = R.string.min_phase_fast),
                stringResource(id = R.string.min_phase_slow),
                stringResource(id = R.string.apodizing_fast),
                stringResource(id = R.string.corrected_min_phase),
                stringResource(id = R.string.brick_wall)
            )
            filters.forEachIndexed { index, f ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFilterSelected(
                                availableFilters.items[index],
                                selectedIndex
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentFilters.items[selectedIndex].first == index,
                        onClick = {
                            onFilterSelected(
                                availableFilters.items[index],
                                selectedIndex
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
private fun ItemFilterPreview() {
    ItemFilter()
}
