/*
 * Copyright (c) 2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038

import android.content.Context
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.E1daUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.HardwareMute
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.HardwareType
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.MasterClockDivider
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.SampleRate
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Standby
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class E1da9038(
    val filters: List<Filter> = Filter.defaults(),
    val firmwareVersion: FirmwareVersion = FirmwareVersion.default(),
    val hardwareMute: HardwareMute = HardwareMute.default(),
    val hardwareType: HardwareType = HardwareType.default(),
    val masterClockDividersDsd: List<MasterClockDivider> = MasterClockDivider.dsdDefaults(),
    val masterClockDividersPcm: List<MasterClockDivider> = MasterClockDivider.pcmDefaults(),
    val sampleRate: SampleRate = SampleRate.default(),
    val standby: Standby = Standby.default(),
    val volumeLevel: VolumeLevel = VolumeLevel.default()
) : E1daUsbDongle(
    modelName = MODEL_NAME,
    productId = PRODUCT_ID
),
    HardwareVolumeControl,
    E1da9038UsbCommand {

    companion object {
        const val FIRMWARE_URL =
            "https://drive.google.com/file/d/1q_2D_6x0CSiB-wDffqTxGvr8Do1johkH/view"
        const val MODEL_NAME = "#9038"
        const val PRODUCT_ID = 24595
    }

    @IgnoredOnParcel
    override val currentVolumeLevel
        get() = volumeLevel.displayValue

    @IgnoredOnParcel
    override val isVolumeControlInverted
        get() = false

    @IgnoredOnParcel
    override val getAudioFormat: ByteArray
        get() = byteArrayOf(8, 0, 0)

    @IgnoredOnParcel
    override val getFilterDsd: ByteArray
        get() = byteArrayOf(4, 44, 4)

    @IgnoredOnParcel
    override val getFilterPcm44To96: ByteArray
        get() = byteArrayOf(4, 36, 4)

    @IgnoredOnParcel
    override val getFilterPcm176To384: ByteArray
        get() = byteArrayOf(4, 40, 4)

    @IgnoredOnParcel
    override val getHwTypeVersionMuteStandby: ByteArray
        get() = byteArrayOf(4, 0, 4)

    @IgnoredOnParcel
    override val getMasterClockDividerDsd: ByteArray
        get() = byteArrayOf(4, 32, 4)

    @IgnoredOnParcel
    override val getMasterClockDividerPcm44To96: ByteArray
        get() = byteArrayOf(4, 24, 4)

    @IgnoredOnParcel
    override val getMasterClockDividerPcm176To384: ByteArray
        get() = byteArrayOf(4, 28, 4)

    @IgnoredOnParcel
    override val getThdRanges: ByteArray
        get() = byteArrayOf(4, 8, 4)

    @IgnoredOnParcel
    override val getThdRange1: ByteArray
        get() = byteArrayOf(4, 12, 4)

    @IgnoredOnParcel
    override val getThdRange2: ByteArray
        get() = byteArrayOf(4, 16, 4)

    @IgnoredOnParcel
    override val getThdRange3: ByteArray
        get() = byteArrayOf(4, 20, 4)

    @IgnoredOnParcel
    override val getVolumeLeftRightMinMax: ByteArray
        get() = byteArrayOf(4, 4, 4)

    @IgnoredOnParcel
    override val setFilterDsd: ByteArray
        get() = byteArrayOf(3, 44, 4)

    @IgnoredOnParcel
    override val setFilterPcm44To96: ByteArray
        get() = byteArrayOf(3, 36, 4)

    @IgnoredOnParcel
    override val setFilterPcm176To384: ByteArray
        get() = byteArrayOf(3, 40, 4)

    @IgnoredOnParcel
    override val setHardwareMute: ByteArray
        get() = byteArrayOf(3, 2, 1)

    @IgnoredOnParcel
    override val setInit: ByteArray
        get() = byteArrayOf(5, 0, 0)

    @IgnoredOnParcel
    override val setMasterClockDividerDsd: ByteArray
        get() = byteArrayOf(3, 32, 4)

    @IgnoredOnParcel
    override val setMasterClockDividerPcm44To96: ByteArray
        get() = byteArrayOf(3, 24, 4)

    @IgnoredOnParcel
    override val setMasterClockDividerPcm176To384: ByteArray
        get() = byteArrayOf(3, 28, 4)

    @IgnoredOnParcel
    override val setRestore: ByteArray
        get() = byteArrayOf(9, 0, 0)

    @IgnoredOnParcel
    override val setStandby: ByteArray
        get() = byteArrayOf(3, 3, 1)

    @IgnoredOnParcel
    override val setVolumeLevel: ByteArray
        get() = byteArrayOf(6, 0, 2)

    @IgnoredOnParcel
    override val setVolumeLevelMin: ByteArray
        get() = byteArrayOf(3, 7, 1)

    @IgnoredOnParcel
    override val setVolumeLevelMax: ByteArray
        get() = byteArrayOf(3, 6, 1)

    @IgnoredOnParcel
    override val volumeStepSizeMin: Float
        get() = 0.5f

    override fun currentStateAsProfile(profileName: String) = Profile(
        name = profileName,
        vendorId = vendorId,
        productId = productId,
        filterIds = filters.map { it.id },
        isHardwareMuteEnabled = hardwareMute.isEnabled,
        isStandbyEnabled = standby.isEnabled,
        masterClockDividersDsdIds = masterClockDividersDsd.map { it.id },
        masterClockDividersPcmIds = masterClockDividersPcm.map { it.id },
        volumeLevel = volumeLevel.displayValue
    )

    override fun defaultStateAsProfile() = Profile(
        name = "",
        vendorId = vendorId,
        productId = productId,
        filterIds = Filter.defaults().map { it.id },
        isHardwareMuteEnabled = HardwareMute.default().isEnabled,
        isStandbyEnabled = Standby.default().isEnabled,
        masterClockDividersDsdIds = MasterClockDivider.dsdDefaults().map { it.id },
        masterClockDividersPcmIds = MasterClockDivider.pcmDefaults().map { it.id },
        volumeLevel = VolumeLevel.default().displayValue
    )

    override fun displayVolumeLevel(context: Context): String =
        context.getString(R.string.volume_db, volumeLevel.displayValue.toString())
}
