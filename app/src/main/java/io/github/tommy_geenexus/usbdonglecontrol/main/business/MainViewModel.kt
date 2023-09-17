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
import io.github.tommy_geenexus.usbdonglecontrol.dongle.HardwareVolumeControl
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.FiioKa5UsbCommunicationRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.data.MoondropDawnUsbCommunicationRepository
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn35_44.data.db.MoondropDawnProfile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.toUsbDongleOrNull
import io.github.tommy_geenexus.usbdonglecontrol.main.data.Profile
import io.github.tommy_geenexus.usbdonglecontrol.main.data.ProfileRepository
import io.github.tommy_geenexus.usbdonglecontrol.main.data.ProfilesList
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
    private val profileRepository: ProfileRepository,
    val fiioKa5UsbCommunicationRepository: FiioKa5UsbCommunicationRepository,
    val moondropDawnUsbCommunicationRepository: MoondropDawnUsbCommunicationRepository
) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    override val container = container<MainState, MainSideEffect>(
        initialState = MainState(),
        savedStateHandle = savedStateHandle,
        onCreate = { getInitialStateAndProfiles() }
    )

    fun getInitialStateAndProfiles() = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        val isUsbPermissionGranted = if (device != null) {
            usbRepository.hasUsbPermission(device)
        } else {
            false
        }
        val usbDongle = if (device != null && isUsbPermissionGranted) {
            val connection = usbRepository.openDeviceOrNull(device)
            if (connection != null) {
                when (device.toUsbDongleOrNull()) {
                    is FiioKa5 -> {
                        fiioKa5UsbCommunicationRepository.getCurrentState(connection)
                    }
                    is MoondropDawn44 -> {
                        moondropDawnUsbCommunicationRepository.getCurrentState(connection)
                    }
                    else -> null
                }
            } else {
                null
            }
        } else {
            null
        }
        val profiles = if (usbDongle != null) {
            profileRepository.getProfiles(usbDongle)
        } else {
            emptyList()
        }
        reduce {
            state.copy(
                usbDongle = usbDongle,
                profiles = ProfilesList(profiles),
                isDeviceAttached = device != null,
                isUsbPermissionGranted = isUsbPermissionGranted,
                loadingTasks = state.minusLoadingTask()
            )
        }
        if (device != null && !isUsbPermissionGranted) {
            postSideEffect(MainSideEffect.RequestPermissions)
        } else if (usbDongle is HardwareVolumeControl) {
            postSideEffect(MainSideEffect.Service.Stop)
            postSideEffect(MainSideEffect.Service.Start)
        }
    }

    fun getCurrentState() = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        val isUsbPermissionGranted = if (device != null) {
            usbRepository.hasUsbPermission(device)
        } else {
            false
        }
        val usbDongle = if (device != null && isUsbPermissionGranted) {
            val connection = usbRepository.openDeviceOrNull(device)
            if (connection != null) {
                when (device.toUsbDongleOrNull()) {
                    is FiioKa5 -> {
                        fiioKa5UsbCommunicationRepository.getCurrentState(connection)
                    }
                    is MoondropDawn44 -> {
                        moondropDawnUsbCommunicationRepository.getCurrentState(connection)
                    }
                    else -> null
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
                isDeviceAttached = device != null,
                isUsbPermissionGranted = isUsbPermissionGranted,
                loadingTasks = state.minusLoadingTask()
            )
        }
        if (device != null && !isUsbPermissionGranted) {
            postSideEffect(MainSideEffect.RequestPermissions)
        } else if (usbDongle is HardwareVolumeControl) {
            postSideEffect(MainSideEffect.Service.Stop)
            postSideEffect(MainSideEffect.Service.Start)
        }
    }

    fun handleAttachedDevicesChanged() = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        val isUsbPermissionGranted = if (device != null) {
            usbRepository.hasUsbPermission(device)
        } else {
            false
        }
        val usbDongle = device?.toUsbDongleOrNull()
        reduce {
            state.copy(
                usbDongle = usbDongle,
                loadingTasks = state.minusLoadingTask(),
                isDeviceAttached = device != null,
                isUsbPermissionGranted = isUsbPermissionGranted
            )
        }
        if (isUsbPermissionGranted) {
            getInitialStateAndProfiles()
        } else {
            postSideEffect(
                if (usbDongle != null) {
                    MainSideEffect.RequestPermissions
                } else {
                    MainSideEffect.Service.Stop
                }
            )
        }
    }

    fun requestUsbPermission() = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val device = usbRepository.getAttachedDeviceOrNull()
        val isUsbPermissionGranted = if (device != null) {
            val permissionRequested = usbRepository.requestUsbPermission(device)
            if (!permissionRequested) {
                usbRepository.hasUsbPermission(device)
            } else {
                false
            }
        } else {
            false
        }
        reduce {
            state.copy(
                loadingTasks = state.minusLoadingTask(),
                isDeviceAttached = device != null,
                isUsbPermissionGranted = isUsbPermissionGranted
            )
        }
        if (isUsbPermissionGranted) {
            getInitialStateAndProfiles()
        }
    }

    fun upsertProfile(
        profileName: String?,
        usbDongle: UsbDongle
    ) = intent {
        if (profileName.isNullOrEmpty()) {
            postSideEffect(MainSideEffect.Profile.Export.Failure)
            return@intent
        }
        val profile = when (usbDongle) {
            is FiioKa5 -> {
                FiioKa5Profile(
                    name = profileName,
                    channelBalance = usbDongle.channelBalance,
                    dacMode = usbDongle.dacMode,
                    displayBrightness = usbDongle.displayBrightness,
                    displayInvertEnabled = usbDongle.displayInvertEnabled,
                    displayTimeout = usbDongle.displayTimeout,
                    filter = usbDongle.filter,
                    firmwareVersion = usbDongle.firmwareVersion,
                    gain = usbDongle.gain,
                    hardwareMuteEnabled = usbDongle.hardwareMuteEnabled,
                    hidMode = usbDongle.hidMode,
                    sampleRate = usbDongle.sampleRate,
                    spdifOutEnabled = usbDongle.spdifOutEnabled,
                    volumeLevel = usbDongle.volumeLevel,
                    volumeMode = usbDongle.volumeMode
                )
            }
            is MoondropDawn44 -> {
                MoondropDawnProfile(
                    name = profileName,
                    productId = MoondropDawn44.PRODUCT_ID,
                    filter = usbDongle.filter,
                    gain = usbDongle.gain,
                    indicatorState = usbDongle.indicatorState,
                    volumeLevel = usbDongle.volumeLevel
                )
            }
            else -> {
                return@intent
            }
        }
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = profileRepository.upsertProfile(profile)
        reduce {
            state.copy(
                profiles = ProfilesList(result.getOrDefault(state.profiles.items)),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                MainSideEffect.Profile.Export.Success
            } else {
                MainSideEffect.Profile.Export.Failure
            }
        )
    }

    fun deleteProfile(profile: Profile) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val success = profileRepository.deleteProfile(profile)
        val profiles = state.profiles.items.toMutableList().apply {
            remove(profile)
        }
        if (success) {
            profileRepository.removeProfileShortcut(profile)
        }
        reduce {
            state.copy(
                profiles = if (success) ProfilesList(profiles) else state.profiles,
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (success) {
                MainSideEffect.Profile.Delete.Success
            } else {
                MainSideEffect.Profile.Delete.Failure
            }
        )
    }

    fun addProfileShortcut(profile: Profile) = intent {
        val success = profileRepository.addProfileShortcut(profile)
        postSideEffect(
            if (success) {
                MainSideEffect.Shortcut.Add.Success
            } else {
                MainSideEffect.Shortcut.Add.Failure
            }
        )
    }

    fun removeProfileShortcut(profile: Profile) = intent {
        val success = profileRepository.removeProfileShortcut(profile)
        postSideEffect(
            if (success) {
                MainSideEffect.Shortcut.Delete.Success
            } else {
                MainSideEffect.Shortcut.Delete.Failure
            }
        )
    }
}
