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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13

import android.content.Context
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.FiioUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.SampleRate
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.SpdifOut
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FiioKa13(
    val filter: Filter = Filter.default(),
    val firmwareVersion: FirmwareVersion = FirmwareVersion.default(),
    val indicatorState: IndicatorState = IndicatorState.default(),
    val sampleRate: SampleRate = SampleRate.default(),
    val spdifOut: SpdifOut = SpdifOut.default(),
    val volumeLevel: VolumeLevel = VolumeLevel.default()
) : FiioUsbDongle(
    modelName = MODEL_NAME,
    productId = PRODUCT_ID
),
    HardwareVolumeControl,
    FiioKa13UsbCommand {

    companion object {
        const val FIRMWARE_URL =
            "https://forum.fiio.com/note/showNoteContent.do?id=202309281720242213748&floor="
        const val MODEL_NAME = "KA13"
        const val PRODUCT_ID = 98
    }

    @IgnoredOnParcel
    override val currentVolumeLevel
        get() = volumeLevel.displayValue.toFloat()

    @IgnoredOnParcel
    override val isVolumeControlInverted
        get() = false

    @IgnoredOnParcel
    override val setFilter
        get() = listOf(
            byteArrayOf(0, 17, -96, -94, 2, 2, 1, 7, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(9, 17, -128, 96, 0, 0, 5, 9, 0, 0, 1, 0, 0, 0, 0, 0)
        )

    @IgnoredOnParcel
    override val setIndicatorState
        get() = byteArrayOf(8, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    @IgnoredOnParcel
    override val setSpdifOut
        get() = byteArrayOf(10, 82, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    @IgnoredOnParcel
    override val setVolumeLevel
        get() = listOf(
            byteArrayOf(12, 17, -96, -94, 0, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(13, 17, -96, -94, 0, 17, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(6, 17, 0, 98, 0, 0, 5, 9, 0, 2, 1, 0, 0, 0, 0, 0),
            byteArrayOf(7, 17, 0, 102, 0, 0, 5, 9, 0, 2, 1, 0, 0, 0, 0, 0),
            byteArrayOf(7, 17, 0, 98, 0, 0, 5, 9, 0, 1, 1, 0, 0, 0, 0, 0),
            byteArrayOf(8, 17, 0, 102, 0, 0, 5, 9, 0, 1, 1, 0, 0, 0, 0, 0)
        )

    @IgnoredOnParcel
    override val volumeStepSizeMin: Float
        get() = 1f

    override fun currentStateAsProfile(profileName: String) = Profile(
        name = profileName,
        vendorId = vendorId,
        productId = productId,
        filterId = filter.id,
        indicatorStateId = indicatorState.id,
        isSpdifOutEnabled = spdifOut.isEnabled,
        volumeLevel = volumeLevel.displayValue.toFloat()
    )

    override fun defaultStateAsProfile() = Profile(
        name = "",
        vendorId = vendorId,
        productId = productId,
        filterId = Filter.default().id,
        indicatorStateId = IndicatorState.default().id,
        isSpdifOutEnabled = SpdifOut.default().isEnabled,
        volumeLevel = VolumeLevel.default().displayValue.toFloat()
    )

    override fun displayVolumeLevel(context: Context): String = context.getString(
        R.string.generic_percent,
        (VolumeLevel.MIN - volumeLevel.displayValue) * 100 / VolumeLevel.MIN
    )
}
