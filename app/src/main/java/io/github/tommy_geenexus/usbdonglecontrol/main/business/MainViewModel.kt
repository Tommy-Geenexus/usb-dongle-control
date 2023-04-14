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

package io.github.tommy_geenexus.usbdonglecontrol.main.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.FiioKa5UsbCommunicationRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.MoondropDawn44UsbCommunicationRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.toUsbDongleOrNull
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val usbRepository: UsbRepository,
    val fiioKa5UsbCommunicationRepository: FiioKa5UsbCommunicationRepository,
    val moondropDawn44UsbCommunicationRepository: MoondropDawn44UsbCommunicationRepository
) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    override val container = container<MainState, MainSideEffect>(
        initialState = MainState(),
        savedStateHandle = savedStateHandle,
        onCreate = { getCurrentState() }
    )

    fun getCurrentState() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        val usbDongle = if (state.usbPermissionGranted) {
            if (device != null) {
                val connection = usbRepository.openDeviceOrNull(device)
                if (connection != null) {
                    when (device.toUsbDongleOrNull()) {
                        is FiioKa5 -> {
                            fiioKa5UsbCommunicationRepository.getCurrentState(connection)
                        }
                        is MoondropDawn44 -> {
                            moondropDawn44UsbCommunicationRepository.getCurrentState(connection)
                        }
                        else -> null
                    }
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
        reduce {
            state.copy(
                usbDongle = usbDongle,
                isLoading = false
            )
        }
        postSideEffect(
            if (device != null && !state.usbPermissionGranted) {
                MainSideEffect.RequestPermissions
            } else {
                MainSideEffect.NotificationService.Start
            }
        )
    }

    fun handleAttachedDevicesChanged() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val usbDongle = usbRepository.getAttachedDeviceOrNull()?.toUsbDongleOrNull()
        reduce {
            state.copy(
                usbDongle = usbDongle,
                isLoading = false,
                usbPermissionGranted = false
            )
        }
        postSideEffect(
            if (usbDongle != null) {
                MainSideEffect.RequestPermissions
            } else {
                MainSideEffect.NotificationService.Stop
            }
        )
    }

    fun handleUsbPermissionGranted() = intent {
        reduce {
            state.copy(
                isLoading = true,
                usbPermissionGranted = true
            )
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        val usbDongle = if (state.usbPermissionGranted) {
            if (device != null) {
                val connection = usbRepository.openDeviceOrNull(device)
                if (connection != null) {
                    when (device.toUsbDongleOrNull()) {
                        is FiioKa5 -> {
                            fiioKa5UsbCommunicationRepository.getCurrentState(connection)
                        }
                        is MoondropDawn44 -> {
                            moondropDawn44UsbCommunicationRepository.getCurrentState(connection)
                        }
                        else -> null
                    }
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
        reduce {
            state.copy(
                usbDongle = usbDongle,
                isLoading = false
            )
        }
        if (usbDongle != null) {
            postSideEffect(MainSideEffect.NotificationService.Start)
        }
    }

    fun requestUsbPermission() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        if (device != null) {
            val permissionRequested = usbRepository.requestUsbPermission(device)
            if (!permissionRequested) {
                if (usbRepository.hasUsbPermission(device)) {
                    handleUsbPermissionGranted()
                    return@intent
                }
            }
        }
        reduce {
            state.copy(isLoading = false)
        }
    }
}
