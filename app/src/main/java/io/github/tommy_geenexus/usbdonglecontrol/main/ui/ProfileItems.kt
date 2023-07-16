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

package io.github.tommy_geenexus.usbdonglecontrol.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwitchAccessShortcut
import androidx.compose.material.icons.outlined.SwitchAccessShortcutAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.main.data.Profile
import io.github.tommy_geenexus.usbdonglecontrol.main.data.ProfilesList

@Composable
fun ProfileItems(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    profiles: ProfilesList = ProfilesList(),
    onProfileShortcutAdd: (Profile) -> Unit = {},
    onProfileShortcutRemove: (Profile) -> Unit = {},
    onProfileDelete: (Profile) -> Unit = {},
    onProfileApply: (Profile) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        items(items = profiles.items) { profile ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .weight(weight = 1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                onProfileShortcutRemove(profile)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SwitchAccessShortcut,
                                contentDescription = null
                            )
                        }
                        FilledTonalIconButton(
                            onClick = {
                                onProfileShortcutAdd(profile)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SwitchAccessShortcutAdd,
                                contentDescription = null
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .weight(weight = 2f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .weight(weight = 2f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Button(
                            onClick = { onProfileDelete(profile) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Button(
                            onClick = {
                                onProfileApply(profile)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.apply))
                        }
                    }
                }
            }
        }
    }
}
