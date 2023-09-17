/*
 * Copyright (c) 2023, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.main.ui.MainScreen

@Preview(name = "No device")
@Composable
fun MainScreenPreviewNoDevice() {
    MainScreen()
}

@Preview(name = "Moondrop Dawn 4.4mm no permission")
@Composable
fun MainScreenPreviewMoondropDawn44NoPermission() {
    MainScreen(
        usbDongle = MoondropDawn44(),
        isDeviceAttached = true
    )
}

@Preview(name = "Moondrop Dawn 4.4mm loading")
@Composable
fun MainScreenPreviewMoondropDawn44Loading() {
    MainScreen(
        usbDongle = MoondropDawn44(),
        isLoading = true,
        isDeviceAttached = true,
        isUsbPermissionGranted = true
    )
}

@Preview(name = "Moondrop Dawn 4.4mm compact")
@Composable
fun MainScreenPreviewMoondropDawn44Compact() {
    MainScreen(
        usbDongle = MoondropDawn44(),
        isDeviceAttached = true,
        isUsbPermissionGranted = true
    )
}

@Preview(name = "Moondrop Dawn 4.4mm medium")
@Composable
fun MainScreenPreviewMoondropDawn44Medium() {
    MainScreen(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero.copy(width = 600.dp)),
        usbDongle = MoondropDawn44(),
        isDeviceAttached = true,
        isUsbPermissionGranted = true
    )
}

@Preview(name = "Moondrop Dawn 4.4mm expanded")
@Composable
fun MainScreenPreviewMoondropDawn44Expanded() {
    MainScreen(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero.copy(width = 840.dp)),
        usbDongle = MoondropDawn44(),
        isDeviceAttached = true,
        isUsbPermissionGranted = true
    )
}
