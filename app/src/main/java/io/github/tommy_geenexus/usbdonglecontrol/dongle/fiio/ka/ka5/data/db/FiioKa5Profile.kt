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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db

import android.os.PersistableBundle
import androidx.core.os.persistableBundleOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.tommy_geenexus.usbdonglecontrol.KEY_CHANNEL_BALANCE
import io.github.tommy_geenexus.usbdonglecontrol.KEY_DAC_MODE
import io.github.tommy_geenexus.usbdonglecontrol.KEY_DISPLAY_BRIGHTNESS
import io.github.tommy_geenexus.usbdonglecontrol.KEY_DISPLAY_INVERT_ENABLED
import io.github.tommy_geenexus.usbdonglecontrol.KEY_DISPLAY_TIMEOUT
import io.github.tommy_geenexus.usbdonglecontrol.KEY_FILTER
import io.github.tommy_geenexus.usbdonglecontrol.KEY_FW_VERSION
import io.github.tommy_geenexus.usbdonglecontrol.KEY_GAIN
import io.github.tommy_geenexus.usbdonglecontrol.KEY_HID_MODE
import io.github.tommy_geenexus.usbdonglecontrol.KEY_HW_MUTE_ENABLED
import io.github.tommy_geenexus.usbdonglecontrol.KEY_ID
import io.github.tommy_geenexus.usbdonglecontrol.KEY_NAME
import io.github.tommy_geenexus.usbdonglecontrol.KEY_PRODUCT_ID
import io.github.tommy_geenexus.usbdonglecontrol.KEY_SAMPLE_RATE
import io.github.tommy_geenexus.usbdonglecontrol.KEY_SPDIF_OUT_ENABLED
import io.github.tommy_geenexus.usbdonglecontrol.KEY_VENDOR_ID
import io.github.tommy_geenexus.usbdonglecontrol.KEY_VOLUME_LEVEL
import io.github.tommy_geenexus.usbdonglecontrol.KEY_VOLUME_MODE
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.FiioUsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.DacMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.HidMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.VolumeMode
import io.github.tommy_geenexus.usbdonglecontrol.main.data.Profile
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FiioKa5Profile(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    override val name: String = "Default",
    val channelBalance: Int = FiioKa5Defaults.CHANNEL_BALANCE,
    val dacMode: DacMode = DacMode.default(),
    val displayBrightness: Int = FiioKa5Defaults.DISPLAY_BRIGHTNESS,
    val displayInvertEnabled: Boolean = false,
    val displayTimeout: Int = FiioKa5Defaults.DISPLAY_TIMEOUT,
    val filter: Filter = Filter.default(),
    val firmwareVersion: String = FiioKa5Defaults.FW_VERSION,
    val gain: Gain = Gain.default(),
    val hardwareMuteEnabled: Boolean = false,
    val hidMode: HidMode = HidMode.default(),
    val sampleRate: String = FiioKa5Defaults.SAMPLE_RATE,
    val spdifOutEnabled: Boolean = false,
    val volumeLevel: Int = FiioKa5Defaults.VOLUME_LEVEL,
    val volumeMode: VolumeMode = VolumeMode.default()
) : Profile(id, name) {

    companion object {

        fun fromPersistableBundle(bundle: PersistableBundle) = FiioKa5Profile(
            id = bundle.getLong(KEY_ID),
            name = bundle.getString(KEY_NAME).orEmpty(),
            channelBalance = bundle.getInt(KEY_CHANNEL_BALANCE),
            dacMode = DacMode.findByIdOrDefault(bundle.getInt(KEY_DAC_MODE).toByte()),
            displayBrightness = bundle.getInt(KEY_DISPLAY_BRIGHTNESS),
            displayInvertEnabled = bundle.getBoolean(KEY_DISPLAY_INVERT_ENABLED),
            displayTimeout = bundle.getInt(KEY_DISPLAY_TIMEOUT),
            filter = Filter.findByIdOrDefault(bundle.getInt(KEY_FILTER).toByte()),
            firmwareVersion = bundle.getString(KEY_FW_VERSION).orEmpty(),
            gain = Gain.findByIdOrDefault(bundle.getInt(KEY_GAIN).toByte()),
            hardwareMuteEnabled = bundle.getBoolean(KEY_HW_MUTE_ENABLED),
            hidMode = HidMode.findByIdOrDefault(bundle.getInt(KEY_HID_MODE).toByte()),
            sampleRate = bundle.getString(KEY_SAMPLE_RATE).orEmpty(),
            spdifOutEnabled = bundle.getBoolean(KEY_SPDIF_OUT_ENABLED),
            volumeLevel = bundle.getInt(KEY_VOLUME_LEVEL),
            volumeMode = VolumeMode.findByIdOrDefault(bundle.getInt(KEY_VOLUME_MODE).toByte())
        )
    }

    override fun toPersistableBundle() = persistableBundleOf(
        KEY_ID to id,
        KEY_NAME to name,
        KEY_VENDOR_ID to FiioUsbDongle.VENDOR_ID,
        KEY_PRODUCT_ID to FiioKa5.PRODUCT_ID,
        KEY_CHANNEL_BALANCE to channelBalance,
        KEY_DAC_MODE to dacMode.id.toInt(),
        KEY_DISPLAY_BRIGHTNESS to displayBrightness,
        KEY_DISPLAY_INVERT_ENABLED to displayInvertEnabled,
        KEY_DISPLAY_TIMEOUT to displayTimeout,
        KEY_FILTER to filter.id.toInt(),
        KEY_FW_VERSION to firmwareVersion,
        KEY_GAIN to gain.id.toInt(),
        KEY_HW_MUTE_ENABLED to hardwareMuteEnabled,
        KEY_HID_MODE to hidMode.id.toInt(),
        KEY_SAMPLE_RATE to sampleRate,
        KEY_SPDIF_OUT_ENABLED to spdifOutEnabled,
        KEY_VOLUME_LEVEL to volumeLevel,
        KEY_VOLUME_MODE to volumeMode.id.toInt()
    )
}
