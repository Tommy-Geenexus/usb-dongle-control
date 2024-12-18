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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.productName
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassCompact
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassExpanded
import io.github.tommygeenexus.usbdonglecontrol.core.util.windowWidthSizeClassMedium

@Composable
fun ControlTopAppBar(
    windowSizeClass: WindowSizeClass,
    windowInsets: WindowInsets,
    productName: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onRefresh: () -> Unit = {},
    onReset: () -> Unit = {},
    onProfileExport: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = productName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        actions = {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                var showMore by remember { mutableStateOf(false) }
                ControlDropdownMenu(
                    windowSizeClass = windowSizeClass,
                    isExpanded = showMore,
                    onDismissRequest = { showMore = false },
                    onRefresh = onRefresh,
                    onReset = onReset,
                    onProfileExport = onProfileExport,
                    onNavigateToSettings = onNavigateToSettings
                )
                IconButton(onClick = { showMore = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(id = R.string.more)
                    )
                }
            }
        },
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior
    )
}

@Preview(name = "Compact")
@Composable
private fun ControlTopAppBarPreview1() {
    ControlTopAppBar(
        windowSizeClass = windowWidthSizeClassCompact,
        windowInsets = WindowInsets.statusBars,
        productName = FiioKa5().productName()
    )
}

@Preview(name = "Medium")
@Composable
private fun ControlTopAppBarPreview2() {
    ControlTopAppBar(
        windowSizeClass = windowWidthSizeClassMedium,
        windowInsets = WindowInsets.statusBars,
        productName = FiioKa5().productName()
    )
}

@Preview(name = "Expanded")
@Composable
private fun ControlTopAppBarPreview3() {
    ControlTopAppBar(
        windowSizeClass = windowWidthSizeClassExpanded,
        windowInsets = WindowInsets.statusBars,
        productName = FiioKa5().productName()
    )
}
