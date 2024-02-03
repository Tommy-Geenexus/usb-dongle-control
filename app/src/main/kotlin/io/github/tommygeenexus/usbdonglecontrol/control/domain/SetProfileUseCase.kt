/*
 * Copyright (c) 2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

import io.github.tommygeenexus.usbdonglecontrol.control.data.Profile
import io.github.tommygeenexus.usbdonglecontrol.dongle.UnsupportedUsbDongleException
import io.github.tommygeenexus.usbdonglecontrol.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.FiioKa5UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DacMode
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DisplayInvert
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.HardwareMute
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.HidMode
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.SpdifOut
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.VolumeLevel as VolumeLevelKa5
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.VolumeMode
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.MoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.data.MoondropDawnUsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.feature.VolumeLevel as VolumeLevelDawn
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn3544Pro.feature.createFromDisplayValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetProfileUseCase @Inject constructor(
    private val fiioKa5UsbRepository: FiioKa5UsbRepository,
    private val moondropDawnUsbRepository: MoondropDawnUsbRepository
) {

    suspend operator fun invoke(usbDongle: UsbDongle, profile: Profile): Result<UsbDongle> {
        return when (usbDongle) {
            is FiioKa5 -> {
                fiioKa5UsbRepository.setAll(
                    fiioKa5 = usbDongle,
                    channelBalance = ChannelBalance.createFromDisplayValue(
                        profile.channelBalance
                    ),
                    dacMode = DacMode.findByIdOrDefault(profile.dacModeId),
                    displayBrightness = DisplayBrightness.createFromDisplayValue(
                        displayValue = profile.displayBrightness
                    ),
                    displayInvert = DisplayInvert(isEnabled = profile.isDisplayInvertEnabled),
                    displayTimeout = DisplayTimeout.createFromDisplayValue(
                        profile.displayTimeout
                    ),
                    filter = Filter.findByIdOrDefault(id = profile.filterId),
                    gain = Gain.findByIdOrDefault(id = profile.gainId),
                    hardwareMute = HardwareMute(isEnabled = profile.isHardwareMuteEnabled),
                    hidMode = HidMode.findByIdOrDefault(id = profile.hidModeId),
                    spdifOut = SpdifOut(isEnabled = profile.isSpdifOutEnabled),
                    volumeLevel = VolumeLevelKa5.createFromDisplayValue(
                        displayValue = profile.volumeLevel,
                        volumeMode = VolumeMode.findByIdOrDefault(
                            id = profile.volumeModeId
                        )
                    ),
                    volumeMode = VolumeMode.findByIdOrDefault(id = profile.volumeModeId)
                )
            }
            is MoondropDawn -> {
                moondropDawnUsbRepository.setAll(
                    moondropDawn = usbDongle,
                    filter = Filter.findByIdOrDefault(id = profile.filterId),
                    gain = Gain.findByIdOrDefault(id = profile.gainId),
                    indicatorState = IndicatorState.findByIdOrDefault(
                        id = profile.indicatorStateId
                    ),
                    volumeLevel = VolumeLevelDawn.createFromDisplayValue(
                        displayValue = profile.volumeLevel
                    )
                )
            }
            else -> Result.failure(UnsupportedUsbDongleException())
        }
    }
}
