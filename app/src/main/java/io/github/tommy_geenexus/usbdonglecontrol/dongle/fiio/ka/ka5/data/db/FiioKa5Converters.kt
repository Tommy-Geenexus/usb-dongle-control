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

import androidx.room.TypeConverter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.DacMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.HidMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.VolumeMode

class FiioKa5Converters {

    @TypeConverter
    fun fromDacModeId(id: Byte?): DacMode {
        return DacMode.findById(id ?: DacMode.default().id) ?: DacMode.default()
    }

    @TypeConverter
    fun toDacModeId(dacMode: DacMode) = dacMode.id

    @TypeConverter
    fun fromFilterId(id: Byte?): Filter {
        return Filter.findById(id ?: Filter.default().id) ?: Filter.default()
    }

    @TypeConverter
    fun toFilterId(filter: Filter) = filter.id

    @TypeConverter
    fun fromGainId(id: Byte?): Gain {
        return Gain.findById(id ?: Gain.default().id) ?: Gain.default()
    }

    @TypeConverter
    fun toGainId(gain: Gain) = gain.id

    @TypeConverter
    fun fromHidModeId(id: Byte?): HidMode {
        return HidMode.findById(id ?: HidMode.default().id) ?: HidMode.default()
    }

    @TypeConverter
    fun toHidModeId(hidMode: HidMode) = hidMode.id

    @TypeConverter
    fun fromVolumeModeId(id: Byte?): VolumeMode {
        return VolumeMode.findById(id ?: VolumeMode.default().id) ?: VolumeMode.default()
    }

    @TypeConverter
    fun toVolumeModeId(volumeMode: VolumeMode) = volumeMode.id
}
