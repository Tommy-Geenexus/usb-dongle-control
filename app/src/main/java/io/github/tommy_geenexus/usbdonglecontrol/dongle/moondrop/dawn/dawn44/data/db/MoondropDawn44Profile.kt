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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db

import android.os.PersistableBundle
import androidx.core.os.persistableBundleOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.tommy_geenexus.usbdonglecontrol.KEY_FILTER
import io.github.tommy_geenexus.usbdonglecontrol.KEY_GAIN
import io.github.tommy_geenexus.usbdonglecontrol.KEY_ID
import io.github.tommy_geenexus.usbdonglecontrol.KEY_INDICATOR_STATE
import io.github.tommy_geenexus.usbdonglecontrol.KEY_NAME
import io.github.tommy_geenexus.usbdonglecontrol.KEY_PRODUCT_ID
import io.github.tommy_geenexus.usbdonglecontrol.KEY_VENDOR_ID
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.MoondropUsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.IndicatorState
import io.github.tommy_geenexus.usbdonglecontrol.main.data.Profile
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class MoondropDawn44Profile(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    override val name: String = "Default",
    val filter: Filter = Filter.default(),
    val gain: Gain = Gain.default(),
    val indicatorState: IndicatorState = IndicatorState.default()
) : Profile(id, name) {

    companion object {

        fun fromPersistableBundle(bundle: PersistableBundle) = MoondropDawn44Profile(
            id = bundle.getLong(KEY_ID),
            name = bundle.getString(KEY_NAME).orEmpty(),
            filter = Filter.findByIdOrDefault(bundle.getInt(KEY_FILTER).toByte()),
            gain = Gain.findByIdOrDefault(bundle.getInt(KEY_GAIN).toByte()),
            indicatorState = IndicatorState.findByIdOrDefault(
                bundle.getInt(KEY_INDICATOR_STATE).toByte()
            )
        )
    }

    override fun toPersistableBundle() = persistableBundleOf(
        KEY_ID to id,
        KEY_NAME to name,
        KEY_VENDOR_ID to MoondropUsbDongle.VENDOR_ID,
        KEY_PRODUCT_ID to MoondropDawn44.PRODUCT_ID,
        KEY_FILTER to filter.id.toInt(),
        KEY_GAIN to gain.id.toInt(),
        KEY_INDICATOR_STATE to indicatorState.id.toInt()
    )
}
