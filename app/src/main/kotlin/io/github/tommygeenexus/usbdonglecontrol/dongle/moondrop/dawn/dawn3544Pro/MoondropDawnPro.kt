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

package io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro

import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.feature.default
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoondropDawnPro(
    override val filter: Filter = Filter.default(),
    override val gain: Gain = Gain.default(),
    override val indicatorState: IndicatorState = IndicatorState.default(),
    override val volumeLevel: VolumeLevel = VolumeLevel.default()
) : MoondropDawn(
    modelName = "Dawn Pro",
    productId = PRODUCT_ID,
    filter = filter,
    gain = gain,
    indicatorState = indicatorState,
    volumeLevel = volumeLevel
) {

    companion object {

        const val PRODUCT_ID = 61546
    }
}
