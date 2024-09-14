/*
 * Copyright (c) 2022-2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.control.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.control.data.ProfileRepository
import io.github.tommygeenexus.usbdonglecontrol.control.domain.GetCurrentStateUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.GetVolumeLevelUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetChannelBalanceUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetDacFilterUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetDacModeUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetDisplayBrightnessUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetDisplayInvertEnabledUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetDisplayTimeoutUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetGainUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetHardwareMuteEnabledUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetHidModeUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetIndicatorStateUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetProfileUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetSpdifOutEnabledUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetVolumeLevelUseCase
import io.github.tommygeenexus.usbdonglecontrol.control.domain.SetVolumeModeUseCase
import io.github.tommygeenexus.usbdonglecontrol.core.data.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import javax.inject.Inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class ControlViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    pagingConfig: PagingConfig,
    private val usbRepository: UsbRepository,
    private val profileRepository: ProfileRepository,
    private val setProfileUseCase: SetProfileUseCase,
    private val getCurrentStateUseCase: GetCurrentStateUseCase,
    private val getVolumeLevelUseCase: GetVolumeLevelUseCase,
    private val setChannelBalanceUseCase: SetChannelBalanceUseCase,
    private val setDacFilterUseCase: SetDacFilterUseCase,
    private val setDacModeUseCase: SetDacModeUseCase,
    private val setDisplayBrightnessUseCase: SetDisplayBrightnessUseCase,
    private val setDisplayInvertEnabledUseCase: SetDisplayInvertEnabledUseCase,
    private val setDisplayTimeoutUseCase: SetDisplayTimeoutUseCase,
    private val setGainUseCase: SetGainUseCase,
    private val setHardwareMuteEnabledUseCase: SetHardwareMuteEnabledUseCase,
    private val setHidModeUseCase: SetHidModeUseCase,
    private val setIndicatorStateUseCase: SetIndicatorStateUseCase,
    private val setSpdifOutEnabledUseCase: SetSpdifOutEnabledUseCase,
    private val setVolumeLevelUseCase: SetVolumeLevelUseCase,
    private val setVolumeModeUseCase: SetVolumeModeUseCase
) : ViewModel(),
    ContainerHost<ControlState, ControlSideEffect> {

    override val container = container<ControlState, ControlSideEffect>(
        initialState = ControlState(),
        savedStateHandle = savedStateHandle,
        onCreate = {
            getCurrentStateForFirstAttachedUsbDongle()
        }
    )

    val profileFlow = Pager(
        config = pagingConfig,
        pagingSourceFactory = {
            profileRepository.getProfilesForUsbDongleWith(
                vendorId = container.stateFlow.value.usbDongle.vendorId,
                productId = container.stateFlow.value.usbDongle.productId
            )
        }
    ).flow

    fun synchronizeVolumeLevel(usbDongle: UsbDongle) = intent {
        reduce {
            state.copy(usbDongle = usbDongle)
        }
    }

    fun getCurrentStateForUsbDongle(usbDongle: UsbDongle) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = getCurrentStateUseCase(usbDongle)
        reduce {
            state.copy(
                usbDongle = result.getOrElse { UnsupportedUsbDongle },
                loadingTasks = state.minusLoadingTask()
            )
        }
        if (result.isSuccess) {
            postSideEffect(ControlSideEffect.UsbCommunication.Rw.Success)
            postSideEffect(ControlSideEffect.Profile.Get.All)
            if (usbDongle is HardwareVolumeControl) {
                postSideEffect(ControlSideEffect.Service.Stop)
                postSideEffect(ControlSideEffect.Service.Start)
            }
        } else {
            postSideEffect(ControlSideEffect.UsbCommunication.Rw.Failure)
        }
    }

    private fun getCurrentStateForFirstAttachedUsbDongle() = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = usbRepository.getFirstAttachedUsbDongle()
        reduce {
            state.copy(loadingTasks = state.minusLoadingTask())
        }
        if (result.isSuccess) {
            val (_, usbDongle, _) = result.getOrThrow()
            postSideEffect(ControlSideEffect.UsbCommunication.Get.Success(usbDongle))
        } else {
            postSideEffect(ControlSideEffect.UsbCommunication.Get.Failure)
        }
    }

    fun setProfile(profile: Profile) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setProfileUseCase(state.usbDongle, profile)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        if (result.isSuccess) {
            postSideEffect(ControlSideEffect.Profile.Apply.Success)
            if (state.usbDongle is HardwareVolumeControl) {
                postSideEffect(ControlSideEffect.Service.Stop)
                postSideEffect(ControlSideEffect.Service.Start)
            }
        } else {
            postSideEffect(ControlSideEffect.Profile.Apply.Failure)
        }
    }

    fun exportProfile(profile: Profile) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = profileRepository.exportProfile(profile)
        reduce {
            state.copy(loadingTasks = state.minusLoadingTask())
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.Profile.Export.Success
            } else {
                ControlSideEffect.Profile.Export.Failure
            }
        )
    }

    fun deleteProfile(profile: Profile) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = profileRepository.deleteProfile(profile = profile)
        if (result.isSuccess) {
            profileRepository.removeProfileShortcut(profile)
        }
        reduce {
            state.copy(loadingTasks = state.minusLoadingTask())
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.Profile.Delete.Success
            } else {
                ControlSideEffect.Profile.Delete.Failure
            }
        )
    }

    fun addProfileShortcut(profile: Profile) = intent {
        val result = profileRepository.addProfileShortcut(profile)
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.Shortcut.Add.Success
            } else {
                ControlSideEffect.Shortcut.Add.Failure
            }
        )
    }

    fun removeProfileShortcut(profile: Profile) = intent {
        val result = profileRepository.removeProfileShortcut(profile)
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.Shortcut.Delete.Success
            } else {
                ControlSideEffect.Shortcut.Delete.Failure
            }
        )
    }

    fun setChannelBalance(channelBalance: Int) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setChannelBalanceUseCase(state.usbDongle, channelBalance)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setDacFilter(dacFilterId: Byte) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setDacFilterUseCase(state.usbDongle, dacFilterId)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setDacMode(dacModeId: Byte) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setDacModeUseCase(state.usbDongle, dacModeId)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setDisplayBrightness(displayBrightness: Int) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setDisplayBrightnessUseCase(state.usbDongle, displayBrightness)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setDisplayInvertEnabled(isDisplayInvertEnabled: Boolean) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setDisplayInvertEnabledUseCase(state.usbDongle, isDisplayInvertEnabled)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setDisplayTimeout(displayTimeout: Int) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setDisplayTimeoutUseCase(state.usbDongle, displayTimeout)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setGain(gainId: Byte) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setGainUseCase(state.usbDongle, gainId)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setHardwareMuteEnabled(isHardwareMuteEnabled: Boolean) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setHardwareMuteEnabledUseCase(state.usbDongle, isHardwareMuteEnabled)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setHidMode(hidModeId: Byte) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setHidModeUseCase(state.usbDongle, hidModeId)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setIndicatorState(indicatorStateId: Byte) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setIndicatorStateUseCase(state.usbDongle, indicatorStateId)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setSpdifOutEnabled(isSpdifOutEnabled: Boolean) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setSpdifOutEnabledUseCase(state.usbDongle, isSpdifOutEnabled)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        postSideEffect(
            if (result.isSuccess) {
                ControlSideEffect.UsbCommunication.Rw.Success
            } else {
                ControlSideEffect.UsbCommunication.Rw.Failure
            }
        )
    }

    fun setVolumeLevel(volumeLevel: Int) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        val result = setVolumeLevelUseCase(state.usbDongle, volumeLevel)
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        if (result.isSuccess) {
            postSideEffect(ControlSideEffect.UsbCommunication.Rw.Success)
            if (state.usbDongle is HardwareVolumeControl) {
                postSideEffect(ControlSideEffect.Service.Stop)
                postSideEffect(ControlSideEffect.Service.Start)
            }
        } else {
            postSideEffect(ControlSideEffect.UsbCommunication.Rw.Failure)
        }
    }

    fun setVolumeMode(volumeModeId: Byte) = intent {
        reduce {
            state.copy(loadingTasks = state.plusLoadingTask())
        }
        var result = setVolumeModeUseCase(state.usbDongle, volumeModeId)
        if (result.isSuccess) {
            result = getVolumeLevelUseCase(state.usbDongle)
        }
        reduce {
            state.copy(
                usbDongle = result.getOrDefault(state.usbDongle),
                loadingTasks = state.minusLoadingTask()
            )
        }
        if (result.isSuccess) {
            postSideEffect(ControlSideEffect.UsbCommunication.Rw.Success)
            if (state.usbDongle is HardwareVolumeControl) {
                postSideEffect(ControlSideEffect.Service.Stop)
                postSideEffect(ControlSideEffect.Service.Start)
            }
        } else {
            postSideEffect(ControlSideEffect.UsbCommunication.Rw.Failure)
        }
    }
}
