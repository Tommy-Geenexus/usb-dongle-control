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

package io.github.tommygeenexus.usbdonglecontrol.control.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.hardware.usb.UsbManager
import android.os.Build
import android.view.accessibility.AccessibilityManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.control.business.ControlSideEffect
import io.github.tommygeenexus.usbdonglecontrol.control.business.ControlViewModel
import io.github.tommygeenexus.usbdonglecontrol.core.control.ControlTabs
import io.github.tommygeenexus.usbdonglecontrol.core.db.Profile
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.moonriver2ti.MoondropMoonriver2Ti
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.productName
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.profileFlow
import io.github.tommygeenexus.usbdonglecontrol.core.extension.consumeProfileShortcut
import io.github.tommygeenexus.usbdonglecontrol.core.receiver.UsbDeviceAttachDetachPermissionReceiver
import io.github.tommygeenexus.usbdonglecontrol.core.receiver.UsbServiceVolumeLevelChangedReceiver
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka13.ui.FiioKa13Items
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.ui.FiioKa5Items
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.ui.MoondropDawnItems
import io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.moonriver2ti.ui.MoondropMoonriver2TiItems
import io.github.tommygeenexus.usbdonglecontrol.navigation.NavDestinations
import io.github.tommygeenexus.usbdonglecontrol.theme.getHorizontalCardPadding
import io.github.tommygeenexus.usbdonglecontrol.volume.ui.UsbService
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ControlScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: ControlViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToSetup: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val maximizeVolumeFailed = stringResource(id = R.string.maximize_volume_failed)
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
    val usbCommunicationFailure = stringResource(id = R.string.usb_comm_failure)
    val usbCommunicationSuccess = stringResource(id = R.string.usb_comm_success)
    val profiles = viewModel.profileFlow.collectAsLazyPagingItems()
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            ControlSideEffect.MaximizeVolume.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = maximizeVolumeFailed,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Profile.Apply.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileApplyFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Profile.Apply.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileApplySuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Profile.Delete.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileDeleteFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Profile.Delete.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileDeleteSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Profile.Export.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileExportFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Profile.Export.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = profileExportSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            is ControlSideEffect.Profile.Get.All -> {
                profiles.refresh()
            }
            ControlSideEffect.Service.Start -> {
                context.startService(Intent(context, UsbService::class.java))
            }
            ControlSideEffect.Service.Stop -> {
                context.stopService(Intent(context, UsbService::class.java))
            }
            ControlSideEffect.Shortcut.Add.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutAddFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Shortcut.Add.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutAddSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Shortcut.Delete.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutDeleteFail,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.Shortcut.Delete.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = shortcutDeleteSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.UsbCommunication.Get.Failure -> {
                onNavigateToSetup()
            }
            is ControlSideEffect.UsbCommunication.Get.Success -> {
                viewModel.getCurrentStateForUsbDongle(usbDongle = sideEffect.usbDongle)
            }
            ControlSideEffect.UsbCommunication.Rw.Failure -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = usbCommunicationFailure,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ControlSideEffect.UsbCommunication.Rw.Success -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = usbCommunicationSuccess,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    val isTouchExplorationEnabled = remember {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        am.isEnabled && am.isTouchExplorationEnabled
    }
    val topScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val bottomScrollBehavior = if (!isTouchExplorationEnabled) {
        BottomAppBarDefaults.exitAlwaysScrollBehavior()
    } else {
        null
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navController.currentBackStackEntryFlow.collect { navBackStackEntry ->
                if (navBackStackEntry
                        .destination
                        .route
                        ?.endsWith(NavDestinations.Control.toString()) == true
                ) {
                    viewModel.verifyUsbDongleIsAttachedAndUsbPermissionIsGranted()
                }
            }
        }
    }
    DisposableEffect(lifecycleOwner) {
        val usbReceiver = UsbDeviceAttachDetachPermissionReceiver(
            onAttachedDevicesChanged = { isAttached ->
                if (!isAttached) {
                    topScrollBehavior.state.heightOffset = 0f
                    topScrollBehavior.state.contentOffset = 0f
                    bottomScrollBehavior?.state?.heightOffset = 0f
                    bottomScrollBehavior?.state?.contentOffset = 0f
                    onNavigateToSetup()
                }
            }
        )
        val usbServiceVolumeLevelChangedReceiver = UsbServiceVolumeLevelChangedReceiver(
            onVolumeLevelChanged = { usbDongle ->
                viewModel.synchronizeVolumeLevel(usbDongle)
            }
        )
        ContextCompat.registerReceiver(
            context,
            usbReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        ContextCompat.registerReceiver(
            context,
            usbServiceVolumeLevelChangedReceiver,
            IntentFilter(UsbService.INTENT_ACTION_VOLUME_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            context.unregisterReceiver(usbReceiver)
            context.unregisterReceiver(usbServiceVolumeLevelChangedReceiver)
        }
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val activity = LocalContext.current as Activity
        val bottomAppBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
        SideEffect {
            @Suppress("DEPRECATION")
            activity.window?.navigationBarColor = bottomAppBarColor
        }
    }
    val state by viewModel.collectAsState()
    ControlScreen(
        windowSizeClass = windowSizeClass,
        topScrollBehavior = topScrollBehavior,
        bottomScrollBehavior = bottomScrollBehavior,
        snackBarHostState = snackBarHostState,
        profiles = profiles,
        usbDongle = state.usbDongle,
        isLoading = state.loadingTasks > 0.toUInt(),
        onNavigateToSettings = onNavigateToSettings,
        onRefresh = { viewModel.getCurrentStateForUsbDongle(state.usbDongle) },
        onReset = {
            viewModel.setProfile(state.usbDongle.defaultStateAsProfile())
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
        onProfileApply = { profile ->
            viewModel.setProfile(profile)
        },
        onProfileExport = { profileName ->
            viewModel.exportProfile(state.usbDongle.currentStateAsProfile(profileName))
        },
        onChannelBalanceSelected = { channelBalance ->
            viewModel.setChannelBalance(channelBalance)
        },
        onDacModeSelected = { dacModeId ->
            viewModel.setDacMode(dacModeId)
        },
        onDisplayBrightnessSelected = { displayBrightness ->
            viewModel.setDisplayBrightness(displayBrightness)
        },
        onDisplayInvertChange = { isDisplayInvertEnabled ->
            viewModel.setDisplayInvertEnabled(isDisplayInvertEnabled)
        },
        onDisplayTimeoutSelected = { displayTimeout ->
            viewModel.setDisplayTimeout(displayTimeout)
        },
        onFilterSelected = { filterId ->
            viewModel.setDacFilter(filterId)
        },
        onGainSelected = { gainId ->
            viewModel.setGain(gainId)
        },
        onHardwareMuteEnabledSelected = { isHardwareMuteEnabled ->
            viewModel.setHardwareMuteEnabled(isHardwareMuteEnabled)
        },
        onHidModeSelected = { hidModeId ->
            viewModel.setHidMode(hidModeId)
        },
        onIndicatorStateSelected = { indicatorStateId ->
            viewModel.setIndicatorState(indicatorStateId)
        },
        onSpdifOutEnabledSelected = { isSpdifOutEnabled ->
            viewModel.setSpdifOutEnabled(isSpdifOutEnabled)
        },
        onVolumeLevelSelected = { volumeLevel ->
            viewModel.setVolumeLevel(volumeLevel)
        },
        onVolumeModeSelected = { volumeModeId ->
            viewModel.setVolumeMode(volumeModeId)
        }
    )
}

@Composable
fun ControlScreen(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    topScrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    bottomScrollBehavior: BottomAppBarScrollBehavior? = null,
    profileListState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    profiles: LazyPagingItems<Profile> =
        UnsupportedUsbDongle.profileFlow().collectAsLazyPagingItems(),
    usbDongle: UsbDongle = UnsupportedUsbDongle,
    isLoading: Boolean = false,
    onNavigateToSettings: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onReset: () -> Unit = {},
    onProfileShortcutAdd: (Profile) -> Unit = {},
    onProfileShortcutRemove: (Profile) -> Unit = {},
    onProfileDelete: (Profile) -> Unit = { _ -> },
    onProfileApply: (Profile) -> Unit = { _ -> },
    onProfileExport: (String) -> Unit = { _ -> },
    onChannelBalanceSelected: (Int) -> Unit = { _ -> },
    onDacModeSelected: (Byte) -> Unit = { _ -> },
    onDisplayBrightnessSelected: (Int) -> Unit = { _ -> },
    onDisplayTimeoutSelected: (Int) -> Unit = { _ -> },
    onDisplayInvertChange: (Boolean) -> Unit = { _ -> },
    onFilterSelected: (Byte) -> Unit = { _ -> },
    onGainSelected: (Byte) -> Unit = { _ -> },
    onHardwareMuteEnabledSelected: (Boolean) -> Unit = { _ -> },
    onHidModeSelected: (Byte) -> Unit = { _ -> },
    onIndicatorStateSelected: (Byte) -> Unit = { _ -> },
    onSpdifOutEnabledSelected: (Boolean) -> Unit = { _ -> },
    onVolumeLevelSelected: (Int) -> Unit = { _ -> },
    onVolumeModeSelected: (Byte) -> Unit = { _ -> }
) {
    Scaffold(
        modifier = if (bottomScrollBehavior != null) {
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                modifier
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .nestedScroll(topScrollBehavior.nestedScrollConnection)
                    .nestedScroll(bottomScrollBehavior.nestedScrollConnection)
            } else {
                modifier
                    .nestedScroll(topScrollBehavior.nestedScrollConnection)
                    .nestedScroll(bottomScrollBehavior.nestedScrollConnection)
            }
        } else if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            modifier
                .windowInsetsPadding(WindowInsets.displayCutout)
                .nestedScroll(topScrollBehavior.nestedScrollConnection)
        } else {
            modifier.nestedScroll(topScrollBehavior.nestedScrollConnection)
        },
        topBar = {
            ControlTopAppBar(
                windowInsets = WindowInsets.statusBars,
                productName = usbDongle.productName(),
                windowSizeClass = windowSizeClass,
                scrollBehavior = topScrollBehavior,
                onRefresh = onRefresh,
                onReset = onReset,
                onProfileExport = onProfileExport,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        bottomBar = {
            if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
                ControlBottomAppBar(
                    windowSizeClass = windowSizeClass,
                    scrollBehavior = bottomScrollBehavior,
                    onRefresh = onRefresh,
                    onReset = onReset,
                    onProfileExport = onProfileExport,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
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
            val surfaceColor = MaterialTheme.colorScheme.surface
            val bottomAppBarColor = MaterialTheme.colorScheme.surfaceContainer
            val overlappedFraction = if (topScrollBehavior.state.overlappedFraction > 0.01f) {
                1f
            } else {
                0f
            }
            val animatedColor by animateColorAsState(
                targetValue = lerp(
                    surfaceColor,
                    bottomAppBarColor,
                    FastOutLinearInEasing.transform(overlappedFraction)
                ),
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "StatusBarColorAnimation"
            )
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                val activity = LocalContext.current as? Activity
                if (activity != null) {
                    LaunchedEffect(animatedColor) {
                        @Suppress("DEPRECATION")
                        activity.window?.statusBarColor = animatedColor.toArgb()
                    }
                }
            }
            var selectedTabIndex by remember { mutableIntStateOf(ControlTabs.State.index) }
            ControlTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = animatedColor,
                onTabSelected = { index ->
                    val prev = selectedTabIndex
                    selectedTabIndex = index
                    if (prev == ControlTabs.Profiles.index &&
                        selectedTabIndex == ControlTabs.State.index
                    ) {
                        onRefresh()
                    }
                }
            )
            if (selectedTabIndex == ControlTabs.State.index) {
                when (usbDongle) {
                    is FiioKa13 -> {
                        FiioKa13Items(
                            modifier = Modifier.padding(
                                horizontal = windowSizeClass.getHorizontalCardPadding()
                            ),
                            fiioKa13 = usbDongle,
                            onFilterSelected = { filterId ->
                                onFilterSelected(filterId)
                            },
                            onIndicatorStateSelected = { indicatorStateId ->
                                onIndicatorStateSelected(indicatorStateId)
                            },
                            onSpdifOutSelected = { isSpdifOutEnabled ->
                                onSpdifOutEnabledSelected(isSpdifOutEnabled)
                            },
                            onVolumeLevelSelected = { volumeLevel ->
                                onVolumeLevelSelected(volumeLevel)
                            }
                        )
                    }
                    is FiioKa5 -> {
                        FiioKa5Items(
                            modifier = Modifier.padding(
                                horizontal = windowSizeClass.getHorizontalCardPadding()
                            ),
                            fiioKa5 = usbDongle,
                            onChannelBalanceSelected = { channelBalance ->
                                onChannelBalanceSelected(channelBalance)
                            },
                            onDisplayBrightnessSelected = { displayBrightness ->
                                onDisplayBrightnessSelected(displayBrightness)
                            },
                            onDisplayInvertSelected = { isDisplayInvertEnabled ->
                                onDisplayInvertChange(isDisplayInvertEnabled)
                            },
                            onDisplayTimeoutSelected = { displayTimeout ->
                                onDisplayTimeoutSelected(displayTimeout)
                            },
                            onDacModeSelected = { dacModeId ->
                                onDacModeSelected(dacModeId)
                            },
                            onFilterSelected = { filterId ->
                                onFilterSelected(filterId)
                            },
                            onGainSelected = { gainId ->
                                onGainSelected(gainId)
                            },
                            onHardwareMuteSelected = { isHardwareMuteEnabled ->
                                onHardwareMuteEnabledSelected(isHardwareMuteEnabled)
                            },
                            onHidModeSelected = { hidModeId ->
                                onHidModeSelected(hidModeId)
                            },
                            onSpdifOutSelected = { isSpdifOutEnabled ->
                                onSpdifOutEnabledSelected(isSpdifOutEnabled)
                            },
                            onVolumeLevelSelected = { volumeLevel ->
                                onVolumeLevelSelected(volumeLevel)
                            },
                            onVolumeModeSelected = { volumeModeId ->
                                onVolumeModeSelected(volumeModeId)
                            }
                        )
                    }
                    is MoondropDawn -> {
                        MoondropDawnItems(
                            modifier = Modifier.padding(
                                horizontal = windowSizeClass.getHorizontalCardPadding()
                            ),
                            moondropDawn = usbDongle,
                            onFilterSelected = { filterId ->
                                onFilterSelected(filterId)
                            },
                            onGainSelected = { gainId ->
                                onGainSelected(gainId)
                            },
                            onIndicatorStateSelected = { indicatorStateId ->
                                onIndicatorStateSelected(indicatorStateId)
                            },
                            onVolumeLevelSelected = { volumeLevel ->
                                onVolumeLevelSelected(volumeLevel)
                            }
                        )
                    }
                    is MoondropMoonriver2Ti -> {
                        MoondropMoonriver2TiItems(
                            modifier = Modifier.padding(
                                horizontal = windowSizeClass.getHorizontalCardPadding()
                            ),
                            moondropMoonriver2Ti = usbDongle,
                            onFilterSelected = { filterId ->
                                onFilterSelected(filterId)
                            },
                            onGainSelected = { gainId ->
                                onGainSelected(gainId)
                            },
                            onIndicatorStateSelected = { indicatorStateId ->
                                onIndicatorStateSelected(indicatorStateId)
                            },
                            onVolumeLevelSelected = { volumeLevel ->
                                onVolumeLevelSelected(volumeLevel)
                            }
                        )
                    }
                }
            } else {
                ProfileItems(
                    modifier = Modifier.padding(
                        horizontal = windowSizeClass.getHorizontalCardPadding()
                    ),
                    state = profileListState,
                    profiles = profiles,
                    onProfileShortcutAdd = onProfileShortcutAdd,
                    onProfileShortcutRemove = onProfileShortcutRemove,
                    onProfileDelete = { profile ->
                        onProfileDelete(profile)
                    },
                    onProfileApply = { profile ->
                        onProfileApply(profile)
                    }
                )
            }
            val context = (LocalContext.current as? Activity)
            if (context != null) {
                val profileItems = profiles.itemSnapshotList.items
                LaunchedEffect(profileItems) {
                    if (profileItems.isNotEmpty()) {
                        val profileShortcut = context.intent.consumeProfileShortcut()
                        if (profileShortcut != null) {
                            selectedTabIndex = ControlTabs.Profiles.index
                            val index = profileItems.indexOf(profileShortcut)
                            profileListState.animateScrollToItem(
                                index = if (index >= 0) {
                                    index
                                } else {
                                    profileItems.lastIndex
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
