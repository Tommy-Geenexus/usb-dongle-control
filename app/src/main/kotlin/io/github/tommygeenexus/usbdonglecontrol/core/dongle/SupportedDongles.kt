/*
 * Copyright (c) 2022-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle

import android.hardware.usb.UsbDevice
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.E1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn35
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn44
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawnPro
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.moonriver2ti.MoondropMoonriver2Ti

val supportedDongles = listOf<UsbDongle>(
    E1da9038(),
    FiioKa13(),
    FiioKa5(),
    MoondropDawn35(),
    MoondropDawn44(),
    MoondropDawnPro(),
    MoondropMoonriver2Ti()
)

fun UsbDevice?.toUsbDongleOrNull() = supportedDongles.find { usbDongle ->
    this?.vendorId == usbDongle.vendorId && productId == usbDongle.productId
}

fun UsbDevice.isSupportedUsbDongle() = toUsbDongleOrNull() != null
