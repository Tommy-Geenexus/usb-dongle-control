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

package io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding

@Composable
fun ItemFilter(
    modifier: Modifier = Modifier,
    filterId: Byte = Filter.default().id,
    onFilterSelected: (Byte) -> Unit = {}
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            Text(
                text = stringResource(id = R.string.filter),
                modifier = Modifier.padding(bottom = cardPadding),
                style = MaterialTheme.typography.titleMedium
            )
            val filterNames = listOf(
                stringResource(id = R.string.filter_fast_roll_off_low_latency),
                stringResource(id = R.string.filter_fast_roll_off_phase_compensated),
                stringResource(id = R.string.filter_slow_roll_off_low_latency),
                stringResource(id = R.string.filter_slow_roll_off_phase_compensated),
                stringResource(id = R.string.filter_non_over_sampling)
            )
            filterNames.forEachIndexed { index, filterName ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = index.toByte() == filterId,
                        onClick = {
                            onFilterSelected(index.toByte())
                        }
                    )
                    Text(
                        text = filterName,
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
