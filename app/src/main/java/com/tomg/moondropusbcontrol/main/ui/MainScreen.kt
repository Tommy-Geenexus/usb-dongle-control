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

package com.tomg.moondropusbcontrol.main.ui

import android.content.IntentFilter
import android.hardware.usb.UsbManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomg.moondropusbcontrol.INTENT_ACTION_USB_PERMISSION
import com.tomg.moondropusbcontrol.R
import com.tomg.moondropusbcontrol.UsbReceiver
import com.tomg.moondropusbcontrol.main.Filter
import com.tomg.moondropusbcontrol.main.Gain
import com.tomg.moondropusbcontrol.main.IndicatorState
import com.tomg.moondropusbcontrol.main.business.MainSideEffect
import com.tomg.moondropusbcontrol.main.business.MainViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val usbReceiver = UsbReceiver(
            onPermissionGranted = { viewModel.handleUsbPermissionGranted() },
            onDeviceAttached = { viewModel.handleAttachedDevicesChanged() },
            onDeviceDetached = { viewModel.handleAttachedDevicesChanged() }
        )
        context.registerReceiver(
            usbReceiver,
            IntentFilter(INTENT_ACTION_USB_PERMISSION).apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            }
        )
        onDispose {
            context.unregisterReceiver(usbReceiver)
        }
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val filterSet = stringResource(id = R.string.filter_set)
    val gainSet = stringResource(id = R.string.gain_set)
    val indicatorStateSet = stringResource(id = R.string.indicator_state_set)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            MainSideEffect.Filter -> {
                scope.launch {
                    snackBarHostState.showSnackbar(message = filterSet)
                }
            }
            MainSideEffect.Gain -> {
                scope.launch {
                    snackBarHostState.showSnackbar(message = gainSet)
                }
            }
            MainSideEffect.IndicatorState -> {
                scope.launch {
                    snackBarHostState.showSnackbar(message = indicatorStateSet)
                }
            }
        }
    }
    val state by viewModel.collectAsState()
    MainScreen(
        modifier = modifier,
        snackBarHostState = snackBarHostState,
        filter = state.filter,
        gain = state.gain,
        indicatorState = state.indicatorState,
        isDeviceAttached = state.isDeviceAttached,
        isLoading = state.isLoading,
        usbPermissionGranted = state.usbPermissionGranted,
        onPermissionRequest = { viewModel.requestUsbPermission() },
        onFilterSelected = { filter -> viewModel.setFilter(filter) },
        onGainSelected = { gain -> viewModel.setGain(gain) },
        onIndicatorStateSelected = { indicatorState -> viewModel.setIndicatorState(indicatorState) }
    )
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    filter: Filter = Filter.default(),
    gain: Gain = Gain.default(),
    indicatorState: IndicatorState = IndicatorState.default(),
    isDeviceAttached: Boolean = false,
    isLoading: Boolean = false,
    usbPermissionGranted: Boolean = false,
    onPermissionRequest: () -> Unit = {},
    onFilterSelected: (Filter) -> Unit = {},
    onGainSelected: (Gain) -> Unit = {},
    onIndicatorStateSelected: (IndicatorState) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            AnimatedVisibility(visible = isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (!isDeviceAttached) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Cable,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.attach_device),
                            modifier = Modifier.padding(all = 16.dp)
                        )
                    }
                }
            } else if (!usbPermissionGranted) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onPermissionRequest,
                        enabled = !isLoading
                    ) {
                        Text(text = stringResource(id = R.string.grant_permission))
                    }
                }
            } else {
                LazyColumn {
                    item {
                        ItemFilter(
                            currentFilter = filter,
                            onFilterSelected = onFilterSelected
                        )
                    }
                    item {
                        ItemGain(
                            currentGain = gain,
                            onGainSelected = onGainSelected
                        )
                    }
                    item {
                        ItemIndicatorState(
                            currentIndicatorState = indicatorState,
                            onIndicatorStateSelected = onIndicatorStateSelected
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Main Screen")
@Composable
fun MainScreenPreview() {
    MainScreen()
}
