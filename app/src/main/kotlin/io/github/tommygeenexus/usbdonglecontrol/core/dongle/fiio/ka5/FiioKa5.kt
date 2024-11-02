/*
 * Copyright (c) 2022-2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5

import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.FiioUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DacMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayInvert
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.HardwareMute
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.HidMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.SampleRate
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.SpdifOut
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.displayValueToPercent
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FiioKa5(
    val channelBalance: ChannelBalance = ChannelBalance.default(),
    val dacMode: DacMode = DacMode.ClassAB,
    val displayBrightness: DisplayBrightness = DisplayBrightness.default(),
    val displayInvert: DisplayInvert = DisplayInvert.default(),
    val displayTimeout: DisplayTimeout = DisplayTimeout.default(),
    val filter: Filter = Filter.default(),
    val firmwareVersion: FirmwareVersion = FirmwareVersion.default(),
    val gain: Gain = Gain.default(),
    val hardwareMute: HardwareMute = HardwareMute.default(),
    val hidMode: HidMode = HidMode.default(),
    val sampleRate: SampleRate = SampleRate.default(),
    val spdifOut: SpdifOut = SpdifOut.default(),
    val volumeLevel: VolumeLevel = VolumeLevel.default(),
    val volumeMode: VolumeMode = VolumeMode.default()
) : FiioUsbDongle(
    modelName = MODEL_NAME,
    productId = PRODUCT_ID
),
    HardwareVolumeControl,
    FiioKa5UsbCommand {

    companion object {
        const val FIRMWARE_URL =
            "https://forum.fiio.com/note/showNoteContent.do?id=202304141747228623151"
        const val MODEL_NAME = "KA5"
        const val PRODUCT_ID = 85
    }

    @IgnoredOnParcel
    override val getFilter
        get() = byteArrayOf(-57, -91, -93)

    @IgnoredOnParcel
    override val getOtherState
        get() = byteArrayOf(-57, -91, -92)

    @IgnoredOnParcel
    override val getSampleRate
        get() = byteArrayOf(-57, -91, -95)

    @IgnoredOnParcel
    override val getVersion
        get() = byteArrayOf(-57, -91, -96)

    @IgnoredOnParcel
    override val getVolumeLevel
        get() = byteArrayOf(-57, -91, -94)

    @IgnoredOnParcel
    override val setChannelBalance
        get() = byteArrayOf(-57, -91, 5)

    @IgnoredOnParcel
    override val setDacMode
        get() = byteArrayOf(-57, -91, 6)

    @IgnoredOnParcel
    override val setDisplayBrightness
        get() = byteArrayOf(-57, -91, 11)

    @IgnoredOnParcel
    override val setDisplayInvert
        get() = byteArrayOf(-57, -91, 12)

    @IgnoredOnParcel
    override val setDisplayTimeout
        get() = byteArrayOf(-57, -91, 9)

    @IgnoredOnParcel
    override val setFilter
        get() = byteArrayOf(-57, -91, 1)

    @IgnoredOnParcel
    override val setGain
        get() = byteArrayOf(-57, -91, 2)

    @IgnoredOnParcel
    override val setHardwareMute
        get() = byteArrayOf(-57, -91, 7)

    @IgnoredOnParcel
    override val setHidMode
        get() = byteArrayOf(-57, -91, 10)

    @IgnoredOnParcel
    override val setSpdifOut
        get() = byteArrayOf(-57, -91, 8)

    @IgnoredOnParcel
    override val setVolumeLevel
        get() = byteArrayOf(-57, -91, 4)

    @IgnoredOnParcel
    override val setVolumeMode
        get() = byteArrayOf(-57, -91, 13)

    @IgnoredOnParcel
    override val isVolumeControlAsc
        get() = true

    override val currentVolumeLevel
        get() = volumeLevel.displayValue

    override val displayVolumeLevel: String
        get() = volumeLevel.displayValueToPercent(volumeMode)

    override fun currentStateAsProfile(profileName: String) = Profile(
        name = profileName,
        vendorId = vendorId,
        productId = productId,
        channelBalance = channelBalance.displayValue,
        dacModeId = dacMode.id,
        displayBrightness = displayBrightness.displayValue,
        displayTimeout = displayTimeout.displayValue,
        filterId = filter.id,
        firmwareVersion = firmwareVersion.displayValue,
        gainId = gain.id,
        hidModeId = hidMode.id,
        isDisplayInvertEnabled = displayInvert.isEnabled,
        isHardwareMuteEnabled = hardwareMute.isEnabled,
        isSpdifOutEnabled = spdifOut.isEnabled,
        sampleRate = sampleRate.displayValue,
        volumeLevel = volumeLevel.displayValue,
        volumeModeId = volumeMode.id
    )

    override fun defaultStateAsProfile() = Profile(
        name = "",
        vendorId = vendorId,
        productId = productId,
        channelBalance = ChannelBalance.default().displayValue,
        dacModeId = DacMode.default().id,
        displayBrightness = DisplayBrightness.default().displayValue,
        displayTimeout = DisplayTimeout.default().displayValue,
        filterId = Filter.default().id,
        firmwareVersion = FirmwareVersion.default().displayValue,
        gainId = Gain.default().id,
        hidModeId = HidMode.default().id,
        isDisplayInvertEnabled = DisplayInvert.default().isEnabled,
        isHardwareMuteEnabled = HardwareMute.default().isEnabled,
        isSpdifOutEnabled = SpdifOut.default().isEnabled,
        sampleRate = SampleRate.default().displayValue,
        volumeLevel = VolumeLevel.default().displayValue,
        volumeModeId = VolumeMode.default().id
    )
}
