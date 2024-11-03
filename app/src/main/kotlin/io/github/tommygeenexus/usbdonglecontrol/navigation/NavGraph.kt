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

package io.github.tommygeenexus.usbdonglecontrol.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.github.tommygeenexus.usbdonglecontrol.control.ui.ControlScreen
import io.github.tommygeenexus.usbdonglecontrol.settings.ui.SettingsScreen
import io.github.tommygeenexus.usbdonglecontrol.setup.ui.SetupScreen
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
fun NavGraph(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: NavDestinations = NavDestinations.Setup
) {
    val slideDistance = rememberSlideDistance()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<NavDestinations.Setup>(
            enterTransition = {
                materialSharedAxisXIn(forward = true, slideDistance = slideDistance)
            },
            exitTransition = {
                materialSharedAxisXOut(forward = false, slideDistance = slideDistance)
            }
        ) {
            SetupScreen(
                viewModel = hiltViewModel(),
                onNavigateToControl = {
                    navController.popBackStack()
                    navController.navigate(
                        route = NavDestinations.Control,
                        navOptions = navOptions {
                            popUpTo(route = NavDestinations.Setup) {
                                inclusive = true
                            }
                        }
                    )
                }
            )
        }
        composable<NavDestinations.Control>(
            enterTransition = {
                if (initialState
                        .destination
                        .route
                        ?.endsWith(NavDestinations.Setup.toString()) == true
                ) {
                    materialSharedAxisXIn(forward = true, slideDistance = slideDistance)
                } else {
                    materialSharedAxisZIn(forward = true)
                }
            },
            exitTransition = {
                materialSharedAxisZOut(forward = false)
            }
        ) {
            ControlScreen(
                windowSizeClass = windowSizeClass,
                viewModel = hiltViewModel(),
                onNavigateToSettings = {
                    navController.navigate(NavDestinations.Settings)
                },
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
        composable<NavDestinations.Settings>(
            enterTransition = {
                materialSharedAxisZIn(forward = true)
            },
            exitTransition = {
                materialSharedAxisZOut(forward = false)
            }
        ) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}
