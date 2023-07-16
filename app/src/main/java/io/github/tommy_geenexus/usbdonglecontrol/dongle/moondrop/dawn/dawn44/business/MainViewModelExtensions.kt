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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.business

import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.IndicatorState
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db.MoondropDawn44Profile
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainSideEffect
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce

fun MainViewModel.applyMoondropDawn44Profile(
    moondropDawn44: MoondropDawn44,
    moondropDawn44Profile: MoondropDawn44Profile
) = intent {
    reduce {
        state.copy(isLoading = true)
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawn44UsbCommunicationRepository.setFilter(
                connection = connection,
                filter = moondropDawn44Profile.filter
            )
            moondropDawn44UsbCommunicationRepository.setGain(
                connection = connection,
                gain = moondropDawn44Profile.gain
            )
            moondropDawn44UsbCommunicationRepository.setIndicatorState(
                connection = connection,
                indicatorState = moondropDawn44Profile.indicatorState
            )
            moondropDawn44UsbCommunicationRepository.closeConnection(connection)
            true
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = if (success) {
                moondropDawn44.copy(
                    filter = moondropDawn44Profile.filter,
                    gain = moondropDawn44Profile.gain,
                    indicatorState = moondropDawn44Profile.indicatorState
                )
            } else {
                moondropDawn44
            },
            isLoading = false
        )
    }
    postSideEffect(
        if (success) {
            MainSideEffect.Profile.Apply.Success
        } else {
            MainSideEffect.Profile.Apply.Failure
        }
    )
}

fun MainViewModel.setFilter(
    moondropDawn44: MoondropDawn44,
    filter: Filter
) = intent {
    reduce {
        state.copy(isLoading = true)
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawn44UsbCommunicationRepository
                .setFilter(connection, filter)
                .also { moondropDawn44UsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn44.copy(
                filter = if (success) filter else moondropDawn44.filter
            ),
            isLoading = false
        )
    }
}

fun MainViewModel.setGain(
    moondropDawn44: MoondropDawn44,
    gain: Gain
) = intent {
    reduce {
        state.copy(isLoading = true)
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawn44UsbCommunicationRepository
                .setGain(connection, gain)
                .also { moondropDawn44UsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn44.copy(gain = if (success) gain else moondropDawn44.gain),
            isLoading = false
        )
    }
}

fun MainViewModel.setIndicatorState(
    moondropDawn44: MoondropDawn44,
    indicatorState: IndicatorState
) = intent {
    reduce {
        state.copy(isLoading = true)
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawn44UsbCommunicationRepository
                .setIndicatorState(
                    connection = connection,
                    indicatorState = indicatorState
                )
                .also { moondropDawn44UsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn44.copy(
                indicatorState = if (success) indicatorState else moondropDawn44.indicatorState
            ),
            isLoading = false
        )
    }
}
