/*
 * Copyright (c) 2024-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassCompact
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassExpanded

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
    FlexibleBottomAppBar(
        modifier = modifier,
        horizontalArrangement = if (windowSizeClass.widthSizeClass ==
            WindowWidthSizeClass.Compact
        ) {
            BottomAppBarDefaults.FlexibleHorizontalArrangement
        } else {
            BottomAppBarDefaults.FlexibleFixedHorizontalArrangement
        },
        scrollBehavior = scrollBehavior,
        content = {
            val exportProfile = stringResource(id = R.string.profile_export)
            val factoryReset = stringResource(id = R.string.state_reset)
            val refresh = stringResource(id = R.string.refresh)
            val settings = stringResource(id = R.string.settings)
            var showExportProfile by remember { mutableStateOf(false) }
            if (showExportProfile) {
                ExportProfileDialog(
                    onDismiss = { showExportProfile = false },
                    onConfirm = { profileName ->
                        showExportProfile = false
                        onProfileExport(profileName)
                    }
                )
            }
            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = { PlainTooltip { Text(text = factoryReset) } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = { onReset() }) {
                    Icon(
                        imageVector = Icons.Outlined.SettingsBackupRestore,
                        contentDescription = factoryReset
                    )
                }
            }
            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = { PlainTooltip { Text(text = exportProfile) } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = { showExportProfile = true }) {
                    Icon(
                        imageVector = Icons.Outlined.ImportExport,
                        contentDescription = exportProfile
                    )
                }
            }
            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = { PlainTooltip { Text(text = settings) } },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = settings
                    )
                }
            }
            TooltipBox(
                modifier = Modifier,
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = { PlainTooltip { Text(text = refresh) } },
                state = rememberTooltipState()
            ) {
                FilledIconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(48.dp),
                    shape = IconButtonDefaults.smallSquareShape
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = refresh
                    )
                }
            }
        }
    )
}

@Preview(name = "Compact")
@Composable
private fun ControlBottomAppBarPreview1() {
    ControlBottomAppBar(windowSizeClass = windowWidthSizeClassCompact)
}

@Preview(name = "Expanded")
@Composable
private fun ControlBottomAppBarPreview2() {
    ControlBottomAppBar(windowSizeClass = windowWidthSizeClassExpanded)
}
