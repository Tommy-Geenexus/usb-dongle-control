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

package io.github.tommygeenexus.usbdonglecontrol.control.domain

import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongleException
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.E1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Filter as FilterE1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.HardwareMute as HardwareMuteE1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.MasterClockDivider
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Standby
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.VolumeLevel as VolumeLevelE1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.Filter as FilterFiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.IndicatorState as IndicatorStateFiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.VolumeLevel as VolumeLevelFiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DacMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayInvert
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Filter as FilterFiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.HardwareMute
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.HidMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.SpdifOut
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeLevel as VolumeLevelFiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.VolumeMode
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.IndicatorState as IndicatorStateMoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel as VolumeLevelMoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.createFromDisplayValue
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.moonriver2ti.MoondropMoonriver2Ti
import io.github.tommygeenexus.usbdonglecontrol.dongle.e1da.series9038.data.E1da9038UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka13.data.FiioKa13UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.data.FiioKa5UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.data.MoondropDawnUsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.moonriver2ti.data.MoondropMoonriver2TiUsbRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetProfileUseCase @Inject constructor(
    private val e1da9038UsbRepository: E1da9038UsbRepository,
    private val fiioKa13UsbRepository: FiioKa13UsbRepository,
    private val fiioKa5UsbRepository: FiioKa5UsbRepository,
    private val moondropDawnUsbRepository: MoondropDawnUsbRepository,
    private val moondropMoonriver2TiUsbRepository: MoondropMoonriver2TiUsbRepository
) {

    suspend operator fun invoke(usbDongle: UsbDongle, profile: Profile): Result<UsbDongle> =
        when (usbDongle) {
            is E1da9038 -> {
                e1da9038UsbRepository.setAll(
                    e1da9038 = usbDongle,
                    filters = profile.filterIds.map { FilterE1da9038.findByIdOrDefault(it) },
                    hardwareMute = HardwareMuteE1da9038(isEnabled = profile.isHardwareMuteEnabled),
                    masterClockDividersDsd = profile.masterClockDividersDsdIds.map {
                        MasterClockDivider.findByIdOrDefault(it)
                    },
                    masterClockDividersPcm = profile.masterClockDividersPcmIds.map {
                        MasterClockDivider.findByIdOrDefault(it)
                    },
                    standby = Standby(isEnabled = profile.isStandbyEnabled),
                    volumeLevel = VolumeLevelE1da9038.createFromDisplayValue(
                        displayValue = profile.volumeLevel
                    )
                )
            }
            is FiioKa13 -> {
                fiioKa13UsbRepository.setAll(
                    fiioKa13 = usbDongle,
                    filter = FilterFiioKa13.findByIdOrDefault(id = profile.filterId),
                    indicatorState = IndicatorStateFiioKa13.findByIdOrDefault(
                        id = profile.indicatorStateId
                    ),
                    spdifOut = SpdifOut(isEnabled = profile.isSpdifOutEnabled),
                    volumeLevel = VolumeLevelFiioKa13.createFromDisplayValue(
                        displayValue = profile.volumeLevel.toInt()
                    )
                )
            }
            is FiioKa5 -> {
                fiioKa5UsbRepository.setAll(
                    fiioKa5 = usbDongle,
                    channelBalance = ChannelBalance.createFromDisplayValue(
                        displayValue = profile.channelBalance
                    ),
                    dacMode = DacMode.findByIdOrDefault(id = profile.dacModeId),
                    displayBrightness = DisplayBrightness.createFromDisplayValue(
                        displayValue = profile.displayBrightness
                    ),
                    displayInvert = DisplayInvert(isEnabled = profile.isDisplayInvertEnabled),
                    displayTimeout = DisplayTimeout.createFromDisplayValue(
                        displayValue = profile.displayTimeout
                    ),
                    filter = FilterFiioKa5.findByIdOrDefault(id = profile.filterId),
                    gain = Gain.findByIdOrDefault(id = profile.gainId),
                    hardwareMute = HardwareMute(isEnabled = profile.isHardwareMuteEnabled),
                    hidMode = HidMode.findByIdOrDefault(id = profile.hidModeId),
                    spdifOut = SpdifOut(isEnabled = profile.isSpdifOutEnabled),
                    volumeLevel = VolumeLevelFiioKa5.createFromDisplayValue(
                        displayValue = profile.volumeLevel.toInt(),
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
                    filter = FilterFiioKa5.findByIdOrDefault(id = profile.filterId),
                    gain = Gain.findByIdOrDefault(id = profile.gainId),
                    indicatorState = IndicatorStateMoondropDawn.findByIdOrDefault(
                        id = profile.indicatorStateId
                    ),
                    volumeLevel = VolumeLevelMoondropDawn.createFromDisplayValue(
                        displayValue = profile.volumeLevel.toInt()
                    )
                )
            }
            is MoondropMoonriver2Ti -> {
                moondropMoonriver2TiUsbRepository.setAll(
                    moondropMoonriver2Ti = usbDongle,
                    filter = FilterFiioKa5.findByIdOrDefault(id = profile.filterId),
                    gain = Gain.findByIdOrDefault(id = profile.gainId),
                    indicatorState = IndicatorStateMoondropDawn.findByIdOrDefault(
                        id = profile.indicatorStateId
                    ),
                    volumeLevel = VolumeLevelMoondropDawn.createFromDisplayValue(
                        displayValue = profile.volumeLevel.toInt()
                    )
                )
            }
            else -> Result.failure(UnsupportedUsbDongleException())
        }
}
