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

package io.github.tommygeenexus.usbdonglecontrol.settings.ui

import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.settings.business.SettingsSideEffect
import io.github.tommygeenexus.usbdonglecontrol.settings.business.SettingsViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onNavigateUp: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val activity = LocalActivity.current
        if (activity != null) {
            val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
            SideEffect {
                @Suppress("DEPRECATION")
                activity.window?.navigationBarColor = surfaceColor
            }
        }
    }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SettingsSideEffect.MaximizeVolume.Failure -> {
            }
            SettingsSideEffect.MaximizeVolume.Success -> {
            }
        }
    }
    val state by viewModel.collectAsState()
    SettingScreen(
        isMaximizeVolumeEnabled = state.isMaximizeVolumeEnabled,
        onMaximizeVolumeRequested = { isEnabled ->
            viewModel.storeMaximizeVolume(isEnabled)
        },
        onNavigateUp = onNavigateUp
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isMaximizeVolumeEnabled: Boolean = false,
    onMaximizeVolumeRequested: (Boolean) -> Unit = {},
    onNavigateUp: () -> Unit = {}
) {
    Scaffold(
        modifier = if (LocalConfiguration.current.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
        ) {
            modifier.windowInsetsPadding(WindowInsets.displayCutout)
        } else {
            modifier
        },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            LazyColumn {
                item {
                    SettingsAudioItem(
                        isMaximizeVolumeEnabled = isMaximizeVolumeEnabled,
                        onMaximizeVolumeSwitched = onMaximizeVolumeRequested
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingsScreen() {
    SettingScreen()
}
