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

package io.github.tommy_geenexus.usbdonglecontrol.main.data

import android.content.Intent
import android.os.Parcelable
import android.os.PersistableBundle
import androidx.core.content.IntentCompat
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_ACTION_SHORTCUT_PROFILE
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_EXTRA_CONSUMED
import io.github.tommy_geenexus.usbdonglecontrol.KEY_PRODUCT_ID
import io.github.tommy_geenexus.usbdonglecontrol.KEY_VENDOR_ID
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.FiioUsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.MoondropUsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db.MoondropDawn44Profile

abstract class Profile(
    open val id: Long,
    open val name: String
) : Parcelable {

    companion object {

        fun fromPersistableBundle(bundle: PersistableBundle): Profile? {
            val vendorId = bundle.getLong(KEY_VENDOR_ID)
            val productId = bundle.getLong(KEY_PRODUCT_ID)
            return when (vendorId) {
                FiioUsbDongle.VENDOR_ID -> {
                    if (productId == FiioKa5.PRODUCT_ID) {
                        FiioKa5Profile.fromPersistableBundle(bundle)
                    } else {
                        null
                    }
                }
                MoondropUsbDongle.VENDOR_ID -> {
                    if (productId == MoondropDawn44.PRODUCT_ID) {
                        MoondropDawn44Profile.fromPersistableBundle(bundle)
                    } else {
                        null
                    }
                }
                else -> {
                    null
                }
            }
        }
    }

    abstract fun toPersistableBundle(): PersistableBundle
}

fun Intent.consumeProfileShortcut(): Profile? {
    if (hasExtra(INTENT_EXTRA_CONSUMED)) {
        return null
    }
    val bundle = IntentCompat.getParcelableExtra(
        this,
        INTENT_ACTION_SHORTCUT_PROFILE,
        PersistableBundle::class.java
    )
    return if (bundle != null) {
        putExtra(INTENT_EXTRA_CONSUMED, true)
        Profile.fromPersistableBundle(bundle)
    } else {
        null
    }
}
