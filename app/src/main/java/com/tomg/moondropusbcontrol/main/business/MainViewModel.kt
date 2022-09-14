/*
 * Copyright (c) 2022, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package com.tomg.moondropusbcontrol.main.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tomg.moondropusbcontrol.main.Filter
import com.tomg.moondropusbcontrol.main.Gain
import com.tomg.moondropusbcontrol.main.IndicatorState
import com.tomg.moondropusbcontrol.main.data.UsbCommunicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val usbCommunicationRepository: UsbCommunicationRepository
) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    override val container = container<MainState, MainSideEffect>(
        initialState = MainState(),
        savedStateHandle = savedStateHandle,
        onCreate = { getCurrentState() }
    )

    private fun getCurrentState() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val isAttached = usbCommunicationRepository.getAttachedDeviceOrNull() != null
        if (state.usbPermissionGranted) {
            val device = usbCommunicationRepository.getAttachedDeviceOrNull()
            val deviceState = if (device != null) {
                val connection = usbCommunicationRepository.openDeviceOrNull(device)
                if (connection != null) {
                    usbCommunicationRepository.getCurrentState(connection)
                } else {
                    null
                }
            } else {
                null
            }
            reduce {
                state.copy(
                    filter = deviceState?.first ?: state.filter,
                    gain = deviceState?.second ?: state.gain,
                    indicatorState = deviceState?.third ?: state.indicatorState,
                    isDeviceAttached = isAttached,
                    isLoading = false
                )
            }
        } else {
            reduce {
                state.copy(
                    isDeviceAttached = isAttached,
                    isLoading = false
                )
            }
            if (isAttached) {
                requestUsbPermission()
            }
        }
    }

    fun handleAttachedDevicesChanged() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val isAttached = usbCommunicationRepository.getAttachedDeviceOrNull() != null
        reduce {
            state.copy(
                isDeviceAttached = isAttached,
                isLoading = false,
                usbPermissionGranted = if (!isAttached) false else state.usbPermissionGranted
            )
        }
        if (isAttached) {
            requestUsbPermission()
        }
    }

    fun handleUsbPermissionGranted() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbCommunicationRepository.getAttachedDeviceOrNull()
        val deviceState = if (device != null) {
            val connection = usbCommunicationRepository.openDeviceOrNull(device)
            if (connection != null) {
                usbCommunicationRepository.getCurrentState(connection)
            } else {
                null
            }
        } else {
            null
        }
        reduce {
            state.copy(
                filter = deviceState?.first ?: state.filter,
                gain = deviceState?.second ?: state.gain,
                indicatorState = deviceState?.third ?: state.indicatorState,
                isLoading = false,
                usbPermissionGranted = true
            )
        }
    }

    fun requestUsbPermission() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbCommunicationRepository.getAttachedDeviceOrNull()
        if (device != null) {
            val permissionRequested = usbCommunicationRepository.requestUsbPermission(device)
            if (!permissionRequested) {
                if (usbCommunicationRepository.hasUsbPermission(device)) {
                    handleUsbPermissionGranted()
                    return@intent
                }
            }
        }
        reduce {
            state.copy(isLoading = false)
        }
    }

    fun setFilter(filter: Filter) = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbCommunicationRepository.getAttachedDeviceOrNull()
        val success = if (device != null) {
            val connection = usbCommunicationRepository.openDeviceOrNull(device)
            if (connection != null) {
                usbCommunicationRepository.setFilter(connection, filter)
            } else {
                false
            }
        } else {
            false
        }
        reduce {
            state.copy(
                filter = if (success) filter else state.filter,
                isLoading = false
            )
        }
        if (success) {
            postSideEffect(MainSideEffect.Filter)
        }
    }

    fun setGain(gain: Gain) = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbCommunicationRepository.getAttachedDeviceOrNull()
        val success = if (device != null) {
            val connection = usbCommunicationRepository.openDeviceOrNull(device)
            if (connection != null) {
                usbCommunicationRepository.setGain(connection, gain)
            } else {
                false
            }
        } else {
            false
        }
        reduce {
            state.copy(
                gain = if (success) gain else state.gain,
                isLoading = false
            )
        }
        if (success) {
            postSideEffect(MainSideEffect.Gain)
        }
    }

    fun setIndicatorState(indicatorState: IndicatorState) = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val device = usbCommunicationRepository.getAttachedDeviceOrNull()
        val success = if (device != null) {
            val connection = usbCommunicationRepository.openDeviceOrNull(device)
            if (connection != null) {
                usbCommunicationRepository.setIndicatorState(connection, indicatorState)
            } else {
                false
            }
        } else {
            false
        }
        reduce {
            state.copy(
                indicatorState = if (success) indicatorState else state.indicatorState,
                isLoading = false
            )
        }
        if (success) {
            postSideEffect(MainSideEffect.IndicatorState)
        }
    }
}
