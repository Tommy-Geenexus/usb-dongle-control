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

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.github.tommygeenexus.usbdonglecontrol.control.ui.ControlScreen
import io.github.tommygeenexus.usbdonglecontrol.setup.ui.SetupScreen

private const val TRANSITION_DURATION = 300

@Composable
fun NavGraph(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestinations.ROUTE_SETUP
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = NavDestinations.ROUTE_SETUP,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = TRANSITION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = TRANSITION_DURATION)
                )
            }
        ) {
            SetupScreen(
                viewModel = hiltViewModel(),
                onNavigateToControl = {
                    navController.popBackStack()
                    navController.navigate(
                        route = NavDestinations.ROUTE_CONTROL,
                        navOptions = navOptions {
                            popUpTo(route = NavDestinations.ROUTE_SETUP) {
                                inclusive = true
                            }
                        }
                    )
                }
            )
        }
        composable(
            route = NavDestinations.ROUTE_CONTROL,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = TRANSITION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = TRANSITION_DURATION)
                )
            }
        ) {
            ControlScreen(
                windowSizeClass = windowSizeClass,
                viewModel = hiltViewModel(),
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}
