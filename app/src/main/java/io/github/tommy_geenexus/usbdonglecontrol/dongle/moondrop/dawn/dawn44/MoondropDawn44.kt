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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44

import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.MoondropUsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.IndicatorState
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.MoondropDawn44UsbCommand
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoondropDawn44(
    val filter: Filter = Filter.default(),
    val gain: Gain = Gain.default(),
    val indicatorState: IndicatorState = IndicatorState.default()
) : MoondropUsbDongle(modelName = "Nano audio 1"),
    MoondropDawn44UsbCommand {

    @IgnoredOnParcel
    override val setFilter = byteArrayOf(-64, -91, 1)

    @IgnoredOnParcel
    override val setGain = byteArrayOf(-64, -91, 2)

    @IgnoredOnParcel
    override val setIndicatorState = byteArrayOf(-64, -91, 6)

    @IgnoredOnParcel
    override val getAny = byteArrayOf(-64, -91, -93)
}
