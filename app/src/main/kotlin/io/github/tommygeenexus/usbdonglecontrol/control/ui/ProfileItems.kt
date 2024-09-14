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

package io.github.tommygeenexus.usbdonglecontrol.control.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SwitchAccessShortcut
import androidx.compose.material.icons.outlined.SwitchAccessShortcutAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.profileFlow
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPaddingBetween
import io.github.tommygeenexus.usbdonglecontrol.theme.cardSizeMinDp
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProfileItems(
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    profiles: LazyPagingItems<Profile> =
        UnsupportedUsbDongle.profileFlow().collectAsLazyPagingItems(),
    onProfileShortcutAdd: (Profile) -> Unit = {},
    onProfileShortcutRemove: (Profile) -> Unit = {},
    onProfileDelete: (Profile) -> Unit = {},
    onProfileApply: (Profile) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = cardSizeMinDp),
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(all = cardPaddingBetween),
        verticalItemSpacing = cardPaddingBetween,
        horizontalArrangement = Arrangement.spacedBy(cardPaddingBetween)
    ) {
        items(
            count = profiles.itemCount,
            key = profiles.itemKey { profile ->
                profile.id
            }
        ) { index ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                val profile = profiles[index] ?: return@ElevatedCard
                Row(
                    modifier = Modifier.padding(all = cardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(weight = 1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        FilledTonalIconButton(onClick = { onProfileShortcutRemove(profile) }) {
                            Icon(
                                imageVector = Icons.Outlined.SwitchAccessShortcut,
                                contentDescription = stringResource(id = R.string.shortcut_add)
                            )
                        }
                        FilledTonalIconButton(onClick = { onProfileShortcutAdd(profile) }) {
                            Icon(
                                imageVector = Icons.Outlined.SwitchAccessShortcutAdd,
                                contentDescription = stringResource(id = R.string.shortcut_delete)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
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
                            .weight(weight = 2f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Button(
                            onClick = {
                                onProfileDelete(profile)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                        Button(onClick = { onProfileApply(profile) }) {
                            Text(
                                text = stringResource(id = R.string.apply),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        when (profiles.loadState.prepend) {
            is LoadState.NotLoading -> {
                // Ignore
            }
            LoadState.Loading -> item {
                Spinner(modifier = Modifier.fillMaxSize())
            }
            is LoadState.Error -> item {
                Refresh {
                    profiles.refresh()
                }
            }
        }
        when (profiles.loadState.append) {
            is LoadState.NotLoading -> {
                // Ignore
            }
            LoadState.Loading -> item {
                Spinner(modifier = Modifier.fillMaxSize())
            }
            is LoadState.Error -> item {
                Refresh {
                    profiles.refresh()
                }
            }
        }
        when (profiles.loadState.refresh) {
            is LoadState.NotLoading -> {
                // Ignore
            }
            LoadState.Loading -> item {
                Spinner(modifier = Modifier.fillMaxSize())
            }
            is LoadState.Error -> item {
                Refresh {
                    profiles.refresh()
                }
            }
        }
    }
}

@Composable
fun Refresh(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    onRefresh: () -> Unit
) {
    Box(
        contentAlignment = alignment,
        modifier = modifier
    ) {
        IconButton(onClick = { onRefresh() }) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = stringResource(id = R.string.refresh)
            )
        }
    }
}

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    progressModifier: Modifier = Modifier.padding(all = cardPadding),
    alignment: Alignment = Alignment.Center
) {
    Box(
        contentAlignment = alignment,
        modifier = modifier
    ) {
        CircularProgressIndicator(modifier = progressModifier)
    }
}

@Preview
@Composable
private fun ProfileItemsPreview() {
    val profiles = mutableListOf<Profile>().apply {
        for (i in 0L..16) {
            add(Profile(id = i, name = "Profile $i", vendorId = 0, productId = 0))
        }
    }
    ProfileItems(profiles = flowOf(PagingData.from(profiles)).collectAsLazyPagingItems())
}
