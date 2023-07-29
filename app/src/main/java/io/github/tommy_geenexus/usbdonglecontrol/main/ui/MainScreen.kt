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

package io.github.tommy_geenexus.usbdonglecontrol.main.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_ACTION_USB_PERMISSION
import io.github.tommy_geenexus.usbdonglecontrol.R
import io.github.tommy_geenexus.usbdonglecontrol.UsbReceiver
import io.github.tommy_geenexus.usbdonglecontrol.UsbService
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.applyFiioKa5Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setChannelBalance
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setDacMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setDisplayBrightness
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setDisplayInvert
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setDisplayTimeout
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setFilter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setGain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setHardwareMute
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setHidMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setSpdifOut
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setVolumeLevel
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.setVolumeMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.updateChannelBalance
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.updateDisplayBrightness
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.updateDisplayTimeout
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.business.updateVolumeLevel
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.DacMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.HidMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.VolumeMode
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.ui.FiioKa5Items
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.business.applyMoondropDawn44Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.business.setFilter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.business.setGain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.business.setIndicatorState
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.IndicatorState
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db.MoondropDawn44Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.ui.MoondropDawn44Items
import io.github.tommy_geenexus.usbdonglecontrol.dongle.productName
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainSideEffect
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainViewModel
import io.github.tommy_geenexus.usbdonglecontrol.main.data.Profile
import io.github.tommy_geenexus.usbdonglecontrol.main.data.ProfilesList
import io.github.tommy_geenexus.usbdonglecontrol.main.data.consumeProfileShortcut
import io.github.tommy_geenexus.usbdonglecontrol.theme.getHorizontalPadding
import io.github.tommy_geenexus.usbdonglecontrol.theme.iconSize
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    systemUiController: SystemUiController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { _ ->
            viewModel.getCurrentState()
        }
    } else {
        null
    }
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val profileApplyFail = stringResource(id = R.string.profile_apply_fail)
    val profileApplySuccess = stringResource(id = R.string.profile_apply_success)
    val profileDeleteFail = stringResource(id = R.string.profile_delete_fail)
    val profileDeleteSuccess = stringResource(id = R.string.profile_delete_success)
    val profileExportFail = stringResource(id = R.string.profile_export_fail)
    val profileExportSuccess = stringResource(id = R.string.profile_export_success)
    val shortcutAddFail = stringResource(id = R.string.shortcut_add_fail)
    val shortcutAddSuccess = stringResource(id = R.string.shortcut_add_success)
    val shortcutDeleteFail = stringResource(id = R.string.shortcut_delete_fail)
    val shortcutDeleteSuccess = stringResource(id = R.string.shortcut_delete_success)
    val usbCommFailure = stringResource(id = R.string.usb_comm_failure)
    val usbCommSuccess = stringResource(id = R.string.usb_comm_success)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            MainSideEffect.Profile.Apply.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileApplyFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Profile.Apply.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileApplySuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Profile.Delete.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileDeleteFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Profile.Delete.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileDeleteSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Profile.Export.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileExportFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Profile.Export.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileExportSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.RequestPermissions -> {
                if (permissionState?.status?.isGranted == false &&
                    !permissionState.status.shouldShowRationale
                ) {
                    permissionState.launchPermissionRequest()
                } else {
                    viewModel.requestUsbPermission()
                }
            }
            MainSideEffect.Service.Start -> {
                context.startService(Intent(context, UsbService::class.java))
            }
            MainSideEffect.Service.Stop -> {
                context.stopService(Intent(context, UsbService::class.java))
            }
            MainSideEffect.Shortcut.Add.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutAddFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Shortcut.Add.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutAddSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Shortcut.Delete.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutDeleteFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.Shortcut.Delete.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutDeleteSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.UsbCommunication.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = usbCommFailure,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            MainSideEffect.UsbCommunication.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = usbCommSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val usbReceiver = UsbReceiver(
            onPermissionGrantResult = { isGranted ->
                if (isGranted) {
                    viewModel.getInitialStateAndProfiles()
                }
            },
            onAttachedDevicesChanged = { viewModel.handleAttachedDevicesChanged() }
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
    val surfaceColor = MaterialTheme.colorScheme.surface
    val bottomAppBarColor =
        MaterialTheme.colorScheme.surfaceColorAtElevation(BottomAppBarDefaults.ContainerElevation)
    SideEffect {
        systemUiController.setNavigationBarColor(
            if (state.usbDongle != null && state.isUsbPermissionGranted) {
                bottomAppBarColor
            } else {
                surfaceColor
            }
        )
    }
    MainScreen(
        modifier = modifier,
        windowSizeClass = windowSizeClass,
        systemUiController = systemUiController,
        snackBarHostState = snackBarHostState,
        usbDongle = state.usbDongle,
        profiles = state.profiles,
        isLoading = state.loadingTasks > 0.toUInt(),
        isDeviceAttached = state.isDeviceAttached,
        isUsbPermissionGranted = state.isUsbPermissionGranted,
        onPermissionRequest = { viewModel.requestUsbPermission() },
        onRefresh = { viewModel.getCurrentState() },
        onReset = { dongle ->
            if (dongle is FiioKa5) {
                viewModel.applyFiioKa5Profile(dongle)
            } else if (dongle is MoondropDawn44) {
                viewModel.applyMoondropDawn44Profile(dongle)
            }
        },
        onProfileShortcutAdd = { profile ->
            viewModel.addProfileShortcut(profile)
        },
        onProfileShortcutRemove = { profile ->
            viewModel.removeProfileShortcut(profile)
        },
        onProfileDelete = { profile ->
            viewModel.deleteProfile(profile)
        },
        onProfileApply = { dongle, profile ->
            if (dongle is FiioKa5) {
                viewModel.applyFiioKa5Profile(dongle, profile as FiioKa5Profile)
            } else if (dongle is MoondropDawn44) {
                viewModel.applyMoondropDawn44Profile(dongle, profile as MoondropDawn44Profile)
            }
        },
        onProfileExport = { profileName, usbDongle ->
            viewModel.upsertProfile(profileName, usbDongle)
        },
        onDisplayBrightnessChanged = { dongle, brightness ->
            if (dongle is FiioKa5) {
                viewModel.updateDisplayBrightness(dongle, brightness)
            }
        },
        onDisplayBrightnessSelected = { dongle, brightness ->
            if (dongle is FiioKa5) {
                viewModel.setDisplayBrightness(dongle, brightness)
            }
        },
        onChannelBalanceChanged = { dongle, channelBalance ->
            if (dongle is FiioKa5) {
                viewModel.updateChannelBalance(dongle, channelBalance)
            }
        },
        onChannelBalanceSelected = { dongle, channelBalance ->
            if (dongle is FiioKa5) {
                viewModel.setChannelBalance(dongle, channelBalance)
            }
        },
        onDacModeSelected = { dongle, dacMode ->
            if (dongle is FiioKa5) {
                viewModel.setDacMode(dongle, dacMode)
            }
        },
        onDisplayTimeoutChanged = { dongle, timeout ->
            if (dongle is FiioKa5) {
                viewModel.updateDisplayTimeout(dongle, timeout)
            }
        },
        onDisplayTimeoutSelected = { dongle, timeout ->
            if (dongle is FiioKa5) {
                viewModel.setDisplayTimeout(dongle, timeout)
            }
        },
        onDisplayInvertChange = { dongle, displayInvert ->
            if (dongle is FiioKa5) {
                viewModel.setDisplayInvert(dongle, displayInvert)
            }
        },
        onFilterSelected = { dongle, filter ->
            if (dongle is FiioKa5) {
                viewModel.setFilter(dongle, filter)
            } else if (dongle is MoondropDawn44) {
                viewModel.setFilter(dongle, filter)
            }
        },
        onGainSelected = { dongle, gain ->
            if (dongle is FiioKa5) {
                viewModel.setGain(dongle, gain)
            } else if (dongle is MoondropDawn44) {
                viewModel.setGain(dongle, gain)
            }
        },
        onHardwareMuteEnabledSelected = { dongle, mute ->
            if (dongle is FiioKa5) {
                viewModel.setHardwareMute(dongle, mute)
            }
        },
        onHidModeSelected = { dongle, hidMode ->
            if (dongle is FiioKa5) {
                viewModel.setHidMode(dongle, hidMode)
            }
        },
        onIndicatorStateSelected = { dongle, indicatorState ->
            if (dongle is MoondropDawn44) {
                viewModel.setIndicatorState(dongle, indicatorState)
            }
        },
        onSpdifOutEnabledSelected = { dongle, out ->
            if (dongle is FiioKa5) {
                viewModel.setSpdifOut(dongle, out)
            }
        },
        onVolumeLevelChanged = { dongle, volumeLevel ->
            if (dongle is FiioKa5) {
                viewModel.updateVolumeLevel(dongle, volumeLevel) }
        },
        onVolumeLevelSelected = { dongle, volumeLevel ->
            if (dongle is FiioKa5) {
                viewModel.setVolumeLevel(dongle, volumeLevel) }
        },
        onVolumeModeSelected = { dongle, volumeMode ->
            if (dongle is FiioKa5) {
                viewModel.setVolumeMode(dongle, volumeMode)
            }
        }
    )
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero),
    systemUiController: SystemUiController = rememberSystemUiController(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    profileListState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    usbDongle: UsbDongle? = null,
    profiles: ProfilesList = ProfilesList(),
    isLoading: Boolean = false,
    isDeviceAttached: Boolean = false,
    isUsbPermissionGranted: Boolean = false,
    onPermissionRequest: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onReset: (UsbDongle) -> Unit = {},
    onProfileShortcutAdd: (Profile) -> Unit = {},
    onProfileShortcutRemove: (Profile) -> Unit = {},
    onProfileDelete: (Profile) -> Unit = {},
    onProfileApply: (UsbDongle, Profile) -> Unit = { _, _ -> },
    onProfileExport: (String, UsbDongle) -> Unit = { _, _ -> },
    onChannelBalanceChanged: (UsbDongle, Int) -> Unit = { _, _ -> },
    onChannelBalanceSelected: (UsbDongle, Int) -> Unit = { _, _ -> },
    onDacModeSelected: (UsbDongle, DacMode) -> Unit = { _, _ -> },
    onDisplayBrightnessChanged: (UsbDongle, Int) -> Unit = { _, _ -> },
    onDisplayBrightnessSelected: (UsbDongle, Int) -> Unit = { _, _ -> },
    onDisplayTimeoutChanged: (UsbDongle, Int) -> Unit = { _, _ -> },
    onDisplayTimeoutSelected: (UsbDongle, Int) -> Unit = { _, _ -> },
    onDisplayInvertChange: (UsbDongle, Boolean) -> Unit = { _, _ -> },
    onFilterSelected: (UsbDongle, Filter) -> Unit = { _, _ -> },
    onGainSelected: (UsbDongle, Gain) -> Unit = { _, _ -> },
    onHardwareMuteEnabledSelected: (UsbDongle, Boolean) -> Unit = { _, _ -> },
    onHidModeSelected: (UsbDongle, HidMode) -> Unit = { _, _ -> },
    onIndicatorStateSelected: (UsbDongle, IndicatorState) -> Unit = { _, _ -> },
    onSpdifOutEnabledSelected: (UsbDongle, Boolean) -> Unit = { _, _ -> },
    onVolumeLevelChanged: (UsbDongle, Int) -> Unit = { _, _ -> },
    onVolumeLevelSelected: (UsbDongle, Int) -> Unit = { _, _ -> },
    onVolumeModeSelected: (UsbDongle, VolumeMode) -> Unit = { _, _ -> }
) {
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                windowSizeClass = windowSizeClass,
                scrollBehavior = scrollBehavior,
                shouldShowActions = {
                    usbDongle != null &&
                        isUsbPermissionGranted &&
                        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                },
                usbDongle = usbDongle,
                onRefresh = onRefresh,
                onReset = onReset,
                onProfileExport = onProfileExport
            )
        },
        bottomBar = {
            if (usbDongle != null &&
                isUsbPermissionGranted &&
                windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded
            ) {
                MainBottomAppBar(
                    windowSizeClass = windowSizeClass,
                    usbDongle = usbDongle,
                    onRefresh = onRefresh,
                    onReset = onReset,
                    onProfileExport = onProfileExport
                )
            }
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
            AnimatedVisibility(visible = isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            val surfaceColor = MaterialTheme.colorScheme.surface
            val bottomAppBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                BottomAppBarDefaults.ContainerElevation
            )
            val overlappedFraction = if (scrollBehavior.state.overlappedFraction > 0.01f) 1f else 0f
            val animatedColor by animateColorAsState(
                targetValue = lerp(
                    surfaceColor,
                    bottomAppBarColor,
                    FastOutLinearInEasing.transform(overlappedFraction)
                ),
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "StatusBarColorAnimation"
            )
            LaunchedEffect(animatedColor) {
                systemUiController.setStatusBarColor(animatedColor)
            }
            if (!isDeviceAttached) {
                scrollBehavior.state.heightOffset = 0f
                scrollBehavior.state.contentOffset = 0f
                AttachUsbDongle()
            } else if (!isUsbPermissionGranted) {
                GrantUsbPermission(
                    isLoading = isLoading,
                    onPermissionRequest = onPermissionRequest
                )
            } else {
                var state by rememberSaveable { mutableIntStateOf(0) }
                MainTabRow(
                    selectedTabIndex = state,
                    containerColor = animatedColor,
                    onTabSelected = { index -> state = index }
                )
                if (state == 0) {
                    if (usbDongle is FiioKa5) {
                        FiioKa5Items(
                            modifier = Modifier.padding(
                                horizontal = windowSizeClass.getHorizontalPadding()
                            ),
                            fiioKa5 = usbDongle,
                            onChannelBalanceChanged = { channelBalance ->
                                onChannelBalanceChanged(usbDongle, channelBalance)
                            },
                            onChannelBalanceSelected = { channelBalance ->
                                onChannelBalanceSelected(usbDongle, channelBalance)
                            },
                            onVolumeLevelChanged = { volumeLevel ->
                                onVolumeLevelChanged(usbDongle, volumeLevel)
                            },
                            onVolumeLevelSelected = { volumeLevel ->
                                onVolumeLevelSelected(usbDongle, volumeLevel)
                            },
                            onVolumeModeSelected = { volumeMode ->
                                onVolumeModeSelected(usbDongle, volumeMode)
                            },
                            onDisplayBrightnessChanged = { displayBrightness ->
                                onDisplayBrightnessChanged(usbDongle, displayBrightness)
                            },
                            onDisplayBrightnessSelected = { displayBrightness ->
                                onDisplayBrightnessSelected(usbDongle, displayBrightness)
                            },
                            onDisplayTimeoutChanged = { displayTimeout ->
                                onDisplayTimeoutChanged(usbDongle, displayTimeout)
                            },
                            onDisplayTimeoutSelected = { displayTimeout ->
                                onDisplayTimeoutSelected(usbDongle, displayTimeout)
                            },
                            onDisplayInvertChange = { displayInvertEnabled ->
                                onDisplayInvertChange(usbDongle, displayInvertEnabled)
                            },
                            onGainSelected = { gain ->
                                onGainSelected(usbDongle, gain)
                            },
                            onFilterSelected = { filter ->
                                onFilterSelected(usbDongle, filter)
                            },
                            onSpdifOutEnabledSelected = { spdifOutEnabled ->
                                onSpdifOutEnabledSelected(usbDongle, spdifOutEnabled)
                            },
                            onHardwareMuteEnabledSelected = { hardwareMuteEnabled ->
                                onHardwareMuteEnabledSelected(usbDongle, hardwareMuteEnabled)
                            },
                            onDacModeSelected = { dacMode ->
                                onDacModeSelected(usbDongle, dacMode)
                            },
                            onHidModeSelected = { hidMode ->
                                onHidModeSelected(usbDongle, hidMode)
                            }
                        )
                    } else if (usbDongle is MoondropDawn44) {
                        MoondropDawn44Items(
                            modifier = Modifier.padding(
                                horizontal = windowSizeClass.getHorizontalPadding()
                            ),
                            moondropDawn44 = usbDongle,
                            onFilterSelected = { filter ->
                                onFilterSelected(usbDongle, filter)
                            },
                            onGainSelected = { gain ->
                                onGainSelected(usbDongle, gain)
                            },
                            onIndicatorStateSelected = { indicatorState ->
                                onIndicatorStateSelected(usbDongle, indicatorState)
                            }
                        )
                    }
                } else if (usbDongle != null) {
                    ProfileItems(
                        modifier = Modifier.padding(
                            horizontal = windowSizeClass.getHorizontalPadding()
                        ),
                        state = profileListState,
                        profiles = profiles,
                        onProfileShortcutAdd = onProfileShortcutAdd,
                        onProfileShortcutRemove = onProfileShortcutRemove,
                        onProfileDelete = onProfileDelete,
                        onProfileApply = { profile ->
                            onProfileApply(usbDongle, profile)
                        }
                    )
                }
                val context = (LocalContext.current as? Activity)
                if (context != null) {
                    LaunchedEffect(Unit) {
                        val profileShortcut = context.intent.consumeProfileShortcut()
                        if (profileShortcut != null) {
                            state = 1
                            val index = profiles.items.indexOf(profileShortcut)
                            profileListState.animateScrollToItem(if (index >= 0) index else 0)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttachUsbDongle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Cable,
                contentDescription = stringResource(id = R.string.cable),
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = stringResource(id = R.string.attach_device),
                modifier = Modifier.padding(all = 16.dp)
            )
        }
    }
}

@Composable
fun GrantUsbPermission(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onPermissionRequest: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onPermissionRequest,
            enabled = !isLoading
        ) {
            Text(text = stringResource(id = R.string.grant_permission))
        }
    }
}

@Composable
fun MainTabRow(
    selectedTabIndex: Int,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit
) {
    val titles = listOf(
        stringResource(id = R.string.state),
        stringResource(id = R.string.profiles)
    )
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
fun MainDropdownMenu(
    windowSizeClass: WindowSizeClass,
    usbDongle: UsbDongle?,
    onShouldShowMore: () -> Boolean,
    onDismissRequest: () -> Unit,
    onRefresh: () -> Unit,
    onReset: (UsbDongle) -> Unit,
    onProfileExport: (String, UsbDongle) -> Unit,
    modifier: Modifier = Modifier
) {
    var showExportProfile by rememberSaveable { mutableStateOf(false) }
    if (showExportProfile) {
        ExportProfileDialog(
            onDismiss = { showExportProfile = false },
            onConfirm = { profileName ->
                showExportProfile = false
                if (usbDongle != null) {
                    onProfileExport(profileName, usbDongle)
                }
            }
        )
    }
    DropdownMenu(
        expanded = onShouldShowMore(),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.refresh),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onDismissRequest()
                    onRefresh()
                }
            )
        }
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.state_reset),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = {
                onDismissRequest()
                if (usbDongle != null) {
                    onReset(usbDongle)
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.profile_export),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = {
                onDismissRequest()
                showExportProfile = true
            }
        )
    }
}

@Composable
fun MainTopAppBar(
    windowSizeClass: WindowSizeClass,
    scrollBehavior: TopAppBarScrollBehavior,
    shouldShowActions: () -> Boolean,
    usbDongle: UsbDongle?,
    onRefresh: () -> Unit,
    onReset: (UsbDongle) -> Unit,
    onProfileExport: (String, UsbDongle) -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = usbDongle?.productName() ?: stringResource(id = R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier.statusBarsPadding(),
        actions = {
            if (shouldShowActions()) {
                var showMore by rememberSaveable { mutableStateOf(false) }
                MainDropdownMenu(
                    windowSizeClass = windowSizeClass,
                    usbDongle = usbDongle,
                    onShouldShowMore = { showMore },
                    onDismissRequest = { showMore = false },
                    onRefresh = onRefresh,
                    onReset = onReset,
                    onProfileExport = onProfileExport
                )
                IconButton(onClick = { showMore = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(id = R.string.more)
                    )
                }
            }
        },
        scrollBehavior = if (usbDongle != null) scrollBehavior else null
    )
}

@Composable
fun MainBottomAppBar(
    windowSizeClass: WindowSizeClass,
    usbDongle: UsbDongle?,
    onRefresh: () -> Unit,
    onReset: (UsbDongle) -> Unit,
    onProfileExport: (String, UsbDongle) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        actions = {
            var showMore by rememberSaveable { mutableStateOf(false) }
            MainDropdownMenu(
                windowSizeClass = windowSizeClass,
                usbDongle = usbDongle,
                onShouldShowMore = { showMore },
                onDismissRequest = { showMore = false },
                onRefresh = onRefresh,
                onReset = onReset,
                onProfileExport = onProfileExport
            )
            IconButton(onClick = { showMore = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(id = R.string.more)
                )
            }
        },
        modifier = modifier.navigationBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRefresh,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(id = R.string.refresh)
                )
            }
        }
    )
}

@Preview(name = "No device")
@Composable
fun MainScreenPreview1() {
    MainScreen()
}

@Preview(name = "FiiO KA5 no permission")
@Composable
fun MainScreenPreview2() {
    MainScreen(
        usbDongle = FiioKa5(),
        isDeviceAttached = true
    )
}

@Preview(name = "FiiO KA5 compact")
@Composable
fun MainScreenPreview3() {
    MainScreen(
        usbDongle = FiioKa5(),
        isDeviceAttached = true,
        isUsbPermissionGranted = true
    )
}

@Preview(name = "FiiO KA5 expanded")
@Composable
fun MainScreenPreview4() {
    MainScreen(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero.copy(width = 940.dp)),
        usbDongle = FiioKa5(),
        isDeviceAttached = true,
        isUsbPermissionGranted = true
    )
}
