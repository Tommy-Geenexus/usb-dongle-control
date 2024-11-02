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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn

import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.MoondropUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl

sealed class MoondropDawn(
    override val modelName: String,
    override val productId: Int,
    open val filter: Filter,
    open val gain: Gain,
    open val indicatorState: IndicatorState,
    open val volumeLevel: VolumeLevel
) : MoondropUsbDongle(modelName, productId),
    HardwareVolumeControl,
    MoondropDawnUsbCommand {

    override val getAny
        get() = byteArrayOf(-64, -91, -93)

    override val getVolumeLevel
        get() = byteArrayOf(-64, -91, -94)

    override val setFilter
        get() = byteArrayOf(-64, -91, 1)

    override val setGain
        get() = byteArrayOf(-64, -91, 2)

    override val setIndicatorState
        get() = byteArrayOf(-64, -91, 6)

    override val setVolumeLevel
        get() = byteArrayOf(-64, -91, 4)

    override val isVolumeControlAsc: Boolean
        get() = false

    override val currentVolumeLevel: Int
        get() = volumeLevel.displayValueAndPayload

    override val displayVolumeLevel: String
        get() = volumeLevel.displayValueToPercent()

    override fun currentStateAsProfile(profileName: String) = Profile(
        name = profileName,
        vendorId = vendorId,
        productId = productId,
        filterId = filter.id,
        gainId = gain.id,
        indicatorStateId = indicatorState.id,
        volumeLevel = volumeLevel.displayValueAndPayload
    )

    override fun defaultStateAsProfile() = Profile(
        name = "",
        vendorId = vendorId,
        productId = productId,
        filterId = Filter.default().id,
        gainId = Gain.default().id,
        indicatorStateId = IndicatorState.default().id,
        volumeLevel = VolumeLevel.default().displayValueAndPayload
    )
}
