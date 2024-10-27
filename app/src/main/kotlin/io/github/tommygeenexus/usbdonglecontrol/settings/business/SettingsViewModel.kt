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

package io.github.tommygeenexus.usbdonglecontrol.settings.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.tommygeenexus.usbdonglecontrol.settings.data.SettingsRepository
import javax.inject.Inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository
) : ViewModel(),
    ContainerHost<SettingsState, SettingsSideEffect> {

    override val container = container<SettingsState, SettingsSideEffect>(
        initialState = SettingsState(),
        savedStateHandle = savedStateHandle,
        onCreate = {
            readDefaultValues()
        }
    )

    private fun readDefaultValues() = intent {
        val isMaximizeVolumeEnabled = settingsRepository.isMaximizeVolumeEnabled()
        reduce {
            state.copy(isMaximizeVolumeEnabled = isMaximizeVolumeEnabled)
        }
    }

    fun storeMaximizeVolume(isEnabled: Boolean) = intent {
        val result = settingsRepository.putMaximizeVolume(isEnabled)
        if (result.isSuccess) {
            reduce {
                state.copy(isMaximizeVolumeEnabled = isEnabled)
            }
            postSideEffect(SettingsSideEffect.MaximizeVolume.Success)
        } else {
            postSideEffect(SettingsSideEffect.MaximizeVolume.Failure)
        }
    }
}
