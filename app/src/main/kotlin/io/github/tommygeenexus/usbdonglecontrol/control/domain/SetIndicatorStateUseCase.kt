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

import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongleException
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.IndicatorState as IndicatorStateFiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.IndicatorState as IndicatorStateMoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.moonriver2ti.MoondropMoonriver2Ti
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka13.data.FiioKa13UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.data.MoondropDawnUsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.moonriver2ti.data.MoondropMoonriver2TiUsbRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetIndicatorStateUseCase @Inject constructor(
    private val fiioKa13UsbRepository: FiioKa13UsbRepository,
    private val moondropDawnUsbRepository: MoondropDawnUsbRepository,
    private val moondropMoonriver2TiUsbRepository: MoondropMoonriver2TiUsbRepository
) {

    suspend operator fun invoke(usbDongle: UsbDongle, id: Byte): Result<UsbDongle> =
        when (usbDongle) {
            is FiioKa13 -> {
                fiioKa13UsbRepository.setIndicatorState(
                    fiioKa13 = usbDongle,
                    indicatorState = IndicatorStateFiioKa13.findByIdOrDefault(id)
                )
            }
            is MoondropDawn -> {
                moondropDawnUsbRepository.setIndicatorState(
                    moondropDawn = usbDongle,
                    indicatorState = IndicatorStateMoondropDawn.findByIdOrDefault(id)
                )
            }
            is MoondropMoonriver2Ti -> {
                moondropMoonriver2TiUsbRepository.setIndicatorState(
                    moondropMoonriver2Ti = usbDongle,
                    indicatorState = IndicatorStateMoondropDawn.findByIdOrDefault(id)
                )
            }
            else -> Result.failure(UnsupportedUsbDongleException())
        }
}
