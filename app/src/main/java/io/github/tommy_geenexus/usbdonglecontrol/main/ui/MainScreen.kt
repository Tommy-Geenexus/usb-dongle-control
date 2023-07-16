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
import android.os.PersistableBundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_ACTION_SHORTCUT_PROFILE
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_ACTION_USB_PERMISSION
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_EXTRA_CONSUMED
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
import io.github.tommy_geenexus.usbdonglecontrol.getParcelableExtra2
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainSideEffect
import io.github.tommy_geenexus.usbdonglecontrol.main.business.MainViewModel
import io.github.tommy_geenexus.usbdonglecontrol.main.data.Profile
import io.github.tommy_geenexus.usbdonglecontrol.main.data.ProfilesList
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MainScreen(
    systemUiController: SystemUiController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) {
            viewModel.getCurrentState()
        }
    } else {
        null
    }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
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
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            MainSideEffect.NotificationService.Start -> {
                context.startService(Intent(context, UsbService::class.java))
            }
            MainSideEffect.NotificationService.Stop -> {
                context.stopService(Intent(context, UsbService::class.java))
            }
            MainSideEffect.Profile.Apply.Failure -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = profileApplyFail)
                }
            }
            MainSideEffect.Profile.Apply.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = profileApplySuccess)
                }
            }
            MainSideEffect.Profile.Delete.Failure -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = profileDeleteFail)
                }
            }
            MainSideEffect.Profile.Delete.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = profileDeleteSuccess)
                }
            }
            MainSideEffect.Profile.Export.Failure -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = profileExportFail)
                }
            }
            MainSideEffect.Profile.Export.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = profileExportSuccess)
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
            MainSideEffect.Shortcut.Add.Failure -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = shortcutAddFail)
                }
            }
            MainSideEffect.Shortcut.Add.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = shortcutAddSuccess)
                }
            }
            MainSideEffect.Shortcut.Delete.Failure -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = shortcutDeleteFail)
                }
            }
            MainSideEffect.Shortcut.Delete.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = shortcutDeleteSuccess)
                }
            }
        }
    }
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
    val state by viewModel.collectAsState()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val bottomAppBarColor =
        MaterialTheme.colorScheme.surfaceColorAtElevation(BottomAppBarDefaults.ContainerElevation)
    SideEffect {
        systemUiController.setNavigationBarColor(
            if (state.usbDongle != null && state.usbPermissionGranted) {
                bottomAppBarColor
            } else {
                surfaceColor
            }
        )
    }
    MainScreen(
        modifier = modifier,
        systemUiController = systemUiController,
        snackBarHostState = snackbarHostState,
        usbDongle = state.usbDongle,
        profiles = state.profiles,
        isLoading = state.isLoading,
        usbPermissionGranted = state.usbPermissionGranted,
        onPermissionRequest = { viewModel.requestUsbPermission() },
        onRefresh = { viewModel.getCurrentState() },
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
        onDisplayBrightnessSelected = { dongle, brightness ->
            if (dongle is FiioKa5) {
                viewModel.setDisplayBrightness(dongle, brightness.toInt())
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
        onDisplayTimeoutSelected = { dongle, timeout ->
            if (dongle is FiioKa5) {
                viewModel.setDisplayTimeout(dongle, timeout.toInt()) }
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
        onVolumeLevelSelected = { dongle, volumeLevel ->
            if (dongle is FiioKa5) {
                viewModel.setVolumeLevel(dongle, volumeLevel.toInt()) }
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
    systemUiController: SystemUiController = rememberSystemUiController(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
    profileListState: LazyListState = rememberLazyListState(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    usbDongle: UsbDongle? = null,
    profiles: ProfilesList = ProfilesList(),
    isLoading: Boolean = false,
    usbPermissionGranted: Boolean = false,
    onPermissionRequest: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onProfileShortcutAdd: (Profile) -> Unit = {},
    onProfileShortcutRemove: (Profile) -> Unit = {},
    onProfileDelete: (Profile) -> Unit = {},
    onProfileApply: (UsbDongle, Profile) -> Unit = { _, _ -> },
    onProfileExport: (String, UsbDongle) -> Unit = { _, _ -> },
    onChannelBalanceSelected: (UsbDongle, Int) -> Unit = { _, _ -> },
    onDacModeSelected: (UsbDongle, DacMode) -> Unit = { _, _ -> },
    onDisplayBrightnessSelected: (UsbDongle, Float) -> Unit = { _, _ -> },
    onDisplayTimeoutSelected: (UsbDongle, Float) -> Unit = { _, _ -> },
    onDisplayInvertChange: (UsbDongle, Boolean) -> Unit = { _, _ -> },
    onFilterSelected: (UsbDongle, Filter) -> Unit = { _, _ -> },
    onGainSelected: (UsbDongle, Gain) -> Unit = { _, _ -> },
    onHardwareMuteEnabledSelected: (UsbDongle, Boolean) -> Unit = { _, _ -> },
    onHidModeSelected: (UsbDongle, HidMode) -> Unit = { _, _ -> },
    onIndicatorStateSelected: (UsbDongle, IndicatorState) -> Unit = { _, _ -> },
    onSpdifOutEnabledSelected: (UsbDongle, Boolean) -> Unit = { _, _ -> },
    onVolumeLevelSelected: (UsbDongle, Float) -> Unit = { _, _ -> },
    onVolumeModeSelected: (UsbDongle, VolumeMode) -> Unit = { _, _ -> }
) {
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = usbDongle?.productName() ?: stringResource(id = R.string.app_name),
                        modifier = Modifier.padding(start = 16.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                scrollBehavior = if (usbDongle != null) scrollBehavior else null
            )
        },
        bottomBar = {
            if (usbDongle != null && usbPermissionGranted) {
                var showDialog by rememberSaveable { mutableStateOf(false) }
                if (showDialog) {
                    ExportProfileDialog(
                        onDismiss = { showDialog = false },
                        onConfirm = { profileName ->
                            showDialog = false
                            onProfileExport(profileName, usbDongle)
                        }
                    )
                }
                var showMore by rememberSaveable { mutableStateOf(false) }
                DropdownMenu(
                    expanded = showMore,
                    onDismissRequest = { showMore = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.profile_export),
                                fontWeight = FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        onClick = {
                            showMore = false
                            showDialog = true
                        }
                    )
                }
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { showMore = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.navigationBarsPadding(),
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
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
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
            if (usbDongle == null) {
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
                val surfaceColor = MaterialTheme.colorScheme.surface
                val bottomAppBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    BottomAppBarDefaults.ContainerElevation
                )
                val overlappedFraction = if (topAppBarState.overlappedFraction > 0.01f) 1f else 0f
                val animatedColor by animateColorAsState(
                    targetValue = lerp(
                        surfaceColor,
                        bottomAppBarColor,
                        FastOutLinearInEasing.transform(overlappedFraction)
                    ),
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
                LaunchedEffect(animatedColor) {
                    systemUiController.setStatusBarColor(animatedColor)
                }
                var state by remember { mutableStateOf(0) }
                val titles = listOf(
                    stringResource(id = R.string.state),
                    stringResource(id = R.string.profiles)
                )
                TabRow(
                    selectedTabIndex = state,
                    containerColor = animatedColor
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
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
                if (state == 0) {
                    if (usbDongle is FiioKa5) {
                        FiioKa5Items(
                            fiioKa5 = usbDongle,
                            onChannelBalanceSelected = { channelBalance ->
                                onChannelBalanceSelected(usbDongle, channelBalance.toInt())
                            },
                            onVolumeLevelSelected = { volumeLevel ->
                                onVolumeLevelSelected(usbDongle, volumeLevel)
                            },
                            onVolumeModeSelected = { volumeMode ->
                                onVolumeModeSelected(usbDongle, volumeMode)
                            },
                            onDisplayBrightnessSelected = { displayBrightness ->
                                onDisplayBrightnessSelected(usbDongle, displayBrightness)
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
                } else {
                    ProfileItems(
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
                val context = (LocalContext.current as Activity)
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

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}

private fun Intent.consumeProfileShortcut(): Profile? {
    if (hasExtra(INTENT_EXTRA_CONSUMED)) {
        return null
    }
    val bundle = getParcelableExtra2<PersistableBundle>(INTENT_ACTION_SHORTCUT_PROFILE)
    return if (bundle != null) {
        putExtra(INTENT_EXTRA_CONSUMED, true)
        Profile.fromPersistableBundle(bundle)
    } else {
        null
    }
}
