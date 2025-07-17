/*
 * Copyright (c) 2023-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

import android.content.Context
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.MoondropUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import kotlinx.parcelize.IgnoredOnParcel

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

    @IgnoredOnParcel
    override val currentVolumeLevel
        get() = volumeLevel.displayValue.toFloat()

    @IgnoredOnParcel
    override val isVolumeControlInverted
        get() = true

    @IgnoredOnParcel
    override val getAny
        get() = byteArrayOf(-64, -91, -93)

    @IgnoredOnParcel
    override val getVolumeLevel
        get() = byteArrayOf(-64, -91, -94)

    @IgnoredOnParcel
    override val setFilter
        get() = byteArrayOf(-64, -91, 1)

    @IgnoredOnParcel
    override val setGain
        get() = byteArrayOf(-64, -91, 2)

    @IgnoredOnParcel
    override val setIndicatorState
        get() = byteArrayOf(-64, -91, 6)

    @IgnoredOnParcel
    override val setVolumeLevel
        get() = byteArrayOf(-64, -91, 4)

    @IgnoredOnParcel
    override val volumeStepSizeMin: Float
        get() = 1f

    override fun currentStateAsProfile(profileName: String) = Profile(
        name = profileName,
        vendorId = vendorId,
        productId = productId,
        filterId = filter.id,
        gainId = gain.id,
        indicatorStateId = indicatorState.id,
        volumeLevel = volumeLevel.displayValue.toFloat()
    )

    override fun defaultStateAsProfile() = Profile(
        name = "",
        vendorId = vendorId,
        productId = productId,
        filterId = Filter.default().id,
        gainId = Gain.default().id,
        indicatorStateId = IndicatorState.default().id,
        volumeLevel = VolumeLevel.default().displayValue.toFloat()
    )

    override fun displayVolumeLevel(context: Context): String = context.getString(
        R.string.generic_percent,
        ((VolumeLevel.MIN - volumeLevel.displayValue) * 100) / VolumeLevel.MIN
    )
}
