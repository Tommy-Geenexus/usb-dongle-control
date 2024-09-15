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

package io.github.tommygeenexus.usbdonglecontrol.core.db

import android.os.Parcelable
import android.os.PersistableBundle
import androidx.core.os.persistableBundleOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.tommygeenexus.usbdonglecontrol.core.util.TOP_LEVEL_PACKAGE_NAME
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val vendorId: Int,
    val productId: Int,
    val channelBalance: Int = 0,
    val dacModeId: Byte = 0,
    val displayBrightness: Int = 0,
    val displayTimeout: Int = 0,
    val filterId: Byte = 0,
    val firmwareVersion: String = "",
    val gainId: Byte = 0,
    val hidModeId: Byte = 0,
    val indicatorStateId: Byte = 0,
    val isDisplayInvertEnabled: Boolean = false,
    val isHardwareMuteEnabled: Boolean = false,
    val isSpdifOutEnabled: Boolean = false,
    val sampleRate: String = "",
    val volumeLevel: Int = 0,
    val volumeModeId: Byte = 0
) : Parcelable {

    companion object {

        private const val KEY_ID = TOP_LEVEL_PACKAGE_NAME + "ID"
        private const val KEY_NAME = TOP_LEVEL_PACKAGE_NAME + "NAME"
        private const val KEY_VENDOR_ID = TOP_LEVEL_PACKAGE_NAME + "VENDOR_ID"
        private const val KEY_PRODUCT_ID = TOP_LEVEL_PACKAGE_NAME + "PRODUCT_ID"
        private const val KEY_CHANNEL_BALANCE = TOP_LEVEL_PACKAGE_NAME + "CHANNEL_BALANCE"
        private const val KEY_DAC_MODE = TOP_LEVEL_PACKAGE_NAME + "DAC_MODE"
        private const val KEY_DISPLAY_BRIGHTNESS = TOP_LEVEL_PACKAGE_NAME + "DISPLAY_BRIGHTNESS"
        private const val KEY_DISPLAY_INVERT = TOP_LEVEL_PACKAGE_NAME + "DISPLAY_INVERT"
        private const val KEY_DISPLAY_TIMEOUT = TOP_LEVEL_PACKAGE_NAME + "DISPLAY_TIMEOUT"
        private const val KEY_FILTER = TOP_LEVEL_PACKAGE_NAME + "FILTER"
        private const val KEY_FW_VERSION = TOP_LEVEL_PACKAGE_NAME + "FW_VERSION"
        private const val KEY_GAIN = TOP_LEVEL_PACKAGE_NAME + "GAIN"
        private const val KEY_HW_MUTE = TOP_LEVEL_PACKAGE_NAME + "HW_MUTE"
        private const val KEY_HID_MODE = TOP_LEVEL_PACKAGE_NAME + "HID_MODE"
        private const val KEY_SAMPLE_RATE = TOP_LEVEL_PACKAGE_NAME + "SAMPLE_RATE"
        private const val KEY_SPDIF_OUT = TOP_LEVEL_PACKAGE_NAME + "SPDIF_OUT"
        private const val KEY_VOLUME_LEVEL = TOP_LEVEL_PACKAGE_NAME + "VOLUME_LEVEL"
        private const val KEY_VOLUME_MODE = TOP_LEVEL_PACKAGE_NAME + "VOLUME_MODE"
        private const val KEY_INDICATOR_STATE = TOP_LEVEL_PACKAGE_NAME + "INDICATOR_STATE"

        fun fromPersistableBundle(bundle: PersistableBundle) = Profile(
            id = bundle.getLong(KEY_ID),
            name = bundle.getString(KEY_NAME).orEmpty(),
            vendorId = bundle.getInt(KEY_VENDOR_ID),
            productId = bundle.getInt(KEY_PRODUCT_ID),
            channelBalance = bundle.getInt(KEY_CHANNEL_BALANCE),
            dacModeId = bundle.getInt(KEY_DAC_MODE).toByte(),
            displayBrightness = bundle.getInt(KEY_DISPLAY_BRIGHTNESS),
            displayTimeout = bundle.getInt(KEY_DISPLAY_TIMEOUT),
            filterId = bundle.getInt(KEY_FILTER).toByte(),
            firmwareVersion = bundle.getString(KEY_FW_VERSION).orEmpty(),
            gainId = bundle.getInt(KEY_GAIN).toByte(),
            hidModeId = bundle.getInt(KEY_HID_MODE).toByte(),
            indicatorStateId = bundle.getInt(KEY_INDICATOR_STATE).toByte(),
            isDisplayInvertEnabled = bundle.getBoolean(KEY_DISPLAY_INVERT),
            isHardwareMuteEnabled = bundle.getBoolean(KEY_HW_MUTE),
            isSpdifOutEnabled = bundle.getBoolean(KEY_SPDIF_OUT),
            sampleRate = bundle.getString(KEY_SAMPLE_RATE).orEmpty(),
            volumeLevel = bundle.getInt(KEY_VOLUME_LEVEL),
            volumeModeId = bundle.getInt(KEY_VOLUME_MODE).toByte()
        )
    }

    fun toPersistableBundle() = persistableBundleOf(
        KEY_ID to id,
        KEY_NAME to name,
        KEY_VENDOR_ID to vendorId,
        KEY_PRODUCT_ID to productId,
        KEY_CHANNEL_BALANCE to channelBalance,
        KEY_DAC_MODE to dacModeId.toInt(),
        KEY_DISPLAY_BRIGHTNESS to displayBrightness,
        KEY_DISPLAY_INVERT to isDisplayInvertEnabled,
        KEY_DISPLAY_TIMEOUT to displayTimeout,
        KEY_FILTER to filterId.toInt(),
        KEY_FW_VERSION to firmwareVersion,
        KEY_GAIN to gainId.toInt(),
        KEY_HW_MUTE to isHardwareMuteEnabled,
        KEY_HID_MODE to hidModeId.toInt(),
        KEY_INDICATOR_STATE to indicatorStateId.toInt(),
        KEY_SAMPLE_RATE to sampleRate,
        KEY_SPDIF_OUT to isSpdifOutEnabled,
        KEY_VOLUME_LEVEL to volumeLevel,
        KEY_VOLUME_MODE to volumeModeId.toInt()
    )
}
