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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.business

import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.data.IndicatorState
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.data.db.MoondropDawnProfile
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainSideEffect
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainViewModel
import io.github.tommy_geenexus.usbdonglecontrol.main.business.minusLoadingTask
import io.github.tommy_geenexus.usbdonglecontrol.main.business.plusLoadingTask
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce

fun MainViewModel.applyMoondropDawnProfile(
    moondropDawn: MoondropDawn44,
    moondropDawnProfile: MoondropDawnProfile = MoondropDawnProfile()
) = intent {
    reduce {
        state.copy(loadingTasks = state.plusLoadingTask())
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawnUsbCommunicationRepository.setFilter(
                connection = connection,
                filter = moondropDawnProfile.filter
            )
            moondropDawnUsbCommunicationRepository.setGain(
                connection = connection,
                gain = moondropDawnProfile.gain
            )
            moondropDawnUsbCommunicationRepository.setIndicatorState(
                connection = connection,
                indicatorState = moondropDawnProfile.indicatorState
            )
            moondropDawnUsbCommunicationRepository.setVolumeLevel(
                connection = connection,
                volumeLevel = moondropDawnProfile.volumeLevel
            )
            moondropDawnUsbCommunicationRepository.closeConnection(connection)
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
                moondropDawn.copy(
                    filter = moondropDawnProfile.filter,
                    gain = moondropDawnProfile.gain,
                    indicatorState = moondropDawnProfile.indicatorState
                )
            } else {
                moondropDawn
            },
            loadingTasks = state.minusLoadingTask()
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
    moondropDawn: MoondropDawn44,
    filter: Filter
) = intent {
    reduce {
        state.copy(loadingTasks = state.plusLoadingTask())
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawnUsbCommunicationRepository
                .setFilter(connection, filter)
                .also { moondropDawnUsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn.copy(
                filter = if (success) filter else moondropDawn.filter
            ),
            loadingTasks = state.minusLoadingTask()
        )
    }
    postSideEffect(
        if (success) {
            MainSideEffect.UsbCommunication.Success
        } else {
            MainSideEffect.UsbCommunication.Failure
        }
    )
}

fun MainViewModel.setGain(
    moondropDawn: MoondropDawn44,
    gain: Gain
) = intent {
    reduce {
        state.copy(loadingTasks = state.plusLoadingTask())
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawnUsbCommunicationRepository
                .setGain(connection, gain)
                .also { moondropDawnUsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn.copy(gain = if (success) gain else moondropDawn.gain),
            loadingTasks = state.minusLoadingTask()
        )
    }
    postSideEffect(
        if (success) {
            MainSideEffect.UsbCommunication.Success
        } else {
            MainSideEffect.UsbCommunication.Failure
        }
    )
}

fun MainViewModel.setIndicatorState(
    moondropDawn: MoondropDawn44,
    indicatorState: IndicatorState
) = intent {
    reduce {
        state.copy(loadingTasks = state.plusLoadingTask())
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawnUsbCommunicationRepository
                .setIndicatorState(
                    connection = connection,
                    indicatorState = indicatorState
                )
                .also { moondropDawnUsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn.copy(
                indicatorState = if (success) indicatorState else moondropDawn.indicatorState
            ),
            loadingTasks = state.minusLoadingTask()
        )
    }
    postSideEffect(
        if (success) {
            MainSideEffect.UsbCommunication.Success
        } else {
            MainSideEffect.UsbCommunication.Failure
        }
    )
}

fun MainViewModel.updateVolumeLevel(
    moondropDawn: MoondropDawn44,
    volumeLevel: Int
) = blockingIntent {
    reduce {
        state.copy(usbDongle = moondropDawn.copy(volumeLevel = volumeLevel))
    }
}

fun MainViewModel.setVolumeLevel(
    moondropDawn: MoondropDawn44,
    volumeLevel: Int
) = intent {
    reduce {
        state.copy(loadingTasks = state.plusLoadingTask())
    }
    val device = usbRepository.getAttachedDeviceOrNull()
    val success = if (device != null) {
        val connection = usbRepository.openDeviceOrNull(device)
        if (connection != null) {
            moondropDawnUsbCommunicationRepository
                .setVolumeLevel(
                    connection = connection,
                    volumeLevel = volumeLevel
                )
                .also { moondropDawnUsbCommunicationRepository.closeConnection(connection) }
        } else {
            false
        }
    } else {
        false
    }
    reduce {
        state.copy(
            usbDongle = moondropDawn.copy(
                volumeLevel = if (success) volumeLevel else moondropDawn.volumeLevel
            ),
            loadingTasks = state.minusLoadingTask()
        )
    }
    postSideEffect(
        if (success) {
            MainSideEffect.UsbCommunication.Success
        } else {
            MainSideEffect.UsbCommunication.Failure
        }
    )
}
