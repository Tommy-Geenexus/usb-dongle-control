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

package io.github.tommygeenexus.usbdonglecontrol.setup.ui

import android.Manifest
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.receiver.UsbDeviceAttachDetachPermissionReceiver
import io.github.tommygeenexus.usbdonglecontrol.core.util.INTENT_ACTION_USB_PERMISSION
import io.github.tommygeenexus.usbdonglecontrol.setup.business.SetupSideEffect
import io.github.tommygeenexus.usbdonglecontrol.setup.business.SetupViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SetupScreen(viewModel: SetupViewModel, onNavigateToControl: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val permissionRequestFail = stringResource(id = R.string.permission_request_fail)
    val permissionRequestSuccess = stringResource(id = R.string.permission_request_success)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SetupSideEffect.Navigate.Control -> {
                onNavigateToControl()
            }
            SetupSideEffect.PermissionRequest.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = permissionRequestFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            SetupSideEffect.PermissionRequest.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = permissionRequestSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    DisposableEffect(LocalLifecycleOwner.current) {
        val usbReceiver = UsbDeviceAttachDetachPermissionReceiver(
            onPermissionGrantResult = { isGranted ->
                if (isGranted) {
                    viewModel.verifyConnectedUsbDevices()
                }
            },
            onAttachedDevicesChanged = {
                viewModel.verifyConnectedUsbDevices()
            }
        )
        ContextCompat.registerReceiver(
            context,
            usbReceiver,
            IntentFilter(INTENT_ACTION_USB_PERMISSION).apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            context.unregisterReceiver(usbReceiver)
        }
    }
    val state by viewModel.collectAsState()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { _ ->
            viewModel.verifyConnectedUsbDevices()
        }
        if (!permissionState.status.isGranted && !permissionState.status.shouldShowRationale) {
            SideEffect {
                permissionState.launchPermissionRequest()
            }
        }
    }
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
    SetupScreen(
        snackBarHostState = snackBarHostState,
        isDeviceAttached = state.usbDongle != UnsupportedUsbDongle,
        isUsbPermissionGranted = state.isUsbPermissionGranted,
        isLoading = state.isLoading,
        onPermissionRequest = {
            viewModel.verifyConnectedUsbDevices()
        }
    )
}

@Composable
fun SetupScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isDeviceAttached: Boolean = false,
    isUsbPermissionGranted: Boolean = false,
    isLoading: Boolean = false,
    onPermissionRequest: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SetupTopAppBar()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
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
            AnimatedVisibility(visible = isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (!isDeviceAttached) {
                AttachUsbDongle()
            } else if (!isUsbPermissionGranted) {
                GrantUsbPermission(
                    isLoading = isLoading,
                    onPermissionRequest = onPermissionRequest
                )
            }
        }
    }
}

@Preview(
    name = "Default",
    group = "No device"
)
@Composable
private fun SetupScreenPreview1() {
    SetupScreen(
        isDeviceAttached = false,
        isUsbPermissionGranted = false,
        isLoading = false
    )
}

@Preview(
    name = "Device attached",
    group = "Device"
)
@Composable
private fun SetupScreenPreview2() {
    SetupScreen(
        isDeviceAttached = true,
        isUsbPermissionGranted = false,
        isLoading = false
    )
}

@Preview(
    name = "Device attached, permission granted",
    group = "Device"
)
@Composable
private fun SetupScreenPreview3() {
    SetupScreen(
        isDeviceAttached = true,
        isUsbPermissionGranted = true,
        isLoading = false
    )
}

@Preview(
    name = "Device attached, permission granted, loading",
    group = "Device"
)
@Composable
private fun SetupScreenPreview4() {
    SetupScreen(
        isDeviceAttached = true,
        isUsbPermissionGranted = true,
        isLoading = true
    )
}
