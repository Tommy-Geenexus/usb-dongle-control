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

package io.github.tommygeenexus.usbdonglecontrol.control.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassCompact

@Composable
fun ControlBottomAppBar(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
    onRefresh: () -> Unit = {},
    onReset: () -> Unit = {},
    onProfileExport: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val collapsedFraction by animateFloatAsState(
        targetValue = if (scrollBehavior != null) {
            1 - scrollBehavior.state.collapsedFraction
        } else {
            1f
        },
        label = "ControlBottomAppBarAlphaAnimation"
    )
    BottomAppBar(
        actions = {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.alpha(collapsedFraction)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
            var showMore by remember { mutableStateOf(false) }
            IconButton(
                onClick = { showMore = true },
                modifier = Modifier.alpha(collapsedFraction)
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(id = R.string.more)
                )
            }
            ControlDropdownMenu(
                windowSizeClass = windowSizeClass,
                isExpanded = showMore,
                onDismissRequest = { showMore = false },
                onRefresh = onRefresh,
                onReset = onReset,
                onProfileExport = onProfileExport,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRefresh,
                modifier = Modifier.alpha(collapsedFraction),
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(id = R.string.refresh)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Preview
@Composable
private fun ControlBottomAppBarPreview() {
    ControlBottomAppBar(windowSizeClass = windowWidthSizeClassCompact)
}
