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

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassCompact
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassExpanded
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassMedium

@Composable
fun ControlDropdownMenu(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onReset: () -> Unit = {},
    onProfileExport: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
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
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.refresh),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onDismissRequest()
                    onRefresh()
                }
            )
        }
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.state_reset),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = {
                onDismissRequest()
                onReset()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.profile_export),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = {
                onDismissRequest()
                showExportProfile = true
            }
        )
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onDismissRequest()
                    onNavigateToSettings()
                }
            )
        }
    }
}

@Preview(name = "Compact")
@Composable
private fun ControlDropdownMenuPreview1() {
    ControlDropdownMenu(
        windowSizeClass = windowWidthSizeClassCompact,
        isExpanded = true
    )
}

@Preview(name = "Medium")
@Composable
private fun ControlDropdownMenuPreview2() {
    ControlDropdownMenu(
        windowSizeClass = windowWidthSizeClassMedium,
        isExpanded = true
    )
}

@Preview(name = "Expanded")
@Composable
private fun ControlDropdownMenuPreview3() {
    ControlDropdownMenu(
        windowSizeClass = windowWidthSizeClassExpanded,
        isExpanded = true
    )
}
