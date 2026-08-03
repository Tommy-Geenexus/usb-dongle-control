/*
 * Copyright (c) 2026, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.github.tommygeenexus.usbdonglecontrol.core.window.getWindowWidthSizeClass
import io.github.tommygeenexus.usbdonglecontrol.navigation.entry.controlEntry
import io.github.tommygeenexus.usbdonglecontrol.navigation.entry.settingsEntry
import io.github.tommygeenexus.usbdonglecontrol.navigation.entry.setupEntry

@Composable
fun UsbDongleControlNavigationDisplay(
    modifier: Modifier = Modifier,
    startRoute: NavKey = NavigationRoute.Setup
) {
    val navigationState = rememberNavigationState(startRoute = startRoute)
    val navigator = remember { Navigator(navigationState) }
    val windowWidthSizeClass = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true)
        .windowSizeClass
        .getWindowWidthSizeClass()
    val entryProvider = entryProvider {
        setupEntry(
            onNavigateToControl = {
                navigator.clearBackStackAndNavigate(route = NavigationRoute.Control)
            }
        )
        controlEntry(
            windowWidthSizeClass = windowWidthSizeClass,
            onNavigateToSettings = {
                navigator.navigate(route = NavigationRoute.Settings)
            },
            onNavigateToSetup = {
                navigator.clearBackStackAndNavigate(route = NavigationRoute.Setup)
            }
        )
        settingsEntry(
            windowWidthSizeClass = windowWidthSizeClass,
            onNavigateUp = {
                navigator.goBack()
            }
        )
    }
    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        modifier = modifier,
        transitionSpec = {
            val initialKey = initialState.entries.lastOrNull()?.contentKey
            val targetKey = targetState.entries.lastOrNull()?.contentKey
            when (initialKey to targetKey) {
                NavigationRoute.Setup.contentKey to NavigationRoute.Control.contentKey -> {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    ).togetherWith(
                        exit = slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    )
                }

                NavigationRoute.Control.contentKey to NavigationRoute.Setup.contentKey -> {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    ).togetherWith(
                        exit = slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    )
                }

                else -> {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up
                    ).togetherWith(
                        exit = slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down
                        )
                    )
                }
            }
        },
        popTransitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            ).togetherWith(
                exit = slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            )
        },
        onBack = {
            navigator.goBack()
        }
    )
}
