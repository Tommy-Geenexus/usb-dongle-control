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

package io.github.tommygeenexus.usbdonglecontrol.setup.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.tommygeenexus.usbdonglecontrol.core.data.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongle
import javax.inject.Inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class SetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val usbRepository: UsbRepository
) : ViewModel(),
    ContainerHost<SetupState, SetupSideEffect> {

    override val container = container<SetupState, SetupSideEffect>(
        initialState = SetupState(),
        savedStateHandle = savedStateHandle,
        onCreate = {
            verifyConnectedUsbDevices()
        }
    )

    fun verifyConnectedUsbDevices() = intent {
        reduce {
            state.copy(isLoading = true)
        }
        val result = usbRepository.getFirstAttachedUsbDongle()
        if (result.isSuccess) {
            val (usbDevice, usbDongle, isUsbPermissionGranted) = result.getOrThrow()
            if (isUsbPermissionGranted) {
                reduce {
                    state.copy(
                        usbDongle = usbDongle,
                        isDeviceAttached = true,
                        isUsbPermissionGranted = true,
                        isLoading = false
                    )
                }
                postSideEffect(SetupSideEffect.Navigate.Control)
            } else {
                reduce {
                    state.copy(
                        usbDongle = usbDongle,
                        isDeviceAttached = true,
                        isUsbPermissionGranted = false,
                        isLoading = false
                    )
                }
                val permissionRequestResult = usbRepository.requestUsbPermission(usbDevice)
                if (permissionRequestResult.isSuccess) {
                    if (permissionRequestResult.getOrThrow()) {
                        postSideEffect(SetupSideEffect.PermissionRequest.Success)
                    } else {
                        postSideEffect(SetupSideEffect.Navigate.Control)
                    }
                } else {
                    postSideEffect(SetupSideEffect.PermissionRequest.Failure)
                }
            }
        } else {
            reduce {
                state.copy(
                    usbDongle = UnsupportedUsbDongle,
                    isDeviceAttached = false,
                    isUsbPermissionGranted = false,
                    isLoading = false
                )
            }
        }
    }
}
