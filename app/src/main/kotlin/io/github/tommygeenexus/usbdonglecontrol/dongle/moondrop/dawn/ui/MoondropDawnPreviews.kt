/*
 * Copyright (c) 2023-2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.tommygeenexus.usbdonglecontrol.control.ui.ControlScreen
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn44

private val usbDongle = MoondropDawn44()

@Preview(name = "Loading")
@Composable
private fun ControlScreenPreview1() {
    ControlScreen(
        usbDongle = usbDongle,
        isLoading = true
    )
}

@Preview(name = "Compact")
@Composable
private fun ControlScreenPreview2() {
    ControlScreen(usbDongle = usbDongle)
}

@Preview(name = "Medium")
@Composable
private fun ControlScreenPreview3() {
    ControlScreen(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero.copy(width = 600.dp)),
        usbDongle = usbDongle
    )
}

@Preview(name = "Expanded")
@Composable
private fun ControlScreenPreview4() {
    ControlScreen(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero.copy(width = 840.dp)),
        usbDongle = usbDongle
    )
}
