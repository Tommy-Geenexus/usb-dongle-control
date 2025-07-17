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

package io.github.tommygeenexus.usbdonglecontrol.control.domain

import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.E1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.VolumeLevel as VolumeLevelE1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.VolumeLevel as VolumeLevelFiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeLevel as VolumeLevelFiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn35
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn44
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel as VolumeLevelMoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.moonriver2ti.MoondropMoonriver2Ti
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class SynchronizeVolumeLevelUseCase @Inject constructor() {

    operator fun invoke(usbDongle: UsbDongle, volumeLevel: Float): UsbDongle = when (usbDongle) {
        is E1da9038 -> {
            usbDongle.copy(
                volumeLevel = VolumeLevelE1da9038.createFromDisplayValue(
                    displayValue = volumeLevel
                )
            )
        }
        is FiioKa13 -> {
            usbDongle.copy(
                volumeLevel = VolumeLevelFiioKa13.createFromDisplayValue(
                    displayValue = volumeLevel.roundToInt()
                )
            )
        }
        is FiioKa5 -> {
            usbDongle.copy(
                volumeLevel = VolumeLevelFiioKa5.createFromDisplayValue(
                    displayValue = volumeLevel.roundToInt(),
                    volumeMode = usbDongle.volumeMode
                )
            )
        }
        is MoondropDawn35 -> {
            usbDongle.copy(
                volumeLevel = VolumeLevelMoondropDawn.createFromDisplayValue(
                    displayValue = volumeLevel.roundToInt()
                )
            )
        }
        is MoondropDawn44 -> {
            usbDongle.copy(
                volumeLevel = VolumeLevelMoondropDawn.createFromDisplayValue(
                    displayValue = volumeLevel.roundToInt()
                )
            )
        }
        is MoondropMoonriver2Ti -> {
            usbDongle.copy(
                volumeLevel = VolumeLevelMoondropDawn.createFromDisplayValue(
                    displayValue = volumeLevel.roundToInt()
                )
            )
        }
        else -> usbDongle
    }
}
